#ifndef LOGGER_H
#define LOGGER_H

#include <stdio.h>
#include <stdint.h>
#include <stdarg.h>
#include <time.h>

#ifdef __cplusplus
extern "C" {
#endif

// Log levels
typedef enum {
    LOG_LEVEL_DEBUG = 0,
    LOG_LEVEL_INFO,
    LOG_LEVEL_WARN,
    LOG_LEVEL_ERROR,
    LOG_LEVEL_OFF
} log_level_t;

// Log output destinations
typedef enum {
    LOG_OUTPUT_CONSOLE = 1,
    LOG_OUTPUT_FILE = 2,
    LOG_OUTPUT_BOTH = 3
} log_output_t;

// Logger configuration structure
typedef struct {
    log_level_t level;
    log_output_t output;
    char file_path[256];
    size_t max_file_size;
    int max_files;
} logger_config_t;

// Function prototypes
int logger_init(const logger_config_t* config);
int logger_destroy(void);
void logger_log(log_level_t level, const char* file, int line, const char* format, ...);
void logger_log_raw(log_level_t level, const char* message);
void logger_flush(void);
const char* logger_level_to_string(log_level_t level);

// Convenience macros that automatically include file and line information
#define LOG_DEBUG(format, ...) logger_log(LOG_LEVEL_DEBUG, __FILE__, __LINE__, format, ##__VA_ARGS__)
#define LOG_INFO(format, ...)  logger_log(LOG_LEVEL_INFO, __FILE__, __LINE__, format, ##__VA_ARGS__)
#define LOG_WARN(format, ...)  logger_log(LOG_LEVEL_WARN, __FILE__, __LINE__, format, ##__VA_ARGS__)
#define LOG_ERROR(format, ...) logger_log(LOG_LEVEL_ERROR, __FILE__, __LINE__, format, ##__VA_ARGS__)

// Conditional logging based on compile-time log level
#ifndef LOG_COMPILE_LEVEL
#define LOG_COMPILE_LEVEL LOG_LEVEL_DEBUG
#endif

#if LOG_COMPILE_LEVEL <= LOG_LEVEL_DEBUG
#define LOG_DEBUG_COND(format, ...) LOG_DEBUG(format, ##__VA_ARGS__)
#else
#define LOG_DEBUG_COND(format, ...)
#endif

#if LOG_COMPILE_LEVEL <= LOG_LEVEL_INFO
#define LOG_INFO_COND(format, ...) LOG_INFO(format, ##__VA_ARGS__)
#else
#define LOG_INFO_COND(format, ...)
#endif

#if LOG_COMPILE_LEVEL <= LOG_LEVEL_WARN
#define LOG_WARN_COND(format, ...) LOG_WARN(format, ##__VA_ARGS__)
#else
#define LOG_WARN_COND(format, ...)
#endif

#if LOG_COMPILE_LEVEL <= LOG_LEVEL_ERROR
#define LOG_ERROR_COND(format, ...) LOG_ERROR(format, ##__VA_ARGS__)
#else
#define LOG_ERROR_COND(format, ...)
#endif

#ifdef __cplusplus
}
#endif

#endif // LOGGER_H