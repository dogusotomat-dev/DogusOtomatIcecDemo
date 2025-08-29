#include "state_machine.h"
#include "../utils/logger.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int state_machine_init(state_machine_t* sm) {
    if (sm == NULL) {
        LOG_ERROR("Invalid state machine pointer");
        return -1;
    }
    
    // Initialize the state machine
    sm->current_state = STATE_IDLE;
    sm->initialized = 1;
    
    // Initialize the mutex
    int result = pthread_mutex_init(&sm->state_mutex, NULL);
    if (result != 0) {
        LOG_ERROR("Failed to initialize mutex: %d", result);
        sm->initialized = 0;
        return -1;
    }
    
    LOG_INFO("State machine initialized, current state: %s", 
             state_machine_state_to_string(sm->current_state));
    
    return 0;
}

int state_machine_destroy(state_machine_t* sm) {
    if (sm == NULL) {
        LOG_ERROR("Invalid state machine pointer");
        return -1;
    }
    
    if (!sm->initialized) {
        LOG_WARN("State machine not initialized");
        return -1;
    }
    
    // Destroy the mutex
    int result = pthread_mutex_destroy(&sm->state_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to destroy mutex: %d", result);
        return -1;
    }
    
    sm->initialized = 0;
    LOG_INFO("State machine destroyed");
    
    return 0;
}

state_t state_machine_get_current_state(state_machine_t* sm) {
    if (sm == NULL) {
        LOG_ERROR("Invalid state machine pointer");
        return STATE_ERROR;
    }
    
    if (!sm->initialized) {
        LOG_ERROR("State machine not initialized");
        return STATE_ERROR;
    }
    
    state_t current_state;
    
    // Lock the mutex to read the state
    int result = pthread_mutex_lock(&sm->state_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to lock mutex: %d", result);
        return STATE_ERROR;
    }
    
    current_state = sm->current_state;
    
    // Unlock the mutex
    result = pthread_mutex_unlock(&sm->state_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to unlock mutex: %d", result);
        // We still return the state we read, but log the error
    }
    
    return current_state;
}

int state_machine_handle_event(state_machine_t* sm, event_t event) {
    if (sm == NULL) {
        LOG_ERROR("Invalid state machine pointer");
        return -1;
    }
    
    if (!sm->initialized) {
        LOG_ERROR("State machine not initialized");
        return -1;
    }
    
    // Lock the mutex to protect state changes
    int result = pthread_mutex_lock(&sm->state_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to lock mutex: %d", result);
        return -1;
    }
    
    state_t previous_state = sm->current_state;
    state_t new_state = sm->current_state;
    
    LOG_DEBUG("Handling event %s in state %s", 
              state_machine_event_to_string(event), 
              state_machine_state_to_string(sm->current_state));
    
    // State transition logic
    switch (sm->current_state) {
        case STATE_IDLE:
            if (event == EVENT_START) {
                new_state = STATE_WAITING_RESPONSE;
            } else if (event == EVENT_ERROR_OCCURRED) {
                new_state = STATE_ERROR;
            }
            break;
            
        case STATE_WAITING_RESPONSE:
            if (event == EVENT_RESPONSE_RECEIVED) {
                new_state = STATE_DISPENSING;
            } else if (event == EVENT_TIMEOUT) {
                new_state = STATE_ERROR;
            } else if (event == EVENT_ERROR_OCCURRED) {
                new_state = STATE_ERROR;
            } else if (event == EVENT_RESET) {
                new_state = STATE_IDLE;
            }
            break;
            
        case STATE_DISPENSING:
            if (event == EVENT_DISPENSE_COMPLETED) {
                new_state = STATE_IDLE;
            } else if (event == EVENT_ERROR_OCCURRED) {
                new_state = STATE_ERROR;
            }
            break;
            
        case STATE_ERROR:
            if (event == EVENT_RESET) {
                new_state = STATE_IDLE;
            }
            break;
            
        default:
            LOG_WARN("Unknown state: %d", sm->current_state);
            new_state = STATE_ERROR;
            break;
    }
    
    // Update state if it changed
    if (new_state != sm->current_state) {
        sm->current_state = new_state;
        LOG_INFO("State transition: %s -> %s (event: %s)",
                 state_machine_state_to_string(previous_state),
                 state_machine_state_to_string(sm->current_state),
                 state_machine_event_to_string(event));
    } else {
        LOG_DEBUG("No state change for event %s in state %s",
                  state_machine_event_to_string(event),
                  state_machine_state_to_string(sm->current_state));
    }
    
    // Unlock the mutex
    result = pthread_mutex_unlock(&sm->state_mutex);
    if (result != 0) {
        LOG_ERROR("Failed to unlock mutex: %d", result);
        return -1;
    }
    
    return 0;
}

const char* state_machine_state_to_string(state_t state) {
    switch (state) {
        case STATE_IDLE: return "IDLE";
        case STATE_WAITING_RESPONSE: return "WAITING_RESPONSE";
        case STATE_DISPENSING: return "DISPENSING";
        case STATE_ERROR: return "ERROR";
        default: return "UNKNOWN";
    }
}

const char* state_machine_event_to_string(event_t event) {
    switch (event) {
        case EVENT_START: return "START";
        case EVENT_RESPONSE_RECEIVED: return "RESPONSE_RECEIVED";
        case EVENT_DISPENSE_CONFIRMED: return "DISPENSE_CONFIRMED";
        case EVENT_DISPENSE_COMPLETED: return "DISPENSE_COMPLETED";
        case EVENT_ERROR_OCCURRED: return "ERROR_OCCURRED";
        case EVENT_TIMEOUT: return "TIMEOUT";
        case EVENT_RESET: return "RESET";
        default: return "UNKNOWN";
    }
}