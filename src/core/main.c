#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "config.h"
#include "core/state_machine.h"
#include "drivers/iceboard.h"
#include "drivers/uart.h"
#include "utils/logger.h"
#include "utils/config_manager.h"

#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif

// Global state machine instance
static state_machine_t g_state_machine;

static void system_init(void);
static void main_loop(void);
static void system_cleanup(void);

int main(void) {
    // Initialize the system
    system_init();
    
    // Run the main application loop
    main_loop();
    
    // Cleanup before exit
    system_cleanup();
    
    return 0;
}

static void system_init(void) {\n    // Initialize configuration manager\n    if (config_manager_init() != 0) {\n        fprintf(stderr, \"Failed to initialize configuration manager\\n\");\n        return;\n    }\n    \n    // Get system configuration\n    const system_config_t* config = config_manager_get_config();\n    if (config == NULL) {\n        fprintf(stderr, \"Failed to get system configuration\\n\");\n        return;\n    }\n    \n    // Initialize logger with configuration\n    logger_config_t logger_config = {\n        .level = config->log_level,\n        .output = LOG_OUTPUT_BOTH,\n        .file_path = config->log_file_path,\n        .max_file_size = config->log_max_file_size,\n        .max_files = config->log_max_files\n    };\n    \n    if (logger_init(&logger_config) != 0) {\n        fprintf(stderr, \"Failed to initialize logger\\n\");\n        return;\n    }\n    \n    LOG_INFO(\"MAIN\", \"Initializing system with configuration manager\");\n    config_manager_print_config(config);\n    \n    // Initialize the state machine\n    if (state_machine_init(&g_state_machine) != 0) {\n        LOG_ERROR(\"MAIN\", \"Failed to initialize state machine\");\n        return;\n    }\n    \n    // Initialize iceboard (which will initialize UART with proper configuration)\n    iceboard_init();\n    \n    LOG_INFO(\"MAIN\", \"System initialization complete\");\n}

static void main_loop(void) {
    LOG_INFO("Entering main loop");
    
    // For demonstration purposes, we'll simulate a simple sequence
    // In a real implementation, this would be an infinite loop with event handling
    
    // Simulate starting the process
    state_machine_handle_event(&g_state_machine, EVENT_START);
    
    // Simulate selecting a flavor (vanilla, 100ml)
    iceboard_dispense_flavor(FLAVOR_VANILLA, 100);
    state_machine_handle_event(&g_state_machine, EVENT_RESPONSE_RECEIVED);
    
    // Simulate adding a topping (chocolate syrup, 20ml)
    iceboard_add_topping(TOPPING_CHOCOLATE_SYRUP, 20);
    state_machine_handle_event(&g_state_machine, EVENT_DISPENSE_CONFIRMED);
    
    // Simulate dispensing completion
#ifdef _WIN32
    Sleep(2000);  // Simulate dispensing time
#else
    usleep(2000000);  // 2 seconds
#endif
    
    state_machine_handle_event(&g_state_machine, EVENT_DISPENSE_COMPLETED);
    
    // Wait a bit before resetting
#ifdef _WIN32
    Sleep(1000);
#else
    usleep(1000000);  // 1 second
#endif
    
    state_machine_handle_event(&g_state_machine, EVENT_RESET);
    
    LOG_INFO("Main loop demonstration completed");
}

static void system_cleanup(void) {
    LOG_INFO("Cleaning up system");
    
    // Cleanup all subsystems
    iceboard_cleanup();
    state_machine_destroy(&g_state_machine);
    
    LOG_INFO("System cleanup complete");
    
    // Destroy logger last
    logger_destroy();
}