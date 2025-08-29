#include "logger.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <time.h>
#include <sys/stat.h>
#include <sys/types.h>

#ifdef _WIN32
#include <windows.h>
#include <io.h>
#define access _access
#define F_OK 0
#else
#include <unistd.h>
#endif

// Global logger state
static struct {
    log_level_t level;
    log_output_t output;
    char file_path[256];
    FILE* file;
    size_t max_file_size;
    int max_files;
    int initialized;
    // For thread safety in a multi-threaded environment, we would add a mutex here
} g_logger = {
    .level = LOG_LEVEL_DEBUG,
    .output = LOG_OUTPUT_CONSOLE,
    .file_path = {0},
    .file = NULL,
    .max_file_size = 1024 * 1024, // 1MB default
    .max_files = 5,
    .initialized = 0
};

int logger_init(const logger_config_t* config) {
    // Check if already initialized
    if (g_logger.initialized) {
        fprintf(stderr, "Logger already initialized\n");
        return -1;
    }
    
    // Use default config if none provided
    if (config) {
        g_logger.level = config->level;
        g_logger.output = config->output;
        
        if (config->file_path[0] != '\0') {
            strncpy(g_logger.file_path, config->file_path, sizeof(g_logger.file_path) - 1);
            g_logger.file_path[sizeof(g_logger.file_path) - 1] = '\0';
        }
        
        if (config->max_file_size > 0) {
            g_logger.max_file_size = config->max_file_size;
        }
        
        if (config->max_files > 0) {
            g_logger.max_files = config->max_files;
        }
    }
    
    // Open log file if needed
    if ((g_logger.output == LOG_OUTPUT_FILE || g_logger.output == LOG_OUTPUT_BOTH) &&
        g_logger.file_path[0] != '\0') {
        
        g_logger.file = fopen(g_logger.file_path, "a");
        if (g_logger.file == NULL) {
            fprintf(stderr, "Failed to open log file: %s\n", g_logger.file_path);
            return -1;
        }
    }
    
    g_logger.initialized = 1;
    
    // Log initialization
    logger_log(LOG_LEVEL_INFO, __FILE__, __LINE__, "Logger initialized with level %s", 
               logger_level_to_string(g_logger.level));
    
    return 0;
}

int logger_destroy(void) {
    if (!g_logger.initialized) {
        return -1;
    }
    
    // Log destruction
    logger_log(LOG_LEVEL_INFO, __FILE__, __LINE__, "Logger destroyed");
    
    // Close file if open
    if (g_logger.file) {
        fclose(g_logger.file);
        g_logger.file = NULL;
    }
    
    g_logger.initialized = 0;
    return 0;
}

static void rotate_log_files(void) {
    if (g_logger.file_path[0] == '\0' || g_logger.max_files <= 1) {
        return;
    }
    
    // Close current file
    if (g_logger.file) {
        fclose(g_logger.file);
        g_logger.file = NULL;
    }
    
    // Rotate files: file.3 -> file.4, file.2 -> file.3, etc.
    for (int i = g_logger.max_files - 1; i > 0; i--) {
        char old_name[300];
        char new_name[300];
        
        if (i == 1) {
            strncpy(old_name, g_logger.file_path, sizeof(old_name) - 1);
            old_name[sizeof(old_name) - 1] = '\0';
        } else {
            snprintf(old_name, sizeof(old_name), "%s.%d", g_logger.file_path, i - 1);
        }
        
        snprintf(new_name, sizeof(new_name), "%s.%d", g_logger.file_path, i);
        
        // Remove new file if it exists
        remove(new_name);
        
        // Rename old file to new file
        if (access(old_name, F_OK) == 0) {
            rename(old_name, new_name);
        }
    }
    
    // Reopen the main log file
    g_logger.file = fopen(g_logger.file_path, "w");
    if (g_logger.file == NULL) {
        fprintf(stderr, "Failed to reopen log file: %s\n", g_logger.file_path);
    }
}

static int check_file_size_and_rotate(void) {
    if (!g_logger.file || g_logger.file_path[0] == '\0') {
        return 0;
    }
    
    // Get current file size
    long current_pos = ftell(g_logger.file);
    if (current_pos < 0) {
        return -1;
    }
    
    // Check if we need to rotate
    if ((size_t)current_pos >= g_logger.max_file_size) {
        rotate_log_files();
    }
    
    return 0;
}

void logger_log(log_level_t level, const char* file, int line, const char* format, ...) {
    if (!g_logger.initialized || level < g_logger.level) {
        return;
    }
    
    // Get current time
    time_t now = time(NULL);
    struct tm* timeinfo = localtime(&now);
    
    // Extract filename from path
    const char* filename = file;
    const char* last_slash = strrchr(file, '/');
    if (last_slash) {
        filename = last_slash + 1;
    }
    
    #ifdef _WIN32
    const char* last_backslash = strrchr(filename, '\\');
    if (last_backslash) {
        filename = last_backslash + 1;
    }
    #endif
    
    // Format the log level string
    const char* level_str = logger_level_to_string(level);
    
    // Format the message
    char message[1024];
    va_list args;
    va_start(args, format);
    vsnprintf(message, sizeof(message), format, args);
    va_end(args);
    
    // Output to console
    if (g_logger.output == LOG_OUTPUT_CONSOLE || g_logger.output == LOG_OUTPUT_BOTH) {
        printf("[%04d-%02d-%02d %02d:%02d:%02d] [%s] [%s:%d] %s\n",
               timeinfo->tm_year + 1900, timeinfo->tm_mon + 1, timeinfo->tm_mday,
               timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec,
               level_str, filename, line, message);
        fflush(stdout);
    }
    
    // Output to file
    if ((g_logger.output == LOG_OUTPUT_FILE || g_logger.output == LOG_OUTPUT_BOTH) && 
        g_logger.file) {
        
        // Check if we need to rotate files
        check_file_size_and_rotate();
        
        if (g_logger.file) {
            fprintf(g_logger.file, "[%04d-%02d-%02d %02d:%02d:%02d] [%s] [%s:%d] %s\n",
                    timeinfo->tm_year + 1900, timeinfo->tm_mon + 1, timeinfo->tm_mday,
                    timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec,
                    level_str, filename, line, message);
            fflush(g_logger.file);
        }
    }
}

void logger_log_raw(log_level_t level, const char* message) {
    if (!g_logger.initialized || level < g_logger.level) {
        return;
    }
    
    // Get current time
    time_t now = time(NULL);
    struct tm* timeinfo = localtime(&now);
    
    // Format the log level string
    const char* level_str = logger_level_to_string(level);
    
    // Output to console
    if (g_logger.output == LOG_OUTPUT_CONSOLE || g_logger.output == LOG_OUTPUT_BOTH) {
        printf("[%04d-%02d-%02d %02d:%02d:%02d] [%s] %s\n",
               timeinfo->tm_year + 1900, timeinfo->tm_mon + 1, timeinfo->tm_mday,
               timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec,
               level_str, message);
        fflush(stdout);
    }
    
    // Output to file
    if ((g_logger.output == LOG_OUTPUT_FILE || g_logger.output == LOG_OUTPUT_BOTH) && 
        g_logger.file) {
        
        // Check if we need to rotate files
        check_file_size_and_rotate();
        
        if (g_logger.file) {
            fprintf(g_logger.file, "[%04d-%02d-%02d %02d:%02d:%02d] [%s] %s\n",
                    timeinfo->tm_year + 1900, timeinfo->tm_mon + 1, timeinfo->tm_mday,
                    timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec,
                    level_str, message);
            fflush(g_logger.file);
        }
    }
}

void logger_flush(void) {
    if (!g_logger.initialized) {
        return;
    }
    
    if (g_logger.output == LOG_OUTPUT_CONSOLE || g_logger.output == LOG_OUTPUT_BOTH) {
        fflush(stdout);
    }
    
    if ((g_logger.output == LOG_OUTPUT_FILE || g_logger.output == LOG_OUTPUT_BOTH) && 
        g_logger.file) {
        fflush(g_logger.file);
    }
}

const char* logger_level_to_string(log_level_t level) {
    switch (level) {
        case LOG_LEVEL_DEBUG: return "DEBUG";
        case LOG_LEVEL_INFO:  return "INFO";
        case LOG_LEVEL_WARN:  return "WARN";
        case LOG_LEVEL_ERROR: return "ERROR";
        case LOG_LEVEL_OFF:   return "OFF";
        default:              return "UNKNOWN";
    }
}