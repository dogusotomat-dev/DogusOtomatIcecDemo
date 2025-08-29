#ifndef CONFIG_H
#define CONFIG_H

// System configuration parameters
#define SYSTEM_CLOCK_FREQ 16000000  // 16 MHz

// UART/Serial Communication Configuration
#define UART_DEVICE "/dev/ttyUSB0"  // Default serial device
#define BAUD_RATE 9600              // Default baud rate
#define UART_BUFFER_SIZE 256        // UART buffer size
#define UART_TIMEOUT_MS 2000        // UART timeout in milliseconds
#define UART_RETRY_COUNT 3          // Number of retry attempts

// Iceboard Configuration
#define ICEBOARD_DEFAULT_TIMEOUT_MS 2000
#define ICEBOARD_DEFAULT_RETRY_COUNT 3
#define MAX_FLAVORS 10
#define MAX_TOPPINGS 20

// GPIO Configuration
// Button pins (wiringPi pin numbers)
#define GPIO_PIN_BTN_START 0
#define GPIO_PIN_BTN_FLAVOR_1 1
#define GPIO_PIN_BTN_FLAVOR_2 2
#define GPIO_PIN_BTN_FLAVOR_3 3
#define GPIO_PIN_BTN_TOPPING_1 4
#define GPIO_PIN_BTN_TOPPING_2 5
#define GPIO_PIN_BTN_TOPPING_3 6
#define GPIO_PIN_BTN_DISPENSE 7
#define GPIO_PIN_BTN_CANCEL 8

// LED pins (wiringPi pin numbers)
#define GPIO_PIN_LED_READY 9
#define GPIO_PIN_LED_BUSY 10
#define GPIO_PIN_LED_ERROR 11
#define GPIO_PIN_LED_FLAVOR_1 12
#define GPIO_PIN_LED_FLAVOR_2 13
#define GPIO_PIN_LED_FLAVOR_3 14
#define GPIO_PIN_LED_TOPPING_1 15
#define GPIO_PIN_LED_TOPPING_2 16
#define GPIO_PIN_LED_TOPPING_3 17

// GPIO Settings
#define GPIO_BTN_DEBOUNCE_TIME_MS 50
#define GPIO_BTN_FILTER_THRESHOLD 5
#define GPIO_LED_DEFAULT_STATE 0  // 0=OFF, 1=ON

// State Machine Configuration
#define STATE_MACHINE_UPDATE_INTERVAL_MS 100  // State machine update interval in milliseconds

// Logging Configuration
#define LOG_FILE_PATH "icec.log"              // Default log file path
#define LOG_MAX_FILE_SIZE (1024 * 1024)      // 1MB maximum log file size
#define LOG_MAX_FILES 5                       // Number of backup log files
#define LOG_BUFFER_SIZE 512                   // Log buffer size

// Log levels (for compile-time optimization)
#define LOG_LEVEL_DEBUG 0
#define LOG_LEVEL_INFO 1
#define LOG_LEVEL_WARN 2
#define LOG_LEVEL_ERROR 3
#define LOG_LEVEL_OFF 4

// Default log level
#ifndef LOG_LEVEL
#define LOG_LEVEL LOG_LEVEL_DEBUG
#endif

// Feature flags
#define FEATURE_ICEBOARD_COMMUNICATION 1
#define FEATURE_GPIO_CONTROL 1
#define FEATURE_UART_DEBUGGING 1

#endif // CONFIG_H