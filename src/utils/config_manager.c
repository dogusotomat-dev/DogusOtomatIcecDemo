#include "config_manager.h"
#include "../utils/logger.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Global configuration instance
static system_config_t g_system_config;
static bool g_config_initialized = false;

int config_manager_init(void) {
    if (g_config_initialized) {
        LOG_WARN("Configuration manager already initialized");
        return 0;
    }
    
    // Load default configuration
    if (config_manager_load_defaults(&g_system_config) != 0) {
        LOG_ERROR("Failed to load default configuration");
        return -1;
    }
    
    g_config_initialized = true;
    LOG_INFO("Configuration manager initialized with default settings");
    return 0;
}

int config_manager_load_defaults(system_config_t* config) {
    if (config == NULL) {
        LOG_ERROR("Invalid configuration parameter");
        return -1;
    }
    
    // UART configuration
    strncpy(config->uart_device, UART_DEVICE, sizeof(config->uart_device) - 1);
    config->uart_device[sizeof(config->uart_device) - 1] = '\0';
    config->baud_rate = BAUD_RATE;
    config->uart_timeout_ms = UART_TIMEOUT_MS;
    config->uart_retry_count = UART_RETRY_COUNT;
    
    // Iceboard configuration
    config->iceboard_timeout_ms = ICEBOARD_DEFAULT_TIMEOUT_MS;
    config->iceboard_retry_count = ICEBOARD_DEFAULT_RETRY_COUNT;
    
    // GPIO configuration
    config->gpio_config = default_gpio_config;
    
    // Logging configuration
    strncpy(config->log_file_path, LOG_FILE_PATH, sizeof(config->log_file_path) - 1);
    config->log_file_path[sizeof(config->log_file_path) - 1] = '\0';
    config->log_max_file_size = LOG_MAX_FILE_SIZE;
    config->log_max_files = LOG_MAX_FILES;
    config->log_level = LOG_LEVEL;
    
    // State machine configuration
    config->state_machine_update_interval_ms = STATE_MACHINE_UPDATE_INTERVAL_MS;
    
    // Feature flags
    config->feature_iceboard_communication = FEATURE_ICEBOARD_COMMUNICATION;
    config->feature_gpio_control = FEATURE_GPIO_CONTROL;
    config->feature_uart_debugging = FEATURE_UART_DEBUGGING;
    
    LOG_DEBUG("Default configuration loaded");
    return 0;
}

int config_manager_load_from_file(const char* filename, system_config_t* config) {
    if (filename == NULL || config == NULL) {
        LOG_ERROR("Invalid parameters for configuration loading");
        return -1;
    }
    
    FILE* file = fopen(filename, "r");
    if (file == NULL) {
        LOG_WARN("Configuration file not found, using defaults: %s", filename);
        return config_manager_load_defaults(config);
    }
    
    // For now, we'll just load defaults and log that file loading is not implemented
    LOG_INFO("Loading configuration from file is not yet implemented, using defaults");
    fclose(file);
    
    return config_manager_load_defaults(config);
}

int config_manager_save_to_file(const char* filename, const system_config_t* config) {
    if (filename == NULL || config == NULL) {
        LOG_ERROR("Invalid parameters for configuration saving");
        return -1;
    }
    
    FILE* file = fopen(filename, "w");
    if (file == NULL) {
        LOG_ERROR("Failed to open configuration file for writing: %s", filename);
        return -1;
    }
    
    // For now, we'll just create an empty file and log that saving is not fully implemented
    LOG_INFO("Saving configuration to file is not yet fully implemented");
    
    fclose(file);
    return 0;
}

const system_config_t* config_manager_get_config(void) {
    if (!g_config_initialized) {
        LOG_ERROR("Configuration manager not initialized");
        return NULL;
    }
    
    return &g_system_config;
}

int config_manager_set_config(const system_config_t* config) {
    if (config == NULL) {
        LOG_ERROR("Invalid configuration parameter");
        return -1;
    }
    
    if (!g_config_initialized) {
        LOG_ERROR("Configuration manager not initialized");
        return -1;
    }
    
    g_system_config = *config;
    LOG_DEBUG("Configuration updated");
    return 0;
}

void config_manager_print_config(const system_config_t* config) {
    if (config == NULL) {
        LOG_ERROR("Invalid configuration parameter");
        return;
    }
    
    LOG_INFO("=== System Configuration ===");
    LOG_INFO("UART Device: %s", config->uart_device);
    LOG_INFO("Baud Rate: %lu", config->baud_rate);
    LOG_INFO("UART Timeout: %lu ms", config->uart_timeout_ms);
    LOG_INFO("UART Retry Count: %d", config->uart_retry_count);
    LOG_INFO("Iceboard Timeout: %lu ms", config->iceboard_timeout_ms);
    LOG_INFO("Iceboard Retry Count: %d", config->iceboard_retry_count);
    LOG_INFO("Log File Path: %s", config->log_file_path);
    LOG_INFO("Log Max File Size: %lu bytes", config->log_max_file_size);
    LOG_INFO("Log Max Files: %d", config->log_max_files);
    LOG_INFO("Log Level: %d", config->log_level);
    LOG_INFO("State Machine Update Interval: %lu ms", config->state_machine_update_interval_ms);
    LOG_INFO("Feature Iceboard Communication: %s", config->feature_iceboard_communication ? "Enabled" : "Disabled");
    LOG_INFO("Feature GPIO Control: %s", config->feature_gpio_control ? "Enabled" : "Disabled");
    LOG_INFO("Feature UART Debugging: %s", config->feature_uart_debugging ? "Enabled" : "Disabled");
    LOG_INFO("=== End Configuration ===");
}