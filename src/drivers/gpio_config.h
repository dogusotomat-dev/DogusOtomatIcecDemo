#ifndef GPIO_CONFIG_H
#define GPIO_CONFIG_H

#include "config.h"

// GPIO Pin Definitions
// Button pins (using wiringPi numbering)
typedef enum {
    GPIO_BTN_START = GPIO_PIN_BTN_START,
    GPIO_BTN_FLAVOR_1 = GPIO_PIN_BTN_FLAVOR_1,
    GPIO_BTN_FLAVOR_2 = GPIO_PIN_BTN_FLAVOR_2,
    GPIO_BTN_FLAVOR_3 = GPIO_PIN_BTN_FLAVOR_3,
    GPIO_BTN_TOPPING_1 = GPIO_PIN_BTN_TOPPING_1,
    GPIO_BTN_TOPPING_2 = GPIO_PIN_BTN_TOPPING_2,
    GPIO_BTN_TOPPING_3 = GPIO_PIN_BTN_TOPPING_3,
    GPIO_BTN_DISPENSE = GPIO_PIN_BTN_DISPENSE,
    GPIO_BTN_CANCEL = GPIO_PIN_BTN_CANCEL,
    GPIO_BTN_COUNT  // Total number of buttons
} gpio_button_pin_t;

// LED pins (using wiringPi numbering)
typedef enum {
    GPIO_LED_READY = GPIO_PIN_LED_READY,
    GPIO_LED_BUSY = GPIO_PIN_LED_BUSY,
    GPIO_LED_ERROR = GPIO_PIN_LED_ERROR,
    GPIO_LED_FLAVOR_1 = GPIO_PIN_LED_FLAVOR_1,
    GPIO_LED_FLAVOR_2 = GPIO_PIN_LED_FLAVOR_2,
    GPIO_LED_FLAVOR_3 = GPIO_PIN_LED_FLAVOR_3,
    GPIO_LED_TOPPING_1 = GPIO_PIN_LED_TOPPING_1,
    GPIO_LED_TOPPING_2 = GPIO_PIN_LED_TOPPING_2,
    GPIO_LED_TOPPING_3 = GPIO_PIN_LED_TOPPING_3,
    GPIO_LED_COUNT  // Total number of LEDs
} gpio_led_pin_t;

// GPIO Configuration Structure
typedef struct {
    // Button configuration
    int button_debounce_time_ms;
    int button_filter_threshold;
    
    // LED configuration
    int led_default_state;
    
    // Pin mappings
    int button_pins[GPIO_BTN_COUNT];
    int led_pins[GPIO_LED_COUNT];
} gpio_config_t;

// Default GPIO configuration
static const gpio_config_t default_gpio_config = {
    .button_debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS,
    .button_filter_threshold = GPIO_BTN_FILTER_THRESHOLD,
    .led_default_state = GPIO_LED_DEFAULT_STATE,
    .button_pins = {
        [GPIO_BTN_START] = GPIO_PIN_BTN_START,
        [GPIO_BTN_FLAVOR_1] = GPIO_PIN_BTN_FLAVOR_1,
        [GPIO_BTN_FLAVOR_2] = GPIO_PIN_BTN_FLAVOR_2,
        [GPIO_BTN_FLAVOR_3] = GPIO_PIN_BTN_FLAVOR_3,
        [GPIO_BTN_TOPPING_1] = GPIO_PIN_BTN_TOPPING_1,
        [GPIO_BTN_TOPPING_2] = GPIO_PIN_BTN_TOPPING_2,
        [GPIO_BTN_TOPPING_3] = GPIO_PIN_BTN_TOPPING_3,
        [GPIO_BTN_DISPENSE] = GPIO_PIN_BTN_DISPENSE,
        [GPIO_BTN_CANCEL] = GPIO_PIN_BTN_CANCEL
    },
    .led_pins = {
        [GPIO_LED_READY] = GPIO_PIN_LED_READY,
        [GPIO_LED_BUSY] = GPIO_PIN_LED_BUSY,
        [GPIO_LED_ERROR] = GPIO_PIN_LED_ERROR,
        [GPIO_LED_FLAVOR_1] = GPIO_PIN_LED_FLAVOR_1,
        [GPIO_LED_FLAVOR_2] = GPIO_PIN_LED_FLAVOR_2,
        [GPIO_LED_FLAVOR_3] = GPIO_PIN_LED_FLAVOR_3,
        [GPIO_LED_TOPPING_1] = GPIO_PIN_LED_TOPPING_1,
        [GPIO_LED_TOPPING_2] = GPIO_PIN_LED_TOPPING_2,
        [GPIO_LED_TOPPING_3] = GPIO_PIN_LED_TOPPING_3
    }
};

#endif // GPIO_CONFIG_H