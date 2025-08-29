#include "test_framework.h"
#include "../src/core/state_machine.h"
#include <pthread.h>

TEST_CASE(state_machine_init_destroy) {
    state_machine_t sm;
    
    // Test initialization
    int result = state_machine_init(&sm);
    TEST_CHECK(assert_equal_int(0, result, "State machine initialization should succeed"));
    TEST_CHECK(assert_equal_int(STATE_IDLE, sm.current_state, "Initial state should be IDLE"));
    TEST_CHECK(assert_equal_int(1, sm.initialized, "Initialized flag should be set"));
    
    // Test destruction
    result = state_machine_destroy(&sm);
    TEST_CHECK(assert_equal_int(0, result, "State machine destruction should succeed"));
    TEST_CHECK(assert_equal_int(0, sm.initialized, "Initialized flag should be cleared"));
    
    return TEST_PASS;
}

TEST_CASE(state_machine_get_current_state) {
    state_machine_t sm;
    state_machine_init(&sm);
    
    state_t current_state = state_machine_get_current_state(&sm);
    TEST_CHECK(assert_equal_int(STATE_IDLE, current_state, "Current state should be IDLE"));
    
    state_machine_destroy(&sm);
    return TEST_PASS;
}

TEST_CASE(state_machine_invalid_pointer) {
    // Test with NULL pointer
    state_t state = state_machine_get_current_state(NULL);
    TEST_CHECK(assert_equal_int(STATE_ERROR, state, "Should return ERROR state for NULL pointer"));
    
    int result = state_machine_init(NULL);
    TEST_CHECK(assert_equal_int(-1, result, "Should return error for NULL init pointer"));
    
    result = state_machine_destroy(NULL);
    TEST_CHECK(assert_equal_int(-1, result, "Should return error for NULL destroy pointer"));
    
    result = state_machine_handle_event(NULL, EVENT_START);
    TEST_CHECK(assert_equal_int(-1, result, "Should return error for NULL event pointer"));
    
    return TEST_PASS;
}

TEST_CASE(state_machine_state_transitions) {
    state_machine_t sm;
    state_machine_init(&sm);
    
    // Test transition from IDLE to WAITING_RESPONSE
    int result = state_machine_handle_event(&sm, EVENT_START);
    TEST_CHECK(assert_equal_int(0, result, "Event handling should succeed"));
    TEST_CHECK(assert_equal_int(STATE_WAITING_RESPONSE, sm.current_state, "State should transition to WAITING_RESPONSE"));
    
    // Test transition from WAITING_RESPONSE to DISPENSING
    result = state_machine_handle_event(&sm, EVENT_RESPONSE_RECEIVED);
    TEST_CHECK(assert_equal_int(0, result, "Event handling should succeed"));
    TEST_CHECK(assert_equal_int(STATE_DISPENSING, sm.current_state, "State should transition to DISPENSING"));
    
    // Test transition from DISPENSING to IDLE
    result = state_machine_handle_event(&sm, EVENT_DISPENSE_COMPLETED);
    TEST_CHECK(assert_equal_int(0, result, "Event handling should succeed"));
    TEST_CHECK(assert_equal_int(STATE_IDLE, sm.current_state, "State should transition to IDLE"));
    
    state_machine_destroy(&sm);
    return TEST_PASS;
}

TEST_CASE(state_machine_error_handling) {
    state_machine_t sm;
    state_machine_init(&sm);
    
    // Test transition to ERROR state
    int result = state_machine_handle_event(&sm, EVENT_ERROR_OCCURRED);
    TEST_CHECK(assert_equal_int(0, result, "Event handling should succeed"));
    TEST_CHECK(assert_equal_int(STATE_ERROR, sm.current_state, "State should transition to ERROR"));
    
    // Test reset from ERROR state
    result = state_machine_handle_event(&sm, EVENT_RESET);
    TEST_CHECK(assert_equal_int(0, result, "Event handling should succeed"));
    TEST_CHECK(assert_equal_int(STATE_IDLE, sm.current_state, "State should transition to IDLE after reset");
    
    state_machine_destroy(&sm);
    return TEST_PASS;
}

TEST_CASE(state_machine_state_string_conversion) {
    TEST_CHECK(assert_equal_str("IDLE", state_machine_state_to_string(STATE_IDLE), "IDLE state string should be correct"));
    TEST_CHECK(assert_equal_str("WAITING_RESPONSE", state_machine_state_to_string(STATE_WAITING_RESPONSE), "WAITING_RESPONSE state string should be correct"));
    TEST_CHECK(assert_equal_str("DISPENSING", state_machine_state_to_string(STATE_DISPENSING), "DISPENSING state string should be correct"));
    TEST_CHECK(assert_equal_str("ERROR", state_machine_state_to_string(STATE_ERROR), "ERROR state string should be correct"));
    TEST_CHECK(assert_equal_str("UNKNOWN", state_machine_state_to_string((state_t)999), "Unknown state should return UNKNOWN"));
    
    return TEST_PASS;
}

TEST_CASE(state_machine_event_string_conversion) {
    TEST_CHECK(assert_equal_str("START", state_machine_event_to_string(EVENT_START), "START event string should be correct"));
    TEST_CHECK(assert_equal_str("RESPONSE_RECEIVED", state_machine_event_to_string(EVENT_RESPONSE_RECEIVED), "RESPONSE_RECEIVED event string should be correct"));
    TEST_CHECK(assert_equal_str("DISPENSE_CONFIRMED", state_machine_event_to_string(EVENT_DISPENSE_CONFIRMED), "DISPENSE_CONFIRMED event string should be correct"));
    TEST_CHECK(assert_equal_str("DISPENSE_COMPLETED", state_machine_event_to_string(EVENT_DISPENSE_COMPLETED), "DISPENSE_COMPLETED event string should be correct"));
    TEST_CHECK(assert_equal_str("ERROR_OCCURRED", state_machine_event_to_string(EVENT_ERROR_OCCURRED), "ERROR_OCCURRED event string should be correct"));
    TEST_CHECK(assert_equal_str("TIMEOUT", state_machine_event_to_string(EVENT_TIMEOUT), "TIMEOUT event string should be correct"));
    TEST_CHECK(assert_equal_str("RESET", state_machine_event_to_string(EVENT_RESET), "RESET event string should be correct"));
    TEST_CHECK(assert_equal_str("UNKNOWN", state_machine_event_to_string((event_t)999), "Unknown event should return UNKNOWN"));
    
    return TEST_PASS;
}

test_suite_t* create_state_machine_test_suite(void) {
    test_suite_t* suite = test_suite_create("State Machine Tests");
    if (suite == NULL) {
        return NULL;
    }
    
    ADD_TEST(suite, state_machine_init_destroy, true);
    ADD_TEST(suite, state_machine_get_current_state, true);
    ADD_TEST(suite, state_machine_invalid_pointer, true);
    ADD_TEST(suite, state_machine_state_transitions, true);
    ADD_TEST(suite, state_machine_error_handling, true);
    ADD_TEST(suite, state_machine_state_string_conversion, true);
    ADD_TEST(suite, state_machine_event_string_conversion, true);
    
    return suite;
}