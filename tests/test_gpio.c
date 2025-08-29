#include "test_framework.h"
#include "../src/drivers/gpio.h"
#include "../src/drivers/gpio_config.h"

TEST_CASE(gpio_button_enum) {
    // Test that button enum values are sequential
    TEST_CHECK(assert_equal_int(0, GPIO_BTN_START, "START button should be 0"));
    TEST_CHECK(assert_equal_int(1, GPIO_BTN_FLAVOR_1, "FLAVOR_1 button should be 1"));
    TEST_CHECK(assert_equal_int(2, GPIO_BTN_FLAVOR_2, "FLAVOR_2 button should be 2"));
    TEST_CHECK(assert_equal_int(3, GPIO_BTN_FLAVOR_3, "FLAVOR_3 button should be 3"));
    TEST_CHECK(assert_equal_int(4, GPIO_BTN_TOPPING_1, "TOPPING_1 button should be 4"));
    TEST_CHECK(assert_equal_int(5, GPIO_BTN_TOPPING_2, "TOPPING_2 button should be 5"));
    TEST_CHECK(assert_equal_int(6, GPIO_BTN_TOPPING_3, "TOPPING_3 button should be 6"));
    TEST_CHECK(assert_equal_int(7, GPIO_BTN_DISPENSE, "DISPENSE button should be 7"));
    TEST_CHECK(assert_equal_int(8, GPIO_BTN_CANCEL, "CANCEL button should be 8"));
    TEST_CHECK(assert_equal_int(9, GPIO_BTN_COUNT, "Button count should be 9"));
    
    return TEST_PASS;
}

TEST_CASE(gpio_led_enum) {
    // Test that LED enum values are sequential
    TEST_CHECK(assert_equal_int(0, GPIO_LED_READY, "READY LED should be 0"));
    TEST_CHECK(assert_equal_int(1, GPIO_LED_BUSY, "BUSY LED should be 1"));
    TEST_CHECK(assert_equal_int(2, GPIO_LED_ERROR, "ERROR LED should be 2"));
    TEST_CHECK(assert_equal_int(3, GPIO_LED_FLAVOR_1, "FLAVOR_1 LED should be 3"));
    TEST_CHECK(assert_equal_int(4, GPIO_LED_FLAVOR_2, "FLAVOR_2 LED should be 4"));
    TEST_CHECK(assert_equal_int(5, GPIO_LED_FLAVOR_3, "FLAVOR_3 LED should be 5"));
    TEST_CHECK(assert_equal_int(6, GPIO_LED_TOPPING_1, "TOPPING_1 LED should be 6"));
    TEST_CHECK(assert_equal_int(7, GPIO_LED_TOPPING_2, "TOPPING_2 LED should be 7"));
    TEST_CHECK(assert_equal_int(8, GPIO_LED_TOPPING_3, "TOPPING_3 LED should be 8"));
    TEST_CHECK(assert_equal_int(9, GPIO_LED_COUNT, "LED count should be 9"));
    
    return TEST_PASS;
}

TEST_CASE(gpio_button_state_enum) {
    // Test button state enum values
    TEST_CHECK(assert_equal_int(0, GPIO_BTN_RELEASED, "RELEASED state should be 0"));
    TEST_CHECK(assert_equal_int(1, GPIO_BTN_PRESSED, "PRESSED state should be 1"));
    TEST_CHECK(assert_equal_int(2, GPIO_BTN_UNKNOWN, "UNKNOWN state should be 2"));
    
    return TEST_PASS;
}

TEST_CASE(gpio_status_enum) {
    // Test status enum values
    TEST_CHECK(assert_equal_int(0, GPIO_STATUS_OK, "OK status should be 0"));
    TEST_CHECK(assert_equal_int(1, GPIO_STATUS_ERROR_INIT, "ERROR_INIT status should be 1"));
    TEST_CHECK(assert_equal_int(2, GPIO_STATUS_ERROR_INVALID_PIN, "ERROR_INVALID_PIN status should be 2"));
    TEST_CHECK(assert_equal_int(3, GPIO_STATUS_ERROR_INVALID_STATE, "ERROR_INVALID_STATE status should be 3"));
    TEST_CHECK(assert_equal_int(4, GPIO_STATUS_ERROR_NOT_INITIALIZED, "ERROR_NOT_INITIALIZED status should be 4"));
    
    return TEST_PASS;
}

TEST_CASE(gpio_config_defaults) {
    // Test default configuration values
    TEST_CHECK(assert_equal_int(50, GPIO_BTN_DEBOUNCE_TIME_MS, "Default debounce time should be 50ms"));
    TEST_CHECK(assert_equal_int(5, GPIO_BTN_FILTER_THRESHOLD, "Default filter threshold should be 5"));
    TEST_CHECK(assert_equal_int(0, GPIO_LED_DEFAULT_STATE, "Default LED state should be OFF (0)"));
    
    return TEST_PASS;
}

TEST_CASE(gpio_pin_mappings) {
    // Test pin mappings
    TEST_CHECK(assert_equal_int(0, GPIO_PIN_BTN_START, "START button pin should be 0"));
    TEST_CHECK(assert_equal_int(9, GPIO_PIN_LED_READY, "READY LED pin should be 9"));
    
    return TEST_PASS;
}

TEST_CASE(gpio_string_conversion) {
    // Test button to string conversion
    TEST_CHECK(assert_equal_str("START", gpio_button_to_string(GPIO_BTN_START), "START button string should be correct"));
    TEST_CHECK(assert_equal_str("FLAVOR_1", gpio_button_to_string(GPIO_BTN_FLAVOR_1), "FLAVOR_1 button string should be correct"));
    TEST_CHECK(assert_equal_str("UNKNOWN", gpio_button_to_string((gpio_button_t)999), "Unknown button should return UNKNOWN"));
    
    // Test LED to string conversion
    TEST_CHECK(assert_equal_str("READY", gpio_led_to_string(GPIO_LED_READY), "READY LED string should be correct"));
    TEST_CHECK(assert_equal_str("BUSY", gpio_led_to_string(GPIO_LED_BUSY), "BUSY LED string should be correct"));
    TEST_CHECK(assert_equal_str("UNKNOWN", gpio_led_to_string((gpio_led_t)999), "Unknown LED should return UNKNOWN"));
    
    return TEST_PASS;
}

test_suite_t* create_gpio_test_suite(void) {
    test_suite_t* suite = test_suite_create("GPIO Module Tests");
    if (suite == NULL) {
        return NULL;
    }
    
    ADD_TEST(suite, gpio_button_enum, true);
    ADD_TEST(suite, gpio_led_enum, true);
    ADD_TEST(suite, gpio_button_state_enum, true);
    ADD_TEST(suite, gpio_status_enum, true);
    ADD_TEST(suite, gpio_config_defaults, true);
    ADD_TEST(suite, gpio_pin_mappings, true);
    ADD_TEST(suite, gpio_string_conversion, true);
    
    return suite;
}