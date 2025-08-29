#include <stdio.h>
#include <stdlib.h>
#include "utils/config_manager.h"
#include "utils/logger.h"

int main(void) {
    // Initialize logger
    logger_config_t logger_config = {
        .level = LOG_LEVEL_DEBUG,
        .output = LOG_OUTPUT_CONSOLE,
        .file_path = "config_test.log"
    };
    
    if (logger_init(&logger_config) != 0) {
        fprintf(stderr, "Failed to initialize logger\n");
        return 1;
    }
    
    LOG_INFO("Configuration Manager Test Started");
    
    // Initialize configuration manager
    if (config_manager_init() != 0) {
        LOG_ERROR("Failed to initialize configuration manager");
        logger_destroy();
        return 1;
    }
    
    LOG_INFO("Configuration manager initialized successfully");
    
    // Get configuration
    const system_config_t* config = config_manager_get_config();
    if (config == NULL) {
        LOG_ERROR("Failed to get configuration");
        logger_destroy();
        return 1;
    }
    
    // Print configuration
    config_manager_print_config(config);
    
    // Test loading from file (will use defaults since file doesn't exist)
    system_config_t file_config;
    if (config_manager_load_from_file("test_config.ini", &file_config) == 0) {
        LOG_INFO("Configuration loaded from file (using defaults)");
        config_manager_print_config(&file_config);
    } else {
        LOG_ERROR("Failed to load configuration from file");
    }
    
    // Test modifying configuration
    system_config_t modified_config = *config;
    modified_config.baud_rate = 115200;
    modified_config.uart_timeout_ms = 5000;
    
    if (config_manager_set_config(&modified_config) == 0) {
        LOG_INFO("Configuration updated successfully");
        const system_config_t* updated_config = config_manager_get_config();
        LOG_INFO("Updated baud rate: %lu", updated_config->baud_rate);
        LOG_INFO("Updated UART timeout: %lu ms", updated_config->uart_timeout_ms);
    } else {
        LOG_ERROR("Failed to update configuration");
    }
    
    // Test saving to file
    if (config_manager_save_to_file("saved_config.ini", config) == 0) {
        LOG_INFO("Configuration saved to file");
    } else {
        LOG_ERROR("Failed to save configuration to file");
    }
    
    LOG_INFO("Configuration Manager Test Completed");
    logger_destroy();
    
    return 0;
}