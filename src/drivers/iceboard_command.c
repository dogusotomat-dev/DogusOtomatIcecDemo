#include "iceboard.h"
#include "iceboard_parse.h"
#include "../utils/logger.h"
#include "uart.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif

// Default timeout and retry values
#define ICEBOARD_DEFAULT_TIMEOUT_MS 2000
#define ICEBOARD_DEFAULT_RETRY_COUNT 3

static int iceboard_initialized = 0;

// CRC calculation function for data integrity
static uint8_t calculate_crc(const uint8_t* data, uint16_t length) {
    uint8_t crc = 0;
    for (uint16_t i = 0; i < length; i++) {
        crc ^= data[i];
    }
    return crc;
}

// Send a command and wait for ACK/NACK response
iceboard_status_t iceboard_send_command_with_retry(const iceboard_command_t* cmd, uint8_t* response, uint16_t response_length) {
    if (!iceboard_initialized) {
        LOG_ERROR("Iceboard not initialized");
        return ICEBOARD_STATUS_ERROR;
    }
    
    if (cmd == NULL) {
        LOG_ERROR("Invalid command parameter");
        return ICEBOARD_STATUS_ERROR;
    }
    
    // Prepare command packet with header and CRC
    uint8_t packet[8]; // Header (1) + Command (1) + Data (3) + CRC (1) + Footer (2)
    packet[0] = 0xAA; // Header
    packet[1] = cmd->command;
    packet[2] = cmd->data[0];
    packet[3] = cmd->data[1];
    packet[4] = cmd->data[2];
    
    // Calculate CRC over command and data
    uint8_t crc = calculate_crc(&packet[1], 4);
    packet[5] = crc;
    
    // Footer
    packet[6] = 0x0D; // CR
    packet[7] = 0x0A; // LF
    
    LOG_DEBUG("Sending command 0x%02X with data: 0x%02X 0x%02X 0x%02X", 
              cmd->command, cmd->data[0], cmd->data[1], cmd->data[2]);
    
    // Retry mechanism
    for (int retry = 0; retry < ICEBOARD_DEFAULT_RETRY_COUNT; retry++) {
        LOG_DEBUG("Attempt %d/%d", retry + 1, ICEBOARD_DEFAULT_RETRY_COUNT);
        
        // Send command packet
        uart_error_t uart_result = uart_send_buffer(packet, sizeof(packet));
        if (uart_result != UART_SUCCESS) {
            LOG_ERROR("Failed to send command packet: %d", uart_result);
            continue; // Try again
        }
        
        // Wait for response with timeout
        uint8_t ack_byte;
        uart_result = uart_receive_byte(&ack_byte);
        if (uart_result == UART_ERROR_TIMEOUT) {
            LOG_WARN("Timeout waiting for response (attempt %d)", retry + 1);
            continue; // Try again
        } else if (uart_result != UART_SUCCESS) {
            LOG_ERROR("Failed to receive response: %d", uart_result);
            continue; // Try again
        }
        
        // Check ACK/NACK
        if (ack_byte == ICEBOARD_ACK) {
            LOG_DEBUG("ACK received");
            
            // If response buffer is provided, receive the full response
            if (response != NULL && response_length > 0) {
                uint16_t received_length;
                uart_result = uart_receive_buffer(response, response_length, &received_length);
                if (uart_result != UART_SUCCESS) {
                    LOG_ERROR("Failed to receive full response: %d", uart_result);
                    continue; // Try again
                }
                
                // Parse and log the response
                iceboard_response_t parsed_response;
                iceboard_status_t parse_result = parse_iceboard_response(response, received_length, &parsed_response);
                if (parse_result == ICEBOARD_STATUS_OK) {
                    switch (parsed_response.type) {
                        case ICEBOARD_RESPONSE_STATUS:
                            LOG_INFO("Received STATUS response - Slot: %d, Data Length: %d", 
                                    parsed_response.slot_number, parsed_response.data_length);
                            break;
                        case ICEBOARD_RESPONSE_ACK:
                            LOG_DEBUG("Received additional ACK response");
                            break;
                        case ICEBOARD_RESPONSE_UNKNOWN:
                            LOG_WARN("Received unknown response type");
                            break;
                        default:
                            LOG_DEBUG("Received response type: %d", parsed_response.type);
                            break;
                    }
                } else if (parse_result == ICEBOARD_STATUS_NACK_RECEIVED) {
                    LOG_WARN("Received NACK in response data - Error Code: %d, Message: %s", 
                            parsed_response.error_code, parsed_response.error_message);
                    return ICEBOARD_STATUS_NACK_RECEIVED;
                } else if (parse_result == ICEBOARD_STATUS_ERROR) {
                    LOG_ERROR("Received ERROR response - Code: %d, Slot: %d, Message: %s", 
                            parsed_response.error_code, parsed_response.slot_number, parsed_response.error_message);
                    return ICEBOARD_STATUS_ERROR;
                }
                
                // Validate response CRC if applicable
                if (received_length > 1) {
                    uint8_t response_crc = response[received_length - 1];
                    uint8_t calculated_crc = calculate_crc(response, received_length - 1);
                    if (response_crc != calculated_crc) {
                        LOG_WARN("Response CRC mismatch (attempt %d)", retry + 1);
                        continue; // Try again
                    }
                }
            }
            
            return ICEBOARD_STATUS_OK;
        } else if (ack_byte == ICEBOARD_NACK) {
            LOG_WARN("NACK received (attempt %d)", retry + 1);
            
            // Receive NACK reason if available
            uint8_t nack_data[32];
            uint16_t nack_length = 0;
            
            // Try to receive additional NACK data
            for (int i = 0; i < sizeof(nack_data); i++) {
                uint8_t byte;
                uart_error_t result = uart_receive_byte(&byte);
                if (result == UART_SUCCESS) {
                    nack_data[nack_length++] = byte;
                } else {
                    break; // Stop if we can't receive more data
                }
            }
            
            // Parse NACK response
            if (nack_length > 0) {
                iceboard_response_t parsed_response;
                iceboard_status_t parse_result = parse_iceboard_response(nack_data, nack_length, &parsed_response);
                if (parse_result == ICEBOARD_STATUS_NACK_RECEIVED && parsed_response.error_code != ICEBOARD_ERROR_NONE) {
                    LOG_WARN("NACK reason - Error Code: %d, Message: %s", 
                            parsed_response.error_code, parsed_response.error_message);
                }
            }
            
            // Don't retry on NACK, return specific error
            return ICEBOARD_STATUS_NACK_RECEIVED;
        } else {
            LOG_WARN("Invalid response received: 0x%02X (attempt %d)", ack_byte, retry + 1);
            
            // Try to receive more data to understand the response
            uint8_t extra_data[32];
            uint16_t extra_length = 0;
            extra_data[0] = ack_byte; // Include the first byte
            extra_length = 1;
            
            // Try to receive additional data
            for (int i = 1; i < sizeof(extra_data); i++) {
                uint8_t byte;
                uart_error_t result = uart_receive_byte(&byte);
                if (result == UART_SUCCESS) {
                    extra_data[extra_length++] = byte;
                } else {
                    break; // Stop if we can't receive more data
                }
            }
            
            // Parse the response to understand what we got
            iceboard_response_t parsed_response;
            iceboard_status_t parse_result = parse_iceboard_response(extra_data, extra_length, &parsed_response);
            if (parse_result == ICEBOARD_STATUS_OK && parsed_response.type == ICEBOARD_RESPONSE_UNKNOWN) {
                LOG_WARN("Unknown response type with %d bytes of data", extra_length);
            }
            
            continue; // Try again
        }
    }
    
    // If we get here, all retries have failed
    LOG_ERROR("Command failed after %d attempts", ICEBOARD_DEFAULT_RETRY_COUNT);
    return ICEBOARD_STATUS_TIMEOUT;
}