#ifndef UART_H
#define UART_H

#include <stdint.h>
#include "../config.h"

// UART error codes
typedef enum {
    UART_SUCCESS = 0,
    UART_ERROR_INVALID_BAUDRATE,
    UART_ERROR_PORT_NOT_OPEN,
    UART_ERROR_TIMEOUT,
    UART_ERROR_READ_FAILED,
    UART_ERROR_WRITE_FAILED
} uart_error_t;

// UART configuration structure
typedef struct {
    uint32_t baud_rate;
    uint32_t timeout_ms;
} uart_config_t;

// UART driver interface
uart_error_t uart_init(const uart_config_t* config);
uart_error_t uart_send_byte(uint8_t byte);
uart_error_t uart_receive_byte(uint8_t* byte);
uart_error_t uart_send_buffer(const uint8_t* buffer, uint16_t length);
uart_error_t uart_receive_buffer(uint8_t* buffer, uint16_t length, uint16_t* received_length);
int uart_data_available(void);
void uart_close(void);

#endif // UART_H