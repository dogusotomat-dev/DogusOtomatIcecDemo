#ifndef ICEBOARD_H
#define ICEBOARD_H

#include <stdint.h>
#include "../config.h"

// Iceboard status codes
typedef enum {
    ICEBOARD_STATUS_OK = 0,
    ICEBOARD_STATUS_ERROR,
    ICEBOARD_STATUS_BUSY,
    ICEBOARD_STATUS_TIMEOUT,
    ICEBOARD_STATUS_INVALID_RESPONSE,
    ICEBOARD_STATUS_NACK_RECEIVED
} iceboard_status_t;

// ACK/NACK codes
typedef enum {
    ICEBOARD_ACK = 0x06,
    ICEBOARD_NACK = 0x15
} iceboard_ack_nack_t;

// Response types
typedef enum {
    ICEBOARD_RESPONSE_ACK = 0,
    ICEBOARD_RESPONSE_NACK,
    ICEBOARD_RESPONSE_STATUS,
    ICEBOARD_RESPONSE_ERROR,
    ICEBOARD_RESPONSE_UNKNOWN
} iceboard_response_type_t;

// Error codes
typedef enum {
    ICEBOARD_ERROR_NONE = 0,
    ICEBOARD_ERROR_INVALID_COMMAND,
    ICEBOARD_ERROR_INVALID_PARAMETER,
    ICEBOARD_ERROR_BUSY,
    ICEBOARD_ERROR_HARDWARE_FAULT,
    ICEBOARD_ERROR_COMMUNICATION_ERROR,
    ICEBOARD_ERROR_TIMEOUT,
    ICEBOARD_ERROR_UNKNOWN
} iceboard_error_code_t;

// Response structure
typedef struct {
    iceboard_response_type_t type;
    uint8_t slot_number;
    iceboard_error_code_t error_code;
    char error_message[64];
    uint8_t data[32];
    uint8_t data_length;
} iceboard_response_t;

// Flavor types
typedef enum {
    FLAVOR_VANILLA = 0,
    FLAVOR_CHOCOLATE,
    FLAVOR_STRAWBERRY,
    FLAVOR_PISTACHIO,
    FLAVOR_MAX
} flavor_t;

// Topping types
typedef enum {
    TOPPING_NONE = 0,
    TOPPING_CHOCOLATE_SYRUP,
    TOPPING_CARAMEL_SYRUP,
    TOPPING_WHIPPED_CREAM,
    TOPPING_NUTS,
    TOPPING_MAX
} topping_t;

// Command structure
typedef struct {
    uint8_t command;
    uint8_t data[3];
} iceboard_command_t;

// Function prototypes
void iceboard_init(void);
iceboard_status_t iceboard_send_command_with_retry(const iceboard_command_t* cmd, uint8_t* response, uint16_t response_length);
iceboard_status_t iceboard_dispense_flavor(flavor_t flavor, uint16_t amount_ml);
iceboard_status_t iceboard_add_topping(topping_t topping, uint16_t amount_ml);
iceboard_status_t iceboard_start_dispensing(void);
iceboard_status_t iceboard_stop_dispensing(void);
iceboard_status_t iceboard_get_status(void);
void iceboard_cleanup(void);

#endif // ICEBOARD_H