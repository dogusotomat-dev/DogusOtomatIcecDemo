#include "test_framework.h"
#include "../src/utils/logger.h"

TEST_CASE(logger_level_enum) {
    // Test log level enum values
    TEST_CHECK(assert_equal_int(0, LOG_LEVEL_DEBUG, "DEBUG level should be 0"));
    TEST_CHECK(assert_equal_int(1, LOG_LEVEL_INFO, "INFO level should be 1"));
    TEST_CHECK(assert_equal_int(2, LOG_LEVEL_WARN, "WARN level should be 2"));
    TEST_CHECK(assert_equal_int(3, LOG_LEVEL_ERROR, "ERROR level should be 3"));
    TEST_CHECK(assert_equal_int(4, LOG_LEVEL_OFF, "OFF level should be 4"));
    
    return TEST_PASS;
}

TEST_CASE(logger_output_enum) {
    // Test output enum values
    TEST_CHECK(assert_equal_int(1, LOG_OUTPUT_CONSOLE, "CONSOLE output should be 1"));
    TEST_CHECK(assert_equal_int(2, LOG_OUTPUT_FILE, "FILE output should be 2"));
    TEST_CHECK(assert_equal_int(3, LOG_OUTPUT_BOTH, "BOTH output should be 3"));
    
    return TEST_PASS;
}

TEST_CASE(logger_level_string_conversion) {
    // Test level to string conversion
    TEST_CHECK(assert_equal_str("DEBUG", logger_level_to_string(LOG_LEVEL_DEBUG), "DEBUG level string should be correct"));
    TEST_CHECK(assert_equal_str("INFO", logger_level_to_string(LOG_LEVEL_INFO), "INFO level string should be correct"));
    TEST_CHECK(assert_equal_str("WARN", logger_level_to_string(LOG_LEVEL_WARN), "WARN level string should be correct"));
    TEST_CHECK(assert_equal_str("ERROR", logger_level_to_string(LOG_LEVEL_ERROR), "ERROR level string should be correct"));
    TEST_CHECK(assert_equal_str("OFF", logger_level_to_string(LOG_LEVEL_OFF), "OFF level string should be correct"));
    TEST_CHECK(assert_equal_str("UNKNOWN", logger_level_to_string((log_level_t)999), "Unknown level should return UNKNOWN"));
    
    return TEST_PASS;
}

TEST_CASE(logger_config_structure) {
    // Test logger configuration structure
    logger_config_t config = {
        .level = LOG_LEVEL_DEBUG,
        .output = LOG_OUTPUT_BOTH,
        .file_path = "test.log",
        .max_file_size = 1024 * 1024,
        .max_files = 5
    };
    
    TEST_CHECK(assert_equal_int(LOG_LEVEL_DEBUG, config.level, "Log level should be set correctly"));
    TEST_CHECK(assert_equal_int(LOG_OUTPUT_BOTH, config.output, "Output destination should be set correctly"));
    TEST_CHECK(assert_equal_str("test.log", config.file_path, "File path should be set correctly"));
    TEST_CHECK(assert_equal_uint(1024 * 1024, config.max_file_size, "Max file size should be set correctly"));
    TEST_CHECK(assert_equal_int(5, config.max_files, "Max files should be set correctly"));
    
    return TEST_PASS;
}

test_suite_t* create_logger_test_suite(void) {
    test_suite_t* suite = test_suite_create("Logger Module Tests");
    if (suite == NULL) {
        return NULL;
    }
    
    ADD_TEST(suite, logger_level_enum, true);
    ADD_TEST(suite, logger_output_enum, true);
    ADD_TEST(suite, logger_level_string_conversion, true);
    ADD_TEST(suite, logger_config_structure, true);
    
    return suite;
}