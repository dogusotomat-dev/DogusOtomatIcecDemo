#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include "core/state_machine.h"
#include "utils/logger.h"

#define NUM_THREADS 5

state_machine_t g_sm;

// Thread function to simulate concurrent access to the state machine
void* thread_function(void* arg) {
    int thread_id = *(int*)arg;
    
    LOG_INFO("THREAD", "Thread %d started", thread_id);
    
    // Each thread will try to trigger different events
    switch (thread_id % 4) {
        case 0:
            state_machine_handle_event(&g_sm, EVENT_START);
            sleep(1);
            state_machine_handle_event(&g_sm, EVENT_RESPONSE_RECEIVED);
            break;
        case 1:
            state_machine_handle_event(&g_sm, EVENT_DISPENSE_CONFIRMED);
            sleep(1);
            state_machine_handle_event(&g_sm, EVENT_DISPENSE_COMPLETED);
            break;
        case 2:
            // This thread will try to read the state
            for (int i = 0; i < 5; i++) {
                state_t current_state = state_machine_get_current_state(&g_sm);
                LOG_INFO("THREAD", "Thread %d read state: %s", 
                         thread_id, state_machine_state_to_string(current_state));
                usleep(200000);  // 200ms
            }
            break;
        case 3:
            state_machine_handle_event(&g_sm, EVENT_ERROR_OCCURRED);
            sleep(1);
            state_machine_handle_event(&g_sm, EVENT_RESET);
            break;
    }
    
    LOG_INFO("THREAD", "Thread %d finished", thread_id);
    return NULL;
}

int main() {
    // Initialize logger
    logger_init();
    
    // Initialize state machine
    if (state_machine_init(&g_sm) != 0) {
        LOG_ERROR("MAIN", "Failed to initialize state machine");
        return 1;
    }
    
    LOG_INFO("MAIN", "Thread-safe state machine test started");
    
    pthread_t threads[NUM_THREADS];
    int thread_ids[NUM_THREADS];
    
    // Create threads
    for (int i = 0; i < NUM_THREADS; i++) {
        thread_ids[i] = i;
        int result = pthread_create(&threads[i], NULL, thread_function, &thread_ids[i]);
        if (result != 0) {
            LOG_ERROR("MAIN", "Failed to create thread %d: %d", i, result);
            return 1;
        }
    }
    
    // Wait for all threads to complete
    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }
    
    // Print final state
    state_t final_state = state_machine_get_current_state(&g_sm);
    LOG_INFO("MAIN", "Final state: %s", state_machine_state_to_string(final_state));
    
    // Cleanup
    state_machine_destroy(&g_sm);
    
    LOG_INFO("MAIN", "Thread-safe state machine test completed");
    return 0;
}