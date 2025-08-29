#include <stdio.h>
#include <string.h>
#include "drivers/iceboard_parse.h"
#include "utils/logger.h"

int main(void) {
    // Initialize logger
    logger_config_t logger_config = {
        .level = LOG_LEVEL_DEBUG,
        .output = LOG_OUTPUT_CONSOLE,
        .file_path = "iceboard_parse_test.log"
    };
    
    if (logger_init(&logger_config) != 0) {
        fprintf(stderr, "Failed to initialize logger\n");
        return 1;
    }
    
    LOG_INFO("Iceboard Response Parser Test Started");
    
    // Test ACK response
    LOG_INFO("Testing ACK response");
    uint8_t ack_response[] = {0x06}; // ACK
    iceboard_response_t parsed_response;
    iceboard_status_t result = parse_iceboard_response(ack_response, sizeof(ack_response), &parsed_response);
    
    if (result == ICEBOARD_STATUS_OK && parsed_response.type == ICEBOARD_RESPONSE_ACK) {
        LOG_INFO("ACK response parsed correctly");
    } else {
        LOG_ERROR("Failed to parse ACK response");
    }
    
    // Test NACK response with error code
    LOG_INFO("Testing NACK response with error code");
    uint8_t nack_response[] = {0x15, 0x03}; // NACK with error code 3
    result = parse_iceboard_response(nack_response, sizeof(nack_response), &parsed_response);
    
    if (result == ICEBOARD_STATUS_NACK_RECEIVED && parsed_response.type == ICEBOARD_RESPONSE_NACK) {
        LOG_INFO("NACK response parsed correctly, error code: %d", parsed_response.error_code);
    } else {
        LOG_ERROR("Failed to parse NACK response");
    }
    
    // Test NACK response with error code and message
    LOG_INFO("Testing NACK response with error code and message");
    uint8_t nack_response_with_msg[] = {0x15, 0x02, 'B', 'u', 's', 'y'}; // NACK with error code 2 and message "Busy"
    result = parse_iceboard_response(nack_response_with_msg, sizeof(nack_response_with_msg), &parsed_response);
    
    if (result == ICEBOARD_STATUS_NACK_RECEIVED && parsed_response.type == ICEBOARD_RESPONSE_NACK) {
        LOG_INFO("NACK response with message parsed correctly, error code: %d, message: %s", 
                parsed_response.error_code, parsed_response.error_message);
    } else {
        LOG_ERROR("Failed to parse NACK response with message");
    }
    
    // Test STATUS response with slot number and data
    LOG_INFO("Testing STATUS response with slot number and data");
    uint8_t status_response[] = {0x01, 0x05, 0x10, 0x20, 0x30}; // STATUS, slot 5, data [0x10, 0x20, 0x30]
    result = parse_iceboard_response(status_response, sizeof(status_response), &parsed_response);
    
    if (result == ICEBOARD_STATUS_OK && parsed_response.type == ICEBOARD_RESPONSE_STATUS) {
        LOG_INFO("STATUS response parsed correctly, slot: %d, data length: %d", 
                parsed_response.slot_number, parsed_response.data_length);
        LOG_DEBUG("Data: ");
        for (int i = 0; i < parsed_response.data_length; i++) {
            LOG_DEBUG("  [%d]: 0x%02X", i, parsed_response.data[i]);
        }
    } else {
        LOG_ERROR("Failed to parse STATUS response");
    }
    
    // Test ERROR response with error code, slot, and message
    LOG_INFO("Testing ERROR response with error code, slot, and message");
    uint8_t error_response[] = {0x02, 0x04, 0x03, 'H', 'a', 'r', 'd', 'w', 'a', 'r', 'e', ' ', 'f', 'a', 'u', 'l', 't'}; 
    // ERROR, error code 4, slot 3, message "Hardware fault"
    result = parse_iceboard_response(error_response, sizeof(error_response), &parsed_response);
    
    if (result == ICEBOARD_STATUS_ERROR && parsed_response.type == ICEBOARD_RESPONSE_ERROR) {
        LOG_INFO("ERROR response parsed correctly, error code: %d, slot: %d, message: %s", 
                parsed_response.error_code, parsed_response.slot_number, parsed_response.error_message);
    } else {
        LOG_ERROR("Failed to parse ERROR response");
    }
    
    // Test unknown response
    LOG_INFO("Testing unknown response");
    uint8_t unknown_response[] = {0xFF, 0x01, 0x02}; // Unknown response type
    result = parse_iceboard_response(unknown_response, sizeof(unknown_response), &parsed_response);
    
    if (result == ICEBOARD_STATUS_OK && parsed_response.type == ICEBOARD_RESPONSE_UNKNOWN) {
        LOG_INFO("Unknown response parsed correctly");
    } else {
        LOG_ERROR("Failed to parse unknown response");
    }
    
    LOG_INFO("Iceboard Response Parser Test Completed");
    logger_destroy();
    
    return 0;
}