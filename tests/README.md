# Test Framework Documentation

This document describes the comprehensive test framework implemented for the ice cream vending machine project.

## Overview

The test framework provides a professional testing solution with the following features:

1. **Simple Assertion Functions**
2. **Test Case Execution Management**
3. **Modular Test Suites**
4. **Colorized Output**
5. **Performance Timing**
6. **Comprehensive Reporting**

## Framework Components

### Core Files
- `test_framework.h` - Main header file with declarations
- `test_framework.c` - Implementation of test framework
- `test_runner.c` - Main test execution program

### Test Suites
- `test_uart.c` - UART module tests
- `test_iceboard.c` - Iceboard module tests
- `test_state_machine.c` - State machine tests
- `test_gpio.c` - GPIO module tests
- `test_logger.c` - Logger module tests
- `test_config_manager.c` - Configuration manager tests

## Assertion Functions

The framework provides the following assertion functions:

### Basic Assertions
- `assert_true(condition, message)` - Assert that condition is true
- `assert_false(condition, message)` - Assert that condition is false
- `assert_fail(message)` - Force test failure
- `assert_skip(message)` - Skip test execution

### Equality Assertions
- `assert_equal_int(expected, actual, message)` - Compare integers
- `assert_equal_uint(expected, actual, message)` - Compare unsigned integers
- `assert_equal_str(expected, actual, message)` - Compare strings

### Pointer Assertions
- `assert_not_null(ptr, message)` - Assert pointer is not NULL
- `assert_null(ptr, message)` - Assert pointer is NULL

## Test Case Structure

Test cases are defined using the `TEST_CASE` macro:

```c
TEST_CASE(example_test) {
    int result = some_function();
    return assert_equal_int(42, result, "Function should return 42");
}
```

## Test Suite Creation

Test suites are created by implementing a factory function:

```c
test_suite_t* create_example_test_suite(void) {
    test_suite_t* suite = test_suite_create("Example Tests");
    if (suite == NULL) {
        return NULL;
    }
    
    ADD_TEST(suite, example_test, true);  // true = enabled
    return suite;
}
```

## Running Tests

### Build and Run All Tests
```bash
cd tests
make
make run
```

### Run Specific Test Suites
```bash
make run-uart
make run-iceboard
make run-state-machine
make run-gpio
make run-logger
make run-config
```

### Command Line Arguments
The test runner accepts command line arguments to run specific test suites:
```bash
./bin/icec_tests uart iceboard  # Run only UART and Iceboard tests
./bin/icec_tests all            # Run all tests (default)
```

## Test Results

The framework provides detailed test results including:

1. **Individual Test Results**
   - Pass/Fail/Skip status
   - Execution time in milliseconds
   - Detailed failure information

2. **Suite Summaries**
   - Passed/Failed/Skipped counts
   - Total test count

3. **Overall Summary**
   - Total suites executed
   - Overall pass/fail statistics
   - Success or failure indication

## Colorized Output

The framework uses colorized output for better readability:
- **Green** - Passed tests
- **Red** - Failed tests
- **Yellow** - Skipped tests
- **Blue** - Informational messages

## Performance Timing

Each test case includes execution time measurement:
- Precise timing using `clock()` function
- Time displayed in milliseconds
- Helps identify performance issues

## Best Practices

### Writing Tests
1. **Use Descriptive Names** - Test case names should clearly indicate what is being tested
2. **Single Assertion Principle** - Each test should ideally verify one thing
3. **Clear Failure Messages** - Provide meaningful messages for assertion failures
4. **Test Edge Cases** - Include tests for boundary conditions and error cases

### Test Organization
1. **Group Related Tests** - Organize tests into logical suites
2. **Enable/Disable Tests** - Use the enabled flag to temporarily disable problematic tests
3. **Clean Up Resources** - Ensure tests properly clean up any allocated resources

### Example Test Case
```c
TEST_CASE(uart_init_valid_config) {
    uart_config_t config = {
        .baud_rate = 9600,
        .timeout_ms = 1000
    };
    
    uart_error_t result = uart_init(&config);
    return assert_equal_int(UART_SUCCESS, result, 
                           "UART initialization should succeed with valid config");
}
```

## Extending the Framework

### Adding New Test Suites
1. Create a new test file (`test_module.c`)
2. Implement test cases using `TEST_CASE` macro
3. Create a suite factory function
4. Add the suite to `test_runner.c`
5. Update the Makefile if necessary

### Adding New Assertion Functions
1. Add declaration to `test_framework.h`
2. Implement in `test_framework.c`
3. Update documentation

## Integration with Build System

The test framework integrates with the main build system:

### Main Makefile Integration
```makefile
# Run all tests
test: 
	cd tests && $(MAKE) run

# Build tests
build-tests:
	cd tests && $(MAKE) all
```

### Continuous Integration
The framework is designed for CI/CD integration:
- Clear pass/fail exit codes
- Detailed reporting
- No user interaction required

## Mocking and Stubbing

For hardware-dependent modules, the framework supports:
- Mock implementations for testing
- Stub functions for isolation
- Simulated hardware behavior

## Future Enhancements

Planned improvements:
1. **XML/JUnit Output** - For CI/CD integration
2. **Code Coverage** - Integration with gcov/lcov
3. **Memory Leak Detection** - Integration with Valgrind
4. **Parameterized Tests** - Data-driven testing support
5. **Test Fixtures** - Setup/teardown functions