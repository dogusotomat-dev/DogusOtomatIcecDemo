#include "iceboard.h"
#include "iceboard_parse.h"
#include "../utils/logger.h"
#include "../utils/config_manager.h"
#include "uart.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif

static int iceboard_initialized = 0;

void iceboard_init(void) {
    LOG_INFO("Initializing iceboard controller");
    
    // Get system configuration
    const system_config_t* config = config_manager_get_config();
    if (config == NULL) {
        LOG_ERROR("Failed to get system configuration");
        return;
    }
    
    // Initialize UART for communication with iceboard
    uart_config_t uart_config = {
        .baud_rate = config->baud_rate,
        .timeout_ms = config->iceboard_timeout_ms
    };
    
    uart_error_t result = uart_init(&uart_config);
    if (result != UART_SUCCESS) {
        LOG_ERROR("Failed to initialize UART: %d", result);
        return;
    }
    
    // Send initialization command
    iceboard_command_t init_cmd = {
        .command = 0x01,
        .data = {0x00, 0x00, 0x00}
    };
    
    result = iceboard_send_command_with_retry(&init_cmd, NULL, 0);
    if (result != ICEBOARD_STATUS_OK) {
        LOG_ERROR("Failed to send initialization command: %d", result);
        return;
    }
    
    iceboard_initialized = 1;
    LOG_INFO("Iceboard controller initialized successfully");
}

iceboard_status_t iceboard_dispense_flavor(flavor_t flavor, uint16_t amount_ml) {
    if (!iceboard_initialized) {
        LOG_ERROR("Iceboard not initialized");
        return ICEBOARD_STATUS_ERROR;
    }
    
    LOG_INFO("Dispensing flavor %d, amount %u ml", flavor, amount_ml);
    
    // Send dispense command
    iceboard_command_t dispense_cmd = {
        .command = 0x02,
        .data = {(uint8_t)flavor, (uint8_t)(amount_ml >> 8), (uint8_t)(amount_ml & 0xFF)}
    };
    
    iceboard_status_t result = iceboard_send_command_with_retry(&dispense_cmd, NULL, 0);
    if (result != ICEBOARD_STATUS_OK) {
        LOG_ERROR("Failed to send dispense command: %d", result);
        return result;
    }
    
    // In a real implementation, we would wait for a response
    // For now, we'll just simulate success
#ifdef _WIN32
    Sleep(100);  // Simulate processing time
#else
    usleep(100000);  // 100ms
#endif
    
    LOG_INFO("Flavor dispensed successfully");
    return ICEBOARD_STATUS_OK;
}

iceboard_status_t iceboard_add_topping(topping_t topping, uint16_t amount_ml) {
    if (!iceboard_initialized) {
        LOG_ERROR("Iceboard not initialized");
        return ICEBOARD_STATUS_ERROR;
    }
    
    LOG_INFO("Adding topping %d, amount %u ml", topping, amount_ml);
    
    // Send topping command
    iceboard_command_t topping_cmd = {
        .command = 0x03,
        .data = {(uint8_t)topping, (uint8_t)(amount_ml >> 8), (uint8_t)(amount_ml & 0xFF)}
    };
    
    iceboard_status_t result = iceboard_send_command_with_retry(&topping_cmd, NULL, 0);
    if (result != ICEBOARD_STATUS_OK) {
        LOG_ERROR("Failed to send topping command: %d", result);
        return result;
    }
    
    // Simulate processing time
#ifdef _WIN32
    Sleep(50);
#else
    usleep(50000);  // 50ms
#endif
    
    LOG_INFO("Topping added successfully");
    return ICEBOARD_STATUS_OK;
}

iceboard_status_t iceboard_start_dispensing(void) {
    if (!iceboard_initialized) {
        LOG_ERROR("Iceboard not initialized");
        return ICEBOARD_STATUS_ERROR;
    }
    
    LOG_INFO("Starting dispensing process");
    
    // Send start command
    iceboard_command_t start_cmd = {
        .command = 0x04,
        .data = {0x00, 0x00, 0x00}
    };
    
    iceboard_status_t result = iceboard_send_command_with_retry(&start_cmd, NULL, 0);
    if (result != ICEBOARD_STATUS_OK) {
        LOG_ERROR("Failed to send start command: %d", result);
        return result;
    }
    
    LOG_INFO("Dispensing process started");
    return ICEBOARD_STATUS_OK;
}

iceboard_status_t iceboard_stop_dispensing(void) {
    if (!iceboard_initialized) {
        LOG_ERROR("Iceboard not initialized");
        return ICEBOARD_STATUS_ERROR;
    }
    
    LOG_INFO("Stopping dispensing process");
    
    // Send stop command
    iceboard_command_t stop_cmd = {
        .command = 0x05,
        .data = {0x00, 0x00, 0x00}
    };
    
    iceboard_status_t result = iceboard_send_command_with_retry(&stop_cmd, NULL, 0);
    if (result != ICEBOARD_STATUS_OK) {
        LOG_ERROR("Failed to send stop command: %d", result);
        return result;
    }
    
    LOG_INFO("Dispensing process stopped");
    return ICEBOARD_STATUS_OK;
}

iceboard_status_t iceboard_get_status(void) {
    if (!iceboard_initialized) {
        LOG_ERROR("Iceboard not initialized");
        return ICEBOARD_STATUS_ERROR;
    }
    
    LOG_DEBUG("Getting iceboard status");
    
    // Send status request command
    iceboard_command_t status_cmd = {
        .command = 0x06,
        .data = {0x00, 0x00, 0x00}
    };
    
    uint8_t response[32];
    uint16_t received_length;
    
    // Send command and get response
    iceboard_command_t cmd = {
        .command = 0x06,
        .data = {0x00, 0x00, 0x00}
    };
    
    // We need to implement a version that gets the actual response
    // For now, we'll simulate getting a response
    uart_error_t uart_result = uart_send_buffer((uint8_t*)&cmd, sizeof(cmd));
    if (uart_result != UART_SUCCESS) {
        LOG_ERROR("Failed to send status request command: %d", uart_result);
        return ICEBOARD_STATUS_ERROR;
    }
    
    // For demonstration, let's simulate a response
    response[0] = 0x01; // STATUS response
    response[1] = 0x02; // Slot number
    response[2] = 0x01; // Some status data
    response[3] = 0x00; // More status data
    received_length = 4;
    
    // Parse the response
    iceboard_response_t parsed_response;
    iceboard_status_t result = parse_iceboard_response(response, received_length, &parsed_response);
    
    if (result != ICEBOARD_STATUS_OK) {
        LOG_ERROR("Failed to parse iceboard response: %d", result);
        return result;
    }
    
    if (parsed_response.type == ICEBOARD_RESPONSE_STATUS) {
        LOG_INFO("Iceboard status - Slot: %d, Data Length: %d", 
                parsed_response.slot_number, parsed_response.data_length);
    }
    
    return ICEBOARD_STATUS_OK;
}

void iceboard_cleanup(void) {
    if (!iceboard_initialized) {
        return;
    }
    
    LOG_INFO("Cleaning up iceboard controller");
    
    // Send cleanup command
    iceboard_command_t cleanup_cmd = {
        .command = 0x07,
        .data = {0x00, 0x00, 0x00}
    };
    
    iceboard_status_t result = iceboard_send_command_with_retry(&cleanup_cmd, NULL, 0);
    if (result != ICEBOARD_STATUS_OK) {
        LOG_ERROR("Failed to send cleanup command: %d", result);
    }
    
    // Close UART connection
    uart_close();
    
    iceboard_initialized = 0;
    LOG_INFO("Iceboard controller cleaned up");
}