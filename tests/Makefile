# Test Makefile for Dogus Otomat Ice Cream Controller
# ==============================================================================

# Directory structure
SRC_DIR = ../src
TESTS_DIR = .
BUILD_DIR = build
BIN_DIR = bin

# Compiler and tools
CC = gcc
CFLAGS = -std=c99 -Wall -Wextra -Wpedantic -Werror -g -I$(SRC_DIR) -I$(TESTS_DIR)

# Source files
FRAMEWORK_SOURCES = $(TESTS_DIR)/test_framework.c
TEST_SOURCES = \
	$(TESTS_DIR)/test_runner.c \
	$(TESTS_DIR)/test_uart.c \
	$(TESTS_DIR)/test_iceboard.c \
	$(TESTS_DIR)/test_state_machine.c \
	$(TESTS_DIR)/test_gpio.c \
	$(TESTS_DIR)/test_logger.c \
	$(TESTS_DIR)/test_config_manager.c

# Object files
FRAMEWORK_OBJECTS = $(FRAMEWORK_SOURCES:$(TESTS_DIR)/%.c=$(BUILD_DIR)/%.o)
TEST_OBJECTS = $(TESTS_DIR)/test_runner.c $(TEST_SOURCES:$(TESTS_DIR)/test_%.c=$(BUILD_DIR)/test_%.o)

# Target executable
TARGET = $(BIN_DIR)/icec_tests

# Phony targets
.PHONY: all clean run help

# Default target
all: directories $(TARGET)

# Create necessary directories
directories:
	@mkdir -p $(BUILD_DIR) $(BIN_DIR)

# Build target
$(TARGET): $(FRAMEWORK_OBJECTS) $(TEST_OBJECTS)
	@echo "Linking test executable..."
	$(CC) $(FRAMEWORK_OBJECTS) $(TEST_OBJECTS) -o $@ -lpthread
	@echo "Test executable built: $@"

# Compile framework files
$(BUILD_DIR)/test_framework.o: $(TESTS_DIR)/test_framework.c
	@echo "Compiling $<..."
	$(CC) $(CFLAGS) -c $< -o $@

# Compile test files
$(BUILD_DIR)/test_%.o: $(TESTS_DIR)/test_%.c
	@echo "Compiling $<..."
	$(CC) $(CFLAGS) -c $< -o $@

# Compile test runner
$(BUILD_DIR)/test_runner.o: $(TESTS_DIR)/test_runner.c
	@echo "Compiling $<..."
	$(CC) $(CFLAGS) -c $< -o $@

# Run tests
run: all
	@echo "Running tests..."
	./$(TARGET)

# Run specific test suite
run-uart: all
	@echo "Running UART tests..."
	./$(TARGET) uart

run-iceboard: all
	@echo "Running Iceboard tests..."
	./$(TARGET) iceboard

run-state-machine: all
	@echo "Running State Machine tests..."
	./$(TARGET) state_machine

run-gpio: all
	@echo "Running GPIO tests..."
	./$(TARGET) gpio

run-logger: all
	@echo "Running Logger tests..."
	./$(TARGET) logger

run-config: all
	@echo "Running Configuration Manager tests..."
	./$(TARGET) config

# Clean target
clean:
	@echo "Cleaning test files..."
	@rm -rf $(BUILD_DIR)/*
	@rm -rf $(BIN_DIR)/*
	@echo "Test files cleaned successfully"

# Help target
help:
	@echo "Test Build System Help"
	@echo ""
	@echo "Targets:"
	@echo "  all             - Build all tests (default)"
	@echo "  clean           - Clean build files"
	@echo "  run             - Run all tests"
	@echo "  run-uart        - Run UART tests"
	@echo "  run-iceboard    - Run Iceboard tests"
	@echo "  run-state-machine - Run State Machine tests"
	@echo "  run-gpio        - Run GPIO tests"
	@echo "  run-logger      - Run Logger tests"
	@echo "  run-config      - Run Configuration Manager tests"
	@echo "  help            - Show this help message"