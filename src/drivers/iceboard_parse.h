#ifndef ICEBOARD_PARSE_H
#define ICEBOARD_PARSE_H

#include "iceboard.h"

// Function prototypes
iceboard_status_t parse_iceboard_response(const uint8_t* response_data, uint16_t response_length, iceboard_response_t* parsed_response);

#endif // ICEBOARD_PARSE_H