#include "test_framework.h"
#include "../src/utils/config_manager.h"

TEST_CASE(config_manager_init) {
    // Test configuration manager initialization
    int result = config_manager_init();
    TEST_CHECK(assert_equal_int(0, result, "Configuration manager initialization should succeed"));
    
    return TEST_PASS;
}

TEST_CASE(config_manager_get_config) {
    // Test getting configuration
    const system_config_t* config = config_manager_get_config();
    TEST_CHECK(assert_not_null((void*)config, "Configuration should not be NULL"));
    
    return TEST_PASS;
}

TEST_CASE(config_manager_load_defaults) {
    // Test loading default configuration
    system_config_t config;
    int result = config_manager_load_defaults(&config);
    TEST_CHECK(assert_equal_int(0, result, "Loading defaults should succeed"));
    TEST_CHECK(assert_not_null((void*)&config, "Configuration structure should be valid"));
    
    return TEST_PASS;
}

TEST_CASE(config_manager_uart_config) {
    // Test UART configuration values
    const system_config_t* config = config_manager_get_config();
    TEST_CHECK(assert_not_null((void*)config, "Configuration should not be NULL"));
    
    // Check some default values (these would come from config.h)
    TEST_CHECK(assert_equal_str("/dev/ttyUSB0", config->uart_device, "Default UART device should be correct"));
    
    return TEST_PASS;
}

TEST_CASE(config_manager_gpio_config) {
    // Test GPIO configuration
    const system_config_t* config = config_manager_get_config();
    TEST_CHECK(assert_not_null((void*)config, "Configuration should not be NULL"));
    
    // Check GPIO configuration
    TEST_CHECK(assert_equal_int(50, config->gpio_config.button_debounce_time_ms, "Default debounce time should be correct"));
    TEST_CHECK(assert_equal_int(5, config->gpio_config.button_filter_threshold, "Default filter threshold should be correct"));
    
    return TEST_PASS;
}

TEST_CASE(config_manager_log_config) {
    // Test logging configuration
    const system_config_t* config = config_manager_get_config();
    TEST_CHECK(assert_not_null((void*)config, "Configuration should not be NULL"));
    
    // Check logging configuration
    TEST_CHECK(assert_equal_str("icec.log", config->log_file_path, "Default log file path should be correct"));
    TEST_CHECK(assert_equal_uint(1024 * 1024, config->log_max_file_size, "Default max file size should be correct"));
    TEST_CHECK(assert_equal_int(5, config->log_max_files, "Default max files should be correct"));
    
    return TEST_PASS;
}

test_suite_t* create_config_manager_test_suite(void) {
    test_suite_t* suite = test_suite_create("Configuration Manager Tests");
    if (suite == NULL) {
        return NULL;
    }
    
    ADD_TEST(suite, config_manager_init, true);
    ADD_TEST(suite, config_manager_get_config, true);
    ADD_TEST(suite, config_manager_load_defaults, true);
    ADD_TEST(suite, config_manager_uart_config, true);
    ADD_TEST(suite, config_manager_gpio_config, true);
    ADD_TEST(suite, config_manager_log_config, true);
    
    return suite;
}