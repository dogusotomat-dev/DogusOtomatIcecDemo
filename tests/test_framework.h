#ifndef TEST_FRAMEWORK_H
#define TEST_FRAMEWORK_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

// Test framework constants
#define TEST_NAME_MAX_LENGTH 128
#define TEST_OUTPUT_BUFFER_SIZE 1024

// Test result codes
typedef enum {
    TEST_PASS = 0,
    TEST_FAIL,
    TEST_SKIP
} test_result_t;

// Test case structure
typedef struct test_case {
    char name[TEST_NAME_MAX_LENGTH];
    test_result_t (*test_function)(void);
    bool enabled;
    struct test_case* next;
} test_case_t;

// Test suite structure
typedef struct {
    char name[TEST_NAME_MAX_LENGTH];
    test_case_t* test_cases;
    int passed;
    int failed;
    int skipped;
    int total;
} test_suite_t;

// Test framework functions
void test_framework_init(void);
void test_framework_cleanup(void);

// Test suite functions
test_suite_t* test_suite_create(const char* name);
void test_suite_destroy(test_suite_t* suite);
void test_suite_add_test(test_suite_t* suite, const char* name, test_result_t (*test_function)(void), bool enabled);
void test_suite_run(test_suite_t* suite);

// Assertion functions
test_result_t assert_true(bool condition, const char* message);
test_result_t assert_false(bool condition, const char* message);
test_result_t assert_equal_int(int expected, int actual, const char* message);
test_result_t assert_equal_uint(unsigned int expected, unsigned int actual, const char* message);
test_result_t assert_equal_str(const char* expected, const char* actual, const char* message);
test_result_t assert_not_null(void* ptr, const char* message);
test_result_t assert_null(void* ptr, const char* message);
test_result_t assert_fail(const char* message);
test_result_t assert_skip(const char* message);

// Helper macros for cleaner test code
#define TEST_CASE(name) static test_result_t test_##name(void)
#define ADD_TEST(suite, name, enabled) test_suite_add_test(suite, #name, test_##name, enabled)
#define RUN_TEST_SUITE(suite) test_suite_run(suite)

// Color codes for output (Unix/Linux/macOS)
#ifdef __unix__
#define COLOR_RED "\033[31m"
#define COLOR_GREEN "\033[32m"
#define COLOR_YELLOW "\033[33m"
#define COLOR_BLUE "\033[34m"
#define COLOR_RESET "\033[0m"
#else
#define COLOR_RED ""
#define COLOR_GREEN ""
#define COLOR_YELLOW ""
#define COLOR_BLUE ""
#define COLOR_RESET ""
#endif

#ifdef __cplusplus
}
#endif

#endif // TEST_FRAMEWORK_H