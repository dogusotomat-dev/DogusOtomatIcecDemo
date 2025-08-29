// Parse Iceboard response
iceboard_status_t parse_iceboard_response(const uint8_t* response_data, uint16_t response_length, iceboard_response_t* parsed_response) {
    if (response_data == NULL || parsed_response == NULL) {
        LOG_ERROR("Invalid parameter for response parsing");
        return ICEBOARD_STATUS_ERROR;
    }
    
    if (response_length == 0) {
        LOG_ERROR("Empty response data");
        return ICEBOARD_STATUS_ERROR;
    }
    
    // Initialize parsed response structure
    memset(parsed_response, 0, sizeof(iceboard_response_t));
    parsed_response->type = ICEBOARD_RESPONSE_UNKNOWN;
    parsed_response->error_code = ICEBOARD_ERROR_NONE;
    
    // Parse based on first byte
    uint8_t first_byte = response_data[0];
    
    switch (first_byte) {
        case ICEBOARD_ACK:
            parsed_response->type = ICEBOARD_RESPONSE_ACK;
            LOG_DEBUG("Parsed ACK response");
            break;
            
        case ICEBOARD_NACK:
            parsed_response->type = ICEBOARD_RESPONSE_NACK;
            
            // If there's additional data, it might contain error information
            if (response_length > 1) {
                parsed_response->error_code = (iceboard_error_code_t)response_data[1];
                
                // Try to extract error message if present
                if (response_length > 2) {
                    uint8_t msg_length = response_length - 2;
                    if (msg_length > sizeof(parsed_response->error_message) - 1) {
                        msg_length = sizeof(parsed_response->error_message) - 1;
                    }
                    memcpy(parsed_response->error_message, &response_data[2], msg_length);
                    parsed_response->error_message[msg_length] = '\0';
                }
            }
            
            LOG_DEBUG("Parsed NACK response, error code: %d, message: %s", 
                     parsed_response->error_code, parsed_response->error_message);
            return ICEBOARD_STATUS_NACK_RECEIVED;
            
        case 0x01: // STATUS response
            parsed_response->type = ICEBOARD_RESPONSE_STATUS;
            
            // Extract slot number (if present)
            if (response_length > 1) {
                parsed_response->slot_number = response_data[1];
            }
            
            // Copy any additional data
            if (response_length > 2) {
                uint8_t data_length = response_length - 2;
                if (data_length > sizeof(parsed_response->data)) {
                    data_length = sizeof(parsed_response->data);
                }
                memcpy(parsed_response->data, &response_data[2], data_length);
                parsed_response->data_length = data_length;
            }
            
            LOG_DEBUG("Parsed STATUS response, slot: %d, data length: %d", 
                     parsed_response->slot_number, parsed_response->data_length);
            break;
            
        case 0x02: // ERROR response
            parsed_response->type = ICEBOARD_RESPONSE_ERROR;
            
            // Extract error code
            if (response_length > 1) {
                parsed_response->error_code = (iceboard_error_code_t)response_data[1];
            }
            
            // Extract slot number (if present)
            if (response_length > 2) {
                parsed_response->slot_number = response_data[2];
            }
            
            // Extract error message
            if (response_length > 3) {
                uint8_t msg_length = response_length - 3;
                if (msg_length > sizeof(parsed_response->error_message) - 1) {
                    msg_length = sizeof(parsed_response->error_message) - 1;
                }
                memcpy(parsed_response->error_message, &response_data[3], msg_length);
                parsed_response->error_message[msg_length] = '\0';
            }
            
            LOG_DEBUG("Parsed ERROR response, code: %d, slot: %d, message: %s", 
                     parsed_response->error_code, parsed_response->slot_number, parsed_response->error_message);
            return ICEBOARD_STATUS_ERROR;
            
        default:
            parsed_response->type = ICEBOARD_RESPONSE_UNKNOWN;
            LOG_WARN("Unknown response type: 0x%02X", first_byte);
            
            // Copy raw data for debugging
            if (response_length > sizeof(parsed_response->data)) {
                parsed_response->data_length = sizeof(parsed_response->data);
            } else {
                parsed_response->data_length = response_length;
            }
            memcpy(parsed_response->data, response_data, parsed_response->data_length);
            break;
    }
    
    return ICEBOARD_STATUS_OK;
}