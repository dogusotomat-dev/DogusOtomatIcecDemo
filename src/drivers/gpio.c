#include "gpio.h"
#include "../utils/logger.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>

#ifdef WIRING_PI_AVAILABLE
#include <wiringPi.h>
#else
// Mock implementations for systems without wiringPi
#define INPUT 0
#define OUTPUT 1
#define HIGH 1
#define LOW 0
static int wiringPiSetup(void) { return 0; }
static void pinMode(int pin, int mode) { /* Mock */ }
static void digitalWrite(int pin, int value) { /* Mock */ }
static int digitalRead(int pin) { return 1; }  // Always return HIGH for mock
static unsigned int millis(void) { 
    static unsigned int counter = 0;
    return counter++;
}
#endif

// Global GPIO driver instance
static gpio_driver_t g_gpio_driver = {
    .buttons = {
        [GPIO_BTN_START] = {.pin_number = GPIO_PIN_BTN_START, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_FLAVOR_1] = {.pin_number = GPIO_PIN_BTN_FLAVOR_1, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_FLAVOR_2] = {.pin_number = GPIO_PIN_BTN_FLAVOR_2, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_FLAVOR_3] = {.pin_number = GPIO_PIN_BTN_FLAVOR_3, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_TOPPING_1] = {.pin_number = GPIO_PIN_BTN_TOPPING_1, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_TOPPING_2] = {.pin_number = GPIO_PIN_BTN_TOPPING_2, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_TOPPING_3] = {.pin_number = GPIO_PIN_BTN_TOPPING_3, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_DISPENSE] = {.pin_number = GPIO_PIN_BTN_DISPENSE, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD},
        [GPIO_BTN_CANCEL] = {.pin_number = GPIO_PIN_BTN_CANCEL, .debounce_time_ms = GPIO_BTN_DEBOUNCE_TIME_MS, .filter_threshold = GPIO_BTN_FILTER_THRESHOLD}
    },
    .leds = {
        [GPIO_LED_READY] = {.pin_number = GPIO_PIN_LED_READY, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_BUSY] = {.pin_number = GPIO_PIN_LED_BUSY, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_ERROR] = {.pin_number = GPIO_PIN_LED_ERROR, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_FLAVOR_1] = {.pin_number = GPIO_PIN_LED_FLAVOR_1, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_FLAVOR_2] = {.pin_number = GPIO_PIN_LED_FLAVOR_2, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_FLAVOR_3] = {.pin_number = GPIO_PIN_LED_FLAVOR_3, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_TOPPING_1] = {.pin_number = GPIO_PIN_LED_TOPPING_1, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_TOPPING_2] = {.pin_number = GPIO_PIN_LED_TOPPING_2, .default_state = GPIO_LED_DEFAULT_STATE},
        [GPIO_LED_TOPPING_3] = {.pin_number = GPIO_PIN_LED_TOPPING_3, .default_state = GPIO_LED_DEFAULT_STATE}
    },
    .initialized = 0
};

gpio_status_t gpio_init(void) {
    // Check if already initialized
    if (g_gpio_driver.initialized) {
        LOG_WARN("GPIO driver already initialized");
        return GPIO_STATUS_OK;
    }
    
    // Initialize wiringPi library
    if (wiringPiSetup() == -1) {
        LOG_ERROR("Failed to initialize wiringPi library");
        return GPIO_STATUS_ERROR_INIT;
    }
    
    // Initialize mutex
    int result = pthread_mutex_init(&g_gpio_driver.gpio_mutex, NULL);
    if (result != 0) {
        LOG_ERROR("Failed to initialize GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    // Configure button pins as inputs with pull-up resistors
    for (int i = 0; i < GPIO_BTN_COUNT; i++) {
        pinMode(g_gpio_driver.buttons[i].pin_number, INPUT);
        // Note: wiringPi doesn't directly support pull-up configuration in this simple form
        // In a real implementation, you would use pullUpDnControl() function
        LOG_DEBUG("Configured button %s on pin %d as input", 
                  gpio_button_to_string((gpio_button_t)i), 
                  g_gpio_driver.buttons[i].pin_number);
    }
    
    // Configure LED pins as outputs
    for (int i = 0; i < GPIO_LED_COUNT; i++) {
        pinMode(g_gpio_driver.leds[i].pin_number, OUTPUT);
        digitalWrite(g_gpio_driver.leds[i].pin_number, g_gpio_driver.leds[i].default_state);
        LOG_DEBUG("Configured LED %s on pin %d as output (default: %d)", 
                  gpio_led_to_string((gpio_led_t)i), 
                  g_gpio_driver.leds[i].pin_number,
                  g_gpio_driver.leds[i].default_state);
    }
    
    g_gpio_driver.initialized = 1;
    LOG_INFO("GPIO driver initialized successfully");
    return GPIO_STATUS_OK;
}

gpio_status_t gpio_destroy(void) {
    if (!g_gpio_driver.initialized) {
        LOG_WARN("GPIO driver not initialized");
        return GPIO_STATUS_OK;
    }
    
    // Turn off all LEDs
    gpio_set_all_leds(0);
    
    // Destroy mutex
    int result = pthread_mutex_destroy(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to destroy GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    g_gpio_driver.initialized = 0;
    LOG_INFO("GPIO driver destroyed");
    return GPIO_STATUS_OK;
}

// Noise filtering function for button readings
static gpio_button_state_t filter_button_reading(int pin, int filter_threshold) {
    int high_count = 0;
    int low_count = 0;
    
    // Take multiple readings
    for (int i = 0; i < filter_threshold * 2; i++) {
        if (digitalRead(pin) == HIGH) {
            high_count++;
        } else {
            low_count++;
        }
        
        // Small delay between readings
        usleep(1000);  // 1ms
    }
    
    // Return the state that occurred most frequently
    if (high_count > low_count) {
        return GPIO_BTN_RELEASED;  // HIGH = not pressed (pull-up)
    } else {
        return GPIO_BTN_PRESSED;   // LOW = pressed
    }
}

// Debounce function for button readings
static gpio_button_state_t debounce_button(gpio_button_t button) {
    if (!g_gpio_driver.initialized) {
        return GPIO_BTN_UNKNOWN;
    }
    
    if (button >= GPIO_BTN_COUNT) {
        LOG_ERROR("Invalid button: %d", button);
        return GPIO_BTN_UNKNOWN;
    }
    
    int pin = g_gpio_driver.buttons[button].pin_number;
    int debounce_time = g_gpio_driver.buttons[button].debounce_time_ms;
    int filter_threshold = g_gpio_driver.buttons[button].filter_threshold;
    
    // Take initial reading
    gpio_button_state_t initial_state = filter_button_reading(pin, filter_threshold);
    
    // Wait for debounce time
    usleep(debounce_time * 1000);
    
    // Take second reading
    gpio_button_state_t second_state = filter_button_reading(pin, filter_threshold);
    
    // Return state only if both readings match
    if (initial_state == second_state) {
        return initial_state;
    } else {
        LOG_DEBUG("Button %s debounce failed - inconsistent readings", gpio_button_to_string(button));
        return GPIO_BTN_UNKNOWN;
    }
}

gpio_button_state_t gpio_read_button(gpio_button_t button) {
    if (!g_gpio_driver.initialized) {
        LOG_ERROR("GPIO driver not initialized");
        return GPIO_BTN_UNKNOWN;
    }
    
    if (button >= GPIO_BTN_COUNT) {
        LOG_ERROR("Invalid button: %d", button);
        return GPIO_BTN_UNKNOWN;
    }
    
    // Lock mutex for thread safety
    int result = pthread_mutex_lock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to lock GPIO mutex: %d", result);
        return GPIO_BTN_UNKNOWN;
    }
    
    gpio_button_state_t state = debounce_button(button);
    
    // Unlock mutex
    result = pthread_mutex_unlock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to unlock GPIO mutex: %d", result);
    }
    
    return state;
}

gpio_status_t gpio_write_led(gpio_led_t led, int state) {
    if (!g_gpio_driver.initialized) {
        LOG_ERROR("GPIO driver not initialized");
        return GPIO_STATUS_ERROR_NOT_INITIALIZED;
    }
    
    if (led >= GPIO_LED_COUNT) {
        LOG_ERROR("Invalid LED: %d", led);
        return GPIO_STATUS_ERROR_INVALID_PIN;
    }
    
    if (state != 0 && state != 1) {
        LOG_ERROR("Invalid LED state: %d", state);
        return GPIO_STATUS_ERROR_INVALID_STATE;
    }
    
    // Lock mutex for thread safety
    int result = pthread_mutex_lock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to lock GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    int pin = g_gpio_driver.leds[led].pin_number;
    digitalWrite(pin, state);
    
    LOG_DEBUG("Set LED %s on pin %d to state %d", 
              gpio_led_to_string(led), pin, state);
    
    // Unlock mutex
    result = pthread_mutex_unlock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to unlock GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    return GPIO_STATUS_OK;
}

gpio_status_t gpio_toggle_led(gpio_led_t led) {
    if (!g_gpio_driver.initialized) {
        LOG_ERROR("GPIO driver not initialized");
        return GPIO_STATUS_ERROR_NOT_INITIALIZED;
    }
    
    if (led >= GPIO_LED_COUNT) {
        LOG_ERROR("Invalid LED: %d", led);
        return GPIO_STATUS_ERROR_INVALID_PIN;
    }
    
    // Lock mutex for thread safety
    int result = pthread_mutex_lock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to lock GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    int pin = g_gpio_driver.leds[led].pin_number;
    int current_state = digitalRead(pin);
    int new_state = !current_state;
    
    digitalWrite(pin, new_state);
    
    LOG_DEBUG("Toggled LED %s on pin %d from %d to %d", 
              gpio_led_to_string(led), pin, current_state, new_state);
    
    // Unlock mutex
    result = pthread_mutex_unlock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to unlock GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    return GPIO_STATUS_OK;
}

gpio_status_t gpio_set_all_leds(int state) {
    if (!g_gpio_driver.initialized) {
        LOG_ERROR("GPIO driver not initialized");
        return GPIO_STATUS_ERROR_NOT_INITIALIZED;
    }
    
    if (state != 0 && state != 1) {
        LOG_ERROR("Invalid LED state: %d", state);
        return GPIO_STATUS_ERROR_INVALID_STATE;
    }
    
    // Lock mutex for thread safety
    int result = pthread_mutex_lock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to lock GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    for (int i = 0; i < GPIO_LED_COUNT; i++) {
        int pin = g_gpio_driver.leds[i].pin_number;
        digitalWrite(pin, state);
        LOG_DEBUG("Set LED %s on pin %d to state %d", 
                  gpio_led_to_string((gpio_led_t)i), pin, state);
    }
    
    // Unlock mutex
    result = pthread_mutex_unlock(&g_gpio_driver.gpio_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to unlock GPIO mutex: %d", result);
        return GPIO_STATUS_ERROR_INIT;
    }
    
    return GPIO_STATUS_OK;
}

const char* gpio_button_to_string(gpio_button_t button) {
    switch (button) {
        case GPIO_BTN_START: return "START";
        case GPIO_BTN_FLAVOR_1: return "FLAVOR_1";
        case GPIO_BTN_FLAVOR_2: return "FLAVOR_2";
        case GPIO_BTN_FLAVOR_3: return "FLAVOR_3";
        case GPIO_BTN_TOPPING_1: return "TOPPING_1";
        case GPIO_BTN_TOPPING_2: return "TOPPING_2";
        case GPIO_BTN_TOPPING_3: return "TOPPING_3";
        case GPIO_BTN_DISPENSE: return "DISPENSE";
        case GPIO_BTN_CANCEL: return "CANCEL";
        default: return "UNKNOWN";
    }
}

const char* gpio_led_to_string(gpio_led_t led) {
    switch (led) {
        case GPIO_LED_READY: return "READY";
        case GPIO_LED_BUSY: return "BUSY";
        case GPIO_LED_ERROR: return "ERROR";
        case GPIO_LED_FLAVOR_1: return "FLAVOR_1";
        case GPIO_LED_FLAVOR_2: return "FLAVOR_2";
        case GPIO_LED_FLAVOR_3: return "FLAVOR_3";
        case GPIO_LED_TOPPING_1: return "TOPPING_1";
        case GPIO_LED_TOPPING_2: return "TOPPING_2";
        case GPIO_LED_TOPPING_3: return "TOPPING_3";
        default: return "UNKNOWN";
    }
}