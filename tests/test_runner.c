#include <stdio.h>
#include <stdlib.h>
#include "test_framework.h"

// Forward declarations of test suite creation functions
test_suite_t* create_uart_test_suite(void);
test_suite_t* create_iceboard_test_suite(void);
test_suite_t* create_state_machine_test_suite(void);
test_suite_t* create_gpio_test_suite(void);
test_suite_t* create_logger_test_suite(void);
test_suite_t* create_config_manager_test_suite(void);

int main(int argc, char* argv[]) {
    // Initialize test framework
    test_framework_init();
    
    // Check if specific test suite is requested
    bool run_all = true;
    bool run_uart = false;
    bool run_iceboard = false;
    bool run_state_machine = false;
    bool run_gpio = false;
    bool run_logger = false;
    bool run_config = false;
    
    if (argc > 1) {
        run_all = false;
        for (int i = 1; i < argc; i++) {
            if (strcmp(argv[i], "uart") == 0) {
                run_uart = true;
            } else if (strcmp(argv[i], "iceboard") == 0) {
                run_iceboard = true;
            } else if (strcmp(argv[i], "state_machine") == 0) {
                run_state_machine = true;
            } else if (strcmp(argv[i], "gpio") == 0) {
                run_gpio = true;
            } else if (strcmp(argv[i], "logger") == 0) {
                run_logger = true;
            } else if (strcmp(argv[i], "config") == 0) {
                run_config = true;
            } else if (strcmp(argv[i], "all") == 0) {
                run_all = true;
                break;
            }
        }
    }
    
    // Create and run test suites
    if (run_all || run_uart) {
        test_suite_t* uart_suite = create_uart_test_suite();
        if (uart_suite != NULL) {
            RUN_TEST_SUITE(uart_suite);
            test_suite_destroy(uart_suite);
        }
    }
    
    if (run_all || run_iceboard) {
        test_suite_t* iceboard_suite = create_iceboard_test_suite();
        if (iceboard_suite != NULL) {
            RUN_TEST_SUITE(iceboard_suite);
            test_suite_destroy(iceboard_suite);
        }
    }
    
    if (run_all || run_state_machine) {
        test_suite_t* state_machine_suite = create_state_machine_test_suite();
        if (state_machine_suite != NULL) {
            RUN_TEST_SUITE(state_machine_suite);
            test_suite_destroy(state_machine_suite);
        }
    }
    
    if (run_all || run_gpio) {
        test_suite_t* gpio_suite = create_gpio_test_suite();
        if (gpio_suite != NULL) {
            RUN_TEST_SUITE(gpio_suite);
            test_suite_destroy(gpio_suite);
        }
    }
    
    if (run_all || run_logger) {
        test_suite_t* logger_suite = create_logger_test_suite();
        if (logger_suite != NULL) {
            RUN_TEST_SUITE(logger_suite);
            test_suite_destroy(logger_suite);
        }
    }
    
    if (run_all || run_config) {
        test_suite_t* config_suite = create_config_manager_test_suite();
        if (config_suite != NULL) {
            RUN_TEST_SUITE(config_suite);
            test_suite_destroy(config_suite);
        }
    }
    
    // Cleanup test framework
    test_framework_cleanup();
    
    // Return appropriate exit code
    return (g_total_failed > 0) ? EXIT_FAILURE : EXIT_SUCCESS;
}