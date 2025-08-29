#ifndef CONFIG_MANAGER_H
#define CONFIG_MANAGER_H

#include <stdint.h>
#include <stdbool.h>
#include "../config.h"
#include "gpio_config.h"

// Configuration structure for the entire system
typedef struct {
    // UART configuration
    char uart_device[64];
    uint32_t baud_rate;
    uint32_t uart_timeout_ms;
    uint8_t uart_retry_count;
    
    // Iceboard configuration
    uint32_t iceboard_timeout_ms;
    uint8_t iceboard_retry_count;
    
    // GPIO configuration
    gpio_config_t gpio_config;
    
    // Logging configuration
    char log_file_path[256];
    uint32_t log_max_file_size;
    uint8_t log_max_files;
    uint8_t log_level;
    
    // State machine configuration
    uint32_t state_machine_update_interval_ms;
    
    // Feature flags
    bool feature_iceboard_communication;
    bool feature_gpio_control;
    bool feature_uart_debugging;
} system_config_t;

// Function prototypes
int config_manager_init(void);
int config_manager_load_defaults(system_config_t* config);
int config_manager_load_from_file(const char* filename, system_config_t* config);
int config_manager_save_to_file(const char* filename, const system_config_t* config);
const system_config_t* config_manager_get_config(void);
int config_manager_set_config(const system_config_t* config);
void config_manager_print_config(const system_config_t* config);

#endif // CONFIG_MANAGER_H