#include "uart.h"
#include "../utils/logger.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <termios.h>
#endif

// Static variables
static uint32_t current_baud_rate = 0;
static uint32_t timeout_ms = 1000;  // Default timeout: 1 second
static int uart_initialized = 0;

#ifdef _WIN32
static HANDLE hSerial = INVALID_HANDLE_VALUE;
#else
static int serial_fd = -1;
#endif

uart_error_t uart_init(const uart_config_t* config) {
    // Validate input
    if (config == NULL) {
        LOG_ERROR("Invalid configuration parameter");
        return UART_ERROR_INVALID_BAUDRATE;
    }
    
    // Validate baud rate
    switch (config->baud_rate) {
        case 9600:
        case 19200:
        case 38400:
        case 57600:
        case 115200:
            break;
        default:
            LOG_ERROR("Invalid baud rate: %lu", config->baud_rate);
            return UART_ERROR_INVALID_BAUDRATE;
    }
    
    current_baud_rate = config->baud_rate;
    timeout_ms = config->timeout_ms;
    
    LOG_INFO("Initializing UART with baud rate: %lu, timeout: %lu ms", 
             current_baud_rate, timeout_ms);
    
    // In a real implementation, this would open the serial port
    // and configure it with the specified parameters
    #ifdef _WIN32
        // Windows implementation would go here
        // hSerial = CreateFile(...);
        // if (hSerial == INVALID_HANDLE_VALUE) {
        //     LOG_ERROR("Failed to open serial port");
        //     return UART_ERROR_PORT_NOT_OPEN;
        // }
    #else
        // Unix implementation would go here
        // serial_fd = open(UART_DEVICE, O_RDWR | O_NOCTTY | O_NDELAY);
        // if (serial_fd == -1) {
        //     LOG_ERROR("Failed to open serial port: %s", strerror(errno));
        //     return UART_ERROR_PORT_NOT_OPEN;
        // }
    #endif
    
    uart_initialized = 1;
    LOG_INFO("UART initialized successfully");
    return UART_SUCCESS;
}

uart_error_t uart_send_byte(uint8_t byte) {
    if (!uart_initialized) {
        LOG_ERROR("UART not initialized");
        return UART_ERROR_PORT_NOT_OPEN;
    }
    
    LOG_DEBUG("Sending byte: 0x%02X", byte);
    
    // In a real implementation, this would write to the serial port
    #ifdef _WIN32
        // DWORD bytes_written;
        // if (!WriteFile(hSerial, &byte, 1, &bytes_written, NULL)) {
        //     LOG_ERROR("Failed to send byte");
        //     return UART_ERROR_WRITE_FAILED;
        // }
    #else
        // if (write(serial_fd, &byte, 1) != 1) {
        //     LOG_ERROR("Failed to send byte: %s", strerror(errno));
        //     return UART_ERROR_WRITE_FAILED;
        // }
    #endif
    
    return UART_SUCCESS;
}

uart_error_t uart_receive_byte(uint8_t* byte) {
    if (!uart_initialized) {
        LOG_ERROR("UART not initialized");
        return UART_ERROR_PORT_NOT_OPEN;
    }
    
    if (byte == NULL) {
        LOG_ERROR("Invalid parameter: byte pointer is NULL");
        return UART_ERROR_READ_FAILED;
    }
    
    // Record start time for timeout
    time_t start_time = time(NULL);
    
    // Wait for data to be available or timeout
    while (!uart_data_available()) {
        // Check for timeout
        if (difftime(time(NULL), start_time) * 1000 >= timeout_ms) {
            LOG_WARN("Timeout waiting for data");
            return UART_ERROR_TIMEOUT;
        }
        
        // Small delay to prevent busy waiting
        #ifdef _WIN32
            Sleep(1);
        #else
            usleep(1000);  // 1ms
        #endif
    }
    
    // In a real implementation, this would read from the serial port
    *byte = 0;  // Placeholder value
    
    #ifdef _WIN32
        // DWORD bytes_read;
        // if (!ReadFile(hSerial, byte, 1, &bytes_read, NULL)) {
        //     LOG_ERROR("Failed to receive byte");
        //     return UART_ERROR_READ_FAILED;
        // }
        // if (bytes_read != 1) {
        //     LOG_WARN("No data received");
        //     return UART_ERROR_TIMEOUT;
        // }
    #else
        // ssize_t result = read(serial_fd, byte, 1);
        // if (result == -1) {
        //     LOG_ERROR("Failed to receive byte: %s", strerror(errno));
        //     return UART_ERROR_READ_FAILED;
        // }
        // if (result == 0) {
        //     LOG_WARN("No data received");
        //     return UART_ERROR_TIMEOUT;
        // }
    #endif
    
    LOG_DEBUG("Received byte: 0x%02X", *byte);
    return UART_SUCCESS;
}

uart_error_t uart_send_buffer(const uint8_t* buffer, uint16_t length) {
    if (!uart_initialized) {
        LOG_ERROR("UART", "UART not initialized");
        return UART_ERROR_PORT_NOT_OPEN;
    }
    
    if (buffer == NULL) {
        LOG_ERROR("UART", "Invalid parameter: buffer pointer is NULL");
        return UART_ERROR_WRITE_FAILED;
    }
    
    if (length == 0) {
        LOG_WARN("UART", "Attempting to send buffer of zero length");
        return UART_SUCCESS;
    }
    
    LOG_INFO("UART", "Sending buffer of length: %u", length);
    
    for (uint16_t i = 0; i < length; i++) {
        uart_error_t result = uart_send_byte(buffer[i]);
        if (result != UART_SUCCESS) {
            LOG_ERROR("UART", "Failed to send buffer at index %u", i);
            return result;
        }
    }
    
    return UART_SUCCESS;
}

uart_error_t uart_receive_buffer(uint8_t* buffer, uint16_t length, uint16_t* received_length) {
    if (!uart_initialized) {
        LOG_ERROR("UART", "UART not initialized");
        return UART_ERROR_PORT_NOT_OPEN;
    }
    
    if (buffer == NULL) {
        LOG_ERROR("UART", "Invalid parameter: buffer pointer is NULL");
        return UART_ERROR_READ_FAILED;
    }
    
    if (received_length == NULL) {
        LOG_ERROR("UART", "Invalid parameter: received_length pointer is NULL");
        return UART_ERROR_READ_FAILED;
    }
    
    if (length == 0) {
        LOG_WARN("UART", "Attempting to receive buffer of zero length");
        *received_length = 0;
        return UART_SUCCESS;
    }
    
    LOG_INFO("UART", "Receiving buffer of maximum length: %u", length);
    
    uint16_t count = 0;
    time_t start_time = time(NULL);
    
    for (uint16_t i = 0; i < length; i++) {
        // Check for timeout
        if (difftime(time(NULL), start_time) * 1000 >= timeout_ms) {
            LOG_WARN("UART", "Timeout while receiving buffer at index %u", i);
            break;
        }
        
        // Check if data is available
        if (!uart_data_available()) {
            // Small delay before checking again
            #ifdef _WIN32
                Sleep(1);
            #else
                usleep(1000);  // 1ms
            #endif
            
            // Check timeout again after delay
            if (difftime(time(NULL), start_time) * 1000 >= timeout_ms) {
                LOG_WARN("UART", "Timeout while receiving buffer at index %u", i);
                break;
            }
            
            // If still no data, continue to next iteration
            if (!uart_data_available()) {
                i--;  // Decrement to retry this position
                continue;
            }
        }
        
        // Receive byte
        uint8_t byte;
        uart_error_t result = uart_receive_byte(&byte);
        if (result == UART_SUCCESS) {
            buffer[i] = byte;
            count++;
        } else if (result == UART_ERROR_TIMEOUT) {
            LOG_WARN("UART", "Timeout while receiving buffer at index %u", i);
            break;
        } else {
            LOG_ERROR("UART", "Failed to receive buffer at index %u", i);
            *received_length = count;
            return result;
        }
    }
    
    *received_length = count;
    LOG_INFO("UART", "Received %u bytes out of requested %u", count, length);
    
    return UART_SUCCESS;
}

int uart_data_available(void) {
    if (!uart_initialized) {
        return 0;
    }
    
    // In a real implementation, this would check if data is available in the serial buffer
    #ifdef _WIN32
        // DWORD errors;
        // COMSTAT comstat;
        // if (ClearCommError(hSerial, &errors, &comstat) && comstat.cbInQue > 0) {
        //     return 1;
        // }
    #else
        // fd_set read_fds;
        // struct timeval timeout;
        // 
        // FD_ZERO(&read_fds);
        // FD_SET(serial_fd, &read_fds);
        // 
        // timeout.tv_sec = 0;
        // timeout.tv_usec = 0;
        // 
        // int result = select(serial_fd + 1, &read_fds, NULL, NULL, &timeout);
        // if (result > 0 && FD_ISSET(serial_fd, &read_fds)) {
        //     return 1;
        // }
    #endif
    
    // Simulate random data availability for demonstration
    static int counter = 0;
    counter++;
    return (counter % 100) == 0;  // Simulate data available 1% of the time
}

void uart_close(void) {
    if (!uart_initialized) {
        return;
    }
    
    LOG_INFO("UART", "Closing UART connection");
    
    #ifdef _WIN32
        if (hSerial != INVALID_HANDLE_VALUE) {
            CloseHandle(hSerial);
            hSerial = INVALID_HANDLE_VALUE;
        }
    #else
        if (serial_fd != -1) {
            close(serial_fd);
            serial_fd = -1;
        }
    #endif
    
    uart_initialized = 0;
    current_baud_rate = 0;
    timeout_ms = 1000;
    LOG_INFO("UART", "UART connection closed");
}