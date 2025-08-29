#include "test_framework.h"
#include "../src/drivers/iceboard.h"
#include "../src/drivers/iceboard_parse.h"
#include <string.h>

// Mock iceboard implementation for testing
static int mock_iceboard_initialized = 0;

void mock_iceboard_init(void) {
    mock_iceboard_initialized = 1;
}

TEST_CASE(iceboard_init) {
    mock_iceboard_init();
    return assert_true(mock_iceboard_initialized, "Iceboard should be initialized");
}

TEST_CASE(iceboard_command_structure) {
    iceboard_command_t cmd = {
        .command = 0x01,
        .data = {0x02, 0x03, 0x04}
    };
    
    TEST_CHECK(assert_equal_uint(0x01, cmd.command, "Command byte should be set correctly"));
    TEST_CHECK(assert_equal_uint(0x02, cmd.data[0], "First data byte should be set correctly"));
    TEST_CHECK(assert_equal_uint(0x03, cmd.data[1], "Second data byte should be set correctly"));
    TEST_CHECK(assert_equal_uint(0x04, cmd.data[2], "Third data byte should be set correctly"));
    
    return TEST_PASS;
}

TEST_CASE(iceboard_response_parsing_ack) {
    uint8_t response_data[] = {0x06}; // ACK
    iceboard_response_t parsed_response;
    
    iceboard_status_t result = parse_iceboard_response(response_data, sizeof(response_data), &parsed_response);
    
    TEST_CHECK(assert_equal_int(ICEBOARD_STATUS_OK, result, "Parsing should succeed"));
    TEST_CHECK(assert_equal_int(ICEBOARD_RESPONSE_ACK, parsed_response.type, "Response type should be ACK"));
    
    return TEST_PASS;
}

TEST_CASE(iceboard_response_parsing_nack) {
    uint8_t response_data[] = {0x15, 0x02}; // NACK with error code 2
    iceboard_response_t parsed_response;
    
    iceboard_status_t result = parse_iceboard_response(response_data, sizeof(response_data), &parsed_response);
    
    TEST_CHECK(assert_equal_int(ICEBOARD_STATUS_NACK_RECEIVED, result, "Parsing should return NACK status"));
    TEST_CHECK(assert_equal_int(ICEBOARD_RESPONSE_NACK, parsed_response.type, "Response type should be NACK"));
    TEST_CHECK(assert_equal_int(ICEBOARD_ERROR_BUSY, parsed_response.error_code, "Error code should be parsed correctly"));
    
    return TEST_PASS;
}

TEST_CASE(iceboard_response_parsing_status) {
    uint8_t response_data[] = {0x01, 0x03, 0x10, 0x20}; // STATUS, slot 3, data [0x10, 0x20]
    iceboard_response_t parsed_response;
    
    iceboard_status_t result = parse_iceboard_response(response_data, sizeof(response_data), &parsed_response);
    
    TEST_CHECK(assert_equal_int(ICEBOARD_STATUS_OK, result, "Parsing should succeed"));
    TEST_CHECK(assert_equal_int(ICEBOARD_RESPONSE_STATUS, parsed_response.type, "Response type should be STATUS"));
    TEST_CHECK(assert_equal_uint(3, parsed_response.slot_number, "Slot number should be parsed correctly"));
    TEST_CHECK(assert_equal_uint(2, parsed_response.data_length, "Data length should be correct"));
    TEST_CHECK(assert_equal_uint(0x10, parsed_response.data[0], "First data byte should be correct"));
    TEST_CHECK(assert_equal_uint(0x20, parsed_response.data[1], "Second data byte should be correct"));
    
    return TEST_PASS;
}

TEST_CASE(iceboard_response_parsing_error) {
    uint8_t response_data[] = {0x02, 0x04, 0x01, 'E', 'r', 'r', 'o', 'r'}; // ERROR, code 4, slot 1, message "Error"
    iceboard_response_t parsed_response;
    
    iceboard_status_t result = parse_iceboard_response(response_data, sizeof(response_data), &parsed_response);
    
    TEST_CHECK(assert_equal_int(ICEBOARD_STATUS_ERROR, result, "Parsing should return ERROR status"));
    TEST_CHECK(assert_equal_int(ICEBOARD_RESPONSE_ERROR, parsed_response.type, "Response type should be ERROR"));
    TEST_CHECK(assert_equal_int(ICEBOARD_ERROR_HARDWARE_FAULT, parsed_response.error_code, "Error code should be parsed correctly"));
    TEST_CHECK(assert_equal_uint(1, parsed_response.slot_number, "Slot number should be parsed correctly"));
    TEST_CHECK(assert_equal_str("Error", parsed_response.error_message, "Error message should be parsed correctly"));
    
    return TEST_PASS;
}

TEST_CASE(iceboard_flavor_enum) {
    // Test that flavor enum values are sequential
    TEST_CHECK(assert_equal_int(0, FLAVOR_VANILLA, "Vanilla should be 0"));
    TEST_CHECK(assert_equal_int(1, FLAVOR_CHOCOLATE, "Chocolate should be 1"));
    TEST_CHECK(assert_equal_int(2, FLAVOR_STRAWBERRY, "Strawberry should be 2"));
    TEST_CHECK(assert_equal_int(3, FLAVOR_PISTACHIO, "Pistachio should be 3"));
    TEST_CHECK(assert_equal_int(4, FLAVOR_MAX, "Max flavor should be 4"));
    
    return TEST_PASS;
}

TEST_CASE(iceboard_topping_enum) {
    // Test that topping enum values are sequential
    TEST_CHECK(assert_equal_int(0, TOPPING_NONE, "None should be 0"));
    TEST_CHECK(assert_equal_int(1, TOPPING_CHOCOLATE_SYRUP, "Chocolate syrup should be 1"));
    TEST_CHECK(assert_equal_int(2, TOPPING_CARAMEL_SYRUP, "Caramel syrup should be 2"));
    TEST_CHECK(assert_equal_int(3, TOPPING_WHIPPED_CREAM, "Whipped cream should be 3"));
    TEST_CHECK(assert_equal_int(4, TOPPING_NUTS, "Nuts should be 4"));
    TEST_CHECK(assert_equal_int(5, TOPPING_MAX, "Max topping should be 5"));
    
    return TEST_PASS;
}

test_suite_t* create_iceboard_test_suite(void) {
    test_suite_t* suite = test_suite_create("Iceboard Module Tests");
    if (suite == NULL) {
        return NULL;
    }
    
    ADD_TEST(suite, iceboard_init, true);
    ADD_TEST(suite, iceboard_command_structure, true);
    ADD_TEST(suite, iceboard_response_parsing_ack, true);
    ADD_TEST(suite, iceboard_response_parsing_nack, true);
    ADD_TEST(suite, iceboard_response_parsing_status, true);
    ADD_TEST(suite, iceboard_response_parsing_error, true);
    ADD_TEST(suite, iceboard_flavor_enum, true);
    ADD_TEST(suite, iceboard_topping_enum, true);
    
    return suite;
}