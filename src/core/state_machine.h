#ifndef STATE_MACHINE_H
#define STATE_MACHINE_H

#include <stdint.h>
#include <pthread.h>

// State machine states
typedef enum {
    STATE_IDLE = 0,
    STATE_WAITING_RESPONSE,
    STATE_DISPENSING,
    STATE_ERROR
} state_t;

// Event types
typedef enum {
    EVENT_START = 0,
    EVENT_RESPONSE_RECEIVED,
    EVENT_DISPENSE_CONFIRMED,
    EVENT_DISPENSE_COMPLETED,
    EVENT_ERROR_OCCURRED,
    EVENT_TIMEOUT,
    EVENT_RESET
} event_t;

// State machine structure
typedef struct {
    state_t current_state;
    pthread_mutex_t state_mutex;
    int initialized;
} state_machine_t;

// Function prototypes
int state_machine_init(state_machine_t* sm);
int state_machine_destroy(state_machine_t* sm);
state_t state_machine_get_current_state(state_machine_t* sm);
int state_machine_handle_event(state_machine_t* sm, event_t event);
const char* state_machine_state_to_string(state_t state);
const char* state_machine_event_to_string(event_t event);

#endif // STATE_MACHINE_H