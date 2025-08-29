#ifndef GPIO_H
#define GPIO_H

#include <stdint.h>
#include <pthread.h>
#include "gpio_config.h"

#ifdef __cplusplus
extern "C" {
#endif

// GPIO pin definitions for the ice cream vending machine
typedef enum {
    // Input pins (buttons)
    GPIO_BTN_START = 0,        // Start button
    GPIO_BTN_FLAVOR_1,         // Flavor 1 selection button
    GPIO_BTN_FLAVOR_2,         // Flavor 2 selection button
    GPIO_BTN_FLAVOR_3,         // Flavor 3 selection button
    GPIO_BTN_TOPPING_1,        // Topping 1 selection button
    GPIO_BTN_TOPPING_2,        // Topping 2 selection button
    GPIO_BTN_TOPPING_3,        // Topping 3 selection button
    GPIO_BTN_DISPENSE,         // Dispense confirmation button
    GPIO_BTN_CANCEL,           // Cancel/Reset button
    GPIO_BTN_COUNT             // Total number of buttons
} gpio_button_t;

// Output pins (LEDs)
typedef enum {
    GPIO_LED_READY = 0,        // Ready indicator LED
    GPIO_LED_BUSY,             // Busy/Processing LED
    GPIO_LED_ERROR,            // Error indicator LED
    GPIO_LED_FLAVOR_1,         // Flavor 1 indicator LED
    GPIO_LED_FLAVOR_2,         // Flavor 2 indicator LED
    GPIO_LED_FLAVOR_3,         // Flavor 3 indicator LED
    GPIO_LED_TOPPING_1,        // Topping 1 indicator LED
    GPIO_LED_TOPPING_2,        // Topping 2 indicator LED
    GPIO_LED_TOPPING_3,        // Topping 3 indicator LED
    GPIO_LED_COUNT             // Total number of LEDs
} gpio_led_t;

// Button states
typedef enum {
    GPIO_BTN_RELEASED = 0,     // Button not pressed
    GPIO_BTN_PRESSED,          // Button pressed
    GPIO_BTN_UNKNOWN           // Unknown state
} gpio_button_state_t;

// GPIO driver status codes
typedef enum {
    GPIO_STATUS_OK = 0,
    GPIO_STATUS_ERROR_INIT,
    GPIO_STATUS_ERROR_INVALID_PIN,
    GPIO_STATUS_ERROR_INVALID_STATE,
    GPIO_STATUS_ERROR_NOT_INITIALIZED
} gpio_status_t;

// Button configuration structure
typedef struct {
    int pin_number;            // Physical pin number
    int debounce_time_ms;      // Debounce time in milliseconds
    int filter_threshold;      // Noise filter threshold
} gpio_button_config_t;

// LED configuration structure
typedef struct {
    int pin_number;            // Physical pin number
    int default_state;         // Default state (0=OFF, 1=ON)
} gpio_led_config_t;

// GPIO driver structure
typedef struct {
    gpio_button_config_t buttons[GPIO_BTN_COUNT];
    gpio_led_config_t leds[GPIO_LED_COUNT];
    int initialized;
    pthread_mutex_t gpio_mutex;
} gpio_driver_t;

// Function prototypes
gpio_status_t gpio_init(void);
gpio_status_t gpio_destroy(void);
gpio_button_state_t gpio_read_button(gpio_button_t button);
gpio_status_t gpio_write_led(gpio_led_t led, int state);
gpio_status_t gpio_toggle_led(gpio_led_t led);
gpio_status_t gpio_set_all_leds(int state);
const char* gpio_button_to_string(gpio_button_t button);
const char* gpio_led_to_string(gpio_led_t led);

#ifdef __cplusplus
}
#endif

#endif // GPIO_H