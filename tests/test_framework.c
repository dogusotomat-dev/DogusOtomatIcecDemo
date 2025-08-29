#include "test_framework.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Global test framework state
static int g_total_passed = 0;
static int g_total_failed = 0;
static int g_total_skipped = 0;
static int g_total_suites = 0;

void test_framework_init(void) {
    g_total_passed = 0;
    g_total_failed = 0;
    g_total_skipped = 0;
    g_total_suites = 0;
    
    printf(COLOR_BLUE "==========================================\n");
    printf("Test Framework Initialized\n");
    printf("==========================================\n" COLOR_RESET);
}

void test_framework_cleanup(void) {
    printf(COLOR_BLUE "\n==========================================\n");
    printf("Test Framework Summary\n");
    printf("==========================================\n" COLOR_RESET);
    printf("Total suites: %d\n", g_total_suites);
    printf("Total passed: " COLOR_GREEN "%d\n" COLOR_RESET, g_total_passed);
    printf("Total failed: " COLOR_RED "%d\n" COLOR_RESET, g_total_failed);
    printf("Total skipped: " COLOR_YELLOW "%d\n" COLOR_RESET, g_total_skipped);
    printf("Total tests: %d\n", g_total_passed + g_total_failed + g_total_skipped);
    
    if (g_total_failed > 0) {
        printf(COLOR_RED "\nSome tests failed!\n" COLOR_RESET);
    } else {
        printf(COLOR_GREEN "\nAll tests passed!\n" COLOR_RESET);
    }
    
    printf(COLOR_BLUE "==========================================\n" COLOR_RESET);
}

test_suite_t* test_suite_create(const char* name) {
    if (name == NULL) {
        return NULL;
    }
    
    test_suite_t* suite = (test_suite_t*)malloc(sizeof(test_suite_t));
    if (suite == NULL) {
        return NULL;
    }
    
    strncpy(suite->name, name, TEST_NAME_MAX_LENGTH - 1);
    suite->name[TEST_NAME_MAX_LENGTH - 1] = '\0';
    suite->test_cases = NULL;
    suite->passed = 0;
    suite->failed = 0;
    suite->skipped = 0;
    suite->total = 0;
    
    g_total_suites++;
    
    return suite;
}

void test_suite_destroy(test_suite_t* suite) {
    if (suite == NULL) {
        return;
    }
    
    test_case_t* current = suite->test_cases;
    while (current != NULL) {
        test_case_t* next = current->next;
        free(current);
        current = next;
    }
    
    free(suite);
}

void test_suite_add_test(test_suite_t* suite, const char* name, test_result_t (*test_function)(void), bool enabled) {
    if (suite == NULL || name == NULL || test_function == NULL) {
        return;
    }
    
    test_case_t* test_case = (test_case_t*)malloc(sizeof(test_case_t));
    if (test_case == NULL) {
        return;
    }
    
    strncpy(test_case->name, name, TEST_NAME_MAX_LENGTH - 1);
    test_case->name[TEST_NAME_MAX_LENGTH - 1] = '\0';
    test_case->test_function = test_function;
    test_case->enabled = enabled;
    test_case->next = suite->test_cases;
    suite->test_cases = test_case;
    suite->total++;
}

void test_suite_run(test_suite_t* suite) {
    if (suite == NULL) {
        return;
    }
    
    printf(COLOR_BLUE "\n==========================================\n");
    printf("Running Test Suite: %s\n", suite->name);
    printf("==========================================\n" COLOR_RESET);
    
    test_case_t* current = suite->test_cases;
    int test_index = 1;
    
    while (current != NULL) {
        if (current->enabled) {
            printf("[%02d] %s: ", test_index, current->name);
            fflush(stdout);
            
            clock_t start_time = clock();
            test_result_t result = current->test_function();
            clock_t end_time = clock();
            
            double elapsed_time = ((double)(end_time - start_time)) / CLOCKS_PER_SEC * 1000;
            
            switch (result) {
                case TEST_PASS:
                    printf(COLOR_GREEN "PASS" COLOR_RESET " (%.2f ms)\n", elapsed_time);
                    suite->passed++;
                    g_total_passed++;
                    break;
                case TEST_FAIL:
                    printf(COLOR_RED "FAIL" COLOR_RESET " (%.2f ms)\n", elapsed_time);
                    suite->failed++;
                    g_total_failed++;
                    break;
                case TEST_SKIP:
                    printf(COLOR_YELLOW "SKIP" COLOR_RESET " (%.2f ms)\n", elapsed_time);
                    suite->skipped++;
                    g_total_skipped++;
                    break;
            }
        } else {
            printf("[%02d] %s: " COLOR_YELLOW "DISABLED\n" COLOR_RESET, test_index, current->name);
        }
        
        current = current->next;
        test_index++;
    }
    
    printf(COLOR_BLUE "\nSuite Summary: %s\n" COLOR_RESET, suite->name);
    printf("  Passed:  " COLOR_GREEN "%d\n" COLOR_RESET, suite->passed);
    printf("  Failed:  " COLOR_RED "%d\n" COLOR_RESET, suite->failed);
    printf("  Skipped: " COLOR_YELLOW "%d\n" COLOR_RESET, suite->skipped);
    printf("  Total:   %d\n", suite->total);
}

// Assertion functions
test_result_t assert_true(bool condition, const char* message) {
    if (condition) {
        return TEST_PASS;
    } else {
        if (message != NULL) {
            printf(COLOR_RED "  Assertion failed: %s\n" COLOR_RESET, message);
        }
        return TEST_FAIL;
    }
}

test_result_t assert_false(bool condition, const char* message) {
    if (!condition) {
        return TEST_PASS;
    } else {
        if (message != NULL) {
            printf(COLOR_RED "  Assertion failed: %s\n" COLOR_RESET, message);
        }
        return TEST_FAIL;
    }
}

test_result_t assert_equal_int(int expected, int actual, const char* message) {
    if (expected == actual) {
        return TEST_PASS;
    } else {
        if (message != NULL) {
            printf(COLOR_RED "  Assertion failed: %s\n" COLOR_RESET, message);
        }
        printf(COLOR_RED "    Expected: %d\n" COLOR_RESET, expected);
        printf(COLOR_RED "    Actual:   %d\n" COLOR_RESET, actual);
        return TEST_FAIL;
    }
}

test_result_t assert_equal_uint(unsigned int expected, unsigned int actual, const char* message) {
    if (expected == actual) {
        return TEST_PASS;
    } else {
        if (message != NULL) {
            printf(COLOR_RED "  Assertion failed: %s\n" COLOR_RESET, message);
        }
        printf(COLOR_RED "    Expected: %u\n" COLOR_RESET, expected);
        printf(COLOR_RED "    Actual:   %u\n" COLOR_RESET, actual);
        return TEST_FAIL;
    }
}

test_result_t assert_equal_str(const char* expected, const char* actual, const char* message) {
    if (expected == NULL && actual == NULL) {
        return TEST_PASS;
    }
    
    if (expected != NULL && actual != NULL && strcmp(expected, actual) == 0) {
        return TEST_PASS;
    } else {
        if (message != NULL) {
            printf(COLOR_RED "  Assertion failed: %s\n" COLOR_RESET, message);
        }
        printf(COLOR_RED "    Expected: \"%s\"\n" COLOR_RESET, expected ? expected : "NULL");
        printf(COLOR_RED "    Actual:   \"%s\"\n" COLOR_RESET, actual ? actual : "NULL");
        return TEST_FAIL;
    }
}

test_result_t assert_not_null(void* ptr, const char* message) {
    if (ptr != NULL) {
        return TEST_PASS;
    } else {
        if (message != NULL) {
            printf(COLOR_RED "  Assertion failed: %s\n" COLOR_RESET, message);
        }
        return TEST_FAIL;
    }
}

test_result_t assert_null(void* ptr, const char* message) {
    if (ptr == NULL) {
        return TEST_PASS;
    } else {
        if (message != NULL) {
            printf(COLOR_RED "  Assertion failed: %s\n" COLOR_RESET, message);
        }
        return TEST_FAIL;
    }
}

test_result_t assert_fail(const char* message) {
    if (message != NULL) {
        printf(COLOR_RED "  Test failed: %s\n" COLOR_RESET, message);
    }
    return TEST_FAIL;
}

test_result_t assert_skip(const char* message) {
    if (message != NULL) {
        printf(COLOR_YELLOW "  Test skipped: %s\n" COLOR_RESET, message);
    }
    return TEST_SKIP;
}