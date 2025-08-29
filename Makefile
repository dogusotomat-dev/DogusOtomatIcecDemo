# Professional Makefile for Dogus Otomat Ice Cream Controller
# ==============================================================================
# This Makefile provides a complete build system with:
# - Release and Debug targets
# - Modular compilation
# - Clean and Install targets
# - WiringPi library linking
# - Comprehensive dependency management

# Project information
PROJECT_NAME = icec_controller
PROJECT_VERSION = 1.0.0

# Directory structure
SRC_DIR = src
BUILD_DIR = build
BIN_DIR = bin
LIB_DIR = lib
INCLUDE_DIR = include

# Source directories
CORE_DIR = $(SRC_DIR)/core
DRIVERS_DIR = $(SRC_DIR)/drivers
UTILS_DIR = $(SRC_DIR)/utils

# Output directories
BUILD_CORE_DIR = $(BUILD_DIR)/core
BUILD_DRIVERS_DIR = $(BUILD_DIR)/drivers
BUILD_UTILS_DIR = $(BUILD_DIR)/utils

# Compiler and tools
CC = gcc
SIZE = size
STRIP = strip

# Compiler flags
CFLAGS_COMMON = -std=c99 -Wall -Wextra -Wpedantic -Werror
CFLAGS_WARNINGS = -Wmissing-prototypes -Wstrict-prototypes -Wconversion
CFLAGS_INCLUDE = -I$(SRC_DIR) -I$(INCLUDE_DIR)

# Debug flags
CFLAGS_DEBUG = -g -DDEBUG -O0 -DLOG_LEVEL=0

# Release flags
CFLAGS_RELEASE = -O2 -DNDEBUG -DLOG_LEVEL=2

# Linker flags
LDFLAGS_COMMON = -lpthread -lm

# WiringPi library (conditional linking)
ifeq ($(WIRINGPI_AVAILABLE),1)
    LDFLAGS_COMMON += -lwiringPi
    CFLAGS_INCLUDE += -DWIRING_PI_AVAILABLE
endif

# Define default target
.DEFAULT_GOAL := all

# Source files
CORE_SOURCES = $(wildcard $(CORE_DIR)/*.c)
DRIVERS_SOURCES = $(wildcard $(DRIVERS_DIR)/*.c)
UTILS_SOURCES = $(wildcard $(UTILS_DIR)/*.c)

# Exclude test files from main build
CORE_SOURCES := $(filter-out %_test.c,$(CORE_SOURCES))
DRIVERS_SOURCES := $(filter-out %_test.c,$(DRIVERS_SOURCES))
UTILS_SOURCES := $(filter-out %_test.c,$(UTILS_SOURCES))

# All source files
SOURCES = $(CORE_SOURCES) $(DRIVERS_SOURCES) $(UTILS_SOURCES)

# Object files
CORE_OBJECTS = $(CORE_SOURCES:$(SRC_DIR)/%.c=$(BUILD_DIR)/%.o)
DRIVERS_OBJECTS = $(DRIVERS_SOURCES:$(SRC_DIR)/%.c=$(BUILD_DIR)/%.o)
UTILS_OBJECTS = $(UTILS_SOURCES:$(SRC_DIR)/%.c=$(BUILD_DIR)/%.o)
OBJECTS = $(CORE_OBJECTS) $(DRIVERS_OBJECTS) $(UTILS_OBJECTS)

# Target executables
TARGET_RELEASE = $(BIN_DIR)/$(PROJECT_NAME)
TARGET_DEBUG = $(BIN_DIR)/$(PROJECT_NAME)_debug

# Test executables
TEST_TARGETS = \
    $(BIN_DIR)/state_machine_test \
    $(BIN_DIR)/gpio_test \
    $(BIN_DIR)/iceboard_parse_test \
    $(BIN_DIR)/config_manager_test

# Test objects
STATE_MACHINE_TEST_OBJECTS = $(BUILD_DIR)/core/state_machine_test.o $(DRIVERS_OBJECTS) $(UTILS_OBJECTS)
GPIO_TEST_OBJECTS = $(BUILD_DIR)/drivers/gpio_test.o $(BUILD_DIR)/drivers/gpio.o $(BUILD_DIR)/utils/logger.o $(BUILD_DIR)/utils/config_manager.o
ICEBOARD_PARSE_TEST_OBJECTS = $(BUILD_DIR)/drivers/iceboard_parse_test.o $(BUILD_DIR)/drivers/iceboard_parse.o $(BUILD_DIR)/utils/logger.o
CONFIG_MANAGER_TEST_OBJECTS = $(BUILD_DIR)/utils/config_manager_test.o $(BUILD_DIR)/utils/config_manager.o $(BUILD_DIR)/utils/logger.o

# Color codes for output
COLOR_RESET = \033[0m
COLOR_DEFAULT = \033[39m
COLOR_GREEN = \033[32m
COLOR_YELLOW = \033[33m
COLOR_RED = \033[31m
COLOR_BLUE = \033[34m

# Phony targets
.PHONY: all release debug clean clean-all install help test test-all size strip

# Main targets
all: release

release: CFLAGS = $(CFLAGS_COMMON) $(CFLAGS_WARNINGS) $(CFLAGS_RELEASE) $(CFLAGS_INCLUDE)
release: LDFLAGS = $(LDFLAGS_COMMON)
release: directories $(TARGET_RELEASE)

debug: CFLAGS = $(CFLAGS_COMMON) $(CFLAGS_WARNINGS) $(CFLAGS_DEBUG) $(CFLAGS_INCLUDE)
debug: LDFLAGS = $(LDFLAGS_COMMON)
debug: directories $(TARGET_DEBUG)

# Create necessary directories
directories:
	@echo "$(COLOR_BLUE)Creating directories...$(COLOR_RESET)"
	@mkdir -p $(BUILD_CORE_DIR) $(BUILD_DRIVERS_DIR) $(BUILD_UTILS_DIR) $(BIN_DIR) $(LIB_DIR) $(INCLUDE_DIR)
	@echo "$(COLOR_GREEN)Directories created successfully$(COLOR_RESET)"

# Build release target
$(TARGET_RELEASE): $(OBJECTS)
	@echo "$(COLOR_BLUE)Linking release executable...$(COLOR_RESET)"
	$(CC) $(OBJECTS) -o $@ $(LDFLAGS)
	@echo "$(COLOR_GREEN)Release build completed: $@$(COLOR_RESET)"

# Build debug target
$(TARGET_DEBUG): $(OBJECTS)
	@echo "$(COLOR_BLUE)Linking debug executable...$(COLOR_RESET)"
	$(CC) $(OBJECTS) -o $@ $(LDFLAGS)
	@echo "$(COLOR_GREEN)Debug build completed: $@$(COLOR_RESET)"

# Compile source files to object files
$(BUILD_DIR)/%.o: $(SRC_DIR)/%.c
	@echo "$(COLOR_YELLOW)Compiling $<...$(COLOR_RESET)"
	$(CC) $(CFLAGS) -c $< -o $@
	@echo "$(COLOR_GREEN)Compiled $< successfully$(COLOR_RESET)"

# Test targets
test: directories $(TEST_TARGETS)
	@echo "$(COLOR_GREEN)All tests built successfully$(COLOR_RESET)"

# State machine test
$(BIN_DIR)/state_machine_test: $(STATE_MACHINE_TEST_OBJECTS)
	@echo "$(COLOR_BLUE)Building state machine test...$(COLOR_RESET)"
	$(CC) $(STATE_MACHINE_TEST_OBJECTS) -o $@ $(LDFLAGS)
	@echo "$(COLOR_GREEN)State machine test built: $@$(COLOR_RESET)"

# GPIO test
$(BIN_DIR)/gpio_test: $(GPIO_TEST_OBJECTS)
	@echo "$(COLOR_BLUE)Building GPIO test...$(COLOR_RESET)"
	$(CC) $(GPIO_TEST_OBJECTS) -o $@ $(LDFLAGS)
	@echo "$(COLOR_GREEN)GPIO test built: $@$(COLOR_RESET)"

# Iceboard parse test
$(BIN_DIR)/iceboard_parse_test: $(ICEBOARD_PARSE_TEST_OBJECTS)
	@echo "$(COLOR_BLUE)Building iceboard parse test...$(COLOR_RESET)"
	$(CC) $(ICEBOARD_PARSE_TEST_OBJECTS) -o $@ $(LDFLAGS)
	@echo "$(COLOR_GREEN)Iceboard parse test built: $@$(COLOR_RESET)"

# Configuration manager test
$(BIN_DIR)/config_manager_test: $(CONFIG_MANAGER_TEST_OBJECTS)
	@echo "$(COLOR_BLUE)Building configuration manager test...$(COLOR_RESET)"
	$(CC) $(CONFIG_MANAGER_TEST_OBJECTS) -o $@ $(LDFLAGS)
	@echo "$(COLOR_GREEN)Configuration manager test built: $@$(COLOR_RESET)"

# Run tests
run-state-machine-test: $(BIN_DIR)/state_machine_test
	@echo "$(COLOR_BLUE)Running state machine test...$(COLOR_RESET)"
	./$(BIN_DIR)/state_machine_test

run-gpio-test: $(BIN_DIR)/gpio_test
	@echo "$(COLOR_BLUE)Running GPIO test...$(COLOR_RESET)"
	./$(BIN_DIR)/gpio_test

run-iceboard-parse-test: $(BIN_DIR)/iceboard_parse_test
	@echo "$(COLOR_BLUE)Running iceboard parse test...$(COLOR_RESET)"
	./$(BIN_DIR)/iceboard_parse_test

run-config-manager-test: $(BIN_DIR)/config_manager_test
	@echo "$(COLOR_BLUE)Running configuration manager test...$(COLOR_RESET)"
	./$(BIN_DIR)/config_manager_test

# Run all tests with new framework
test-all-framework:
	@echo "$(COLOR_BLUE)Building and running all tests with framework...$(COLOR_RESET)"
	$(MAKE) -C tests run

# Run specific test suites with framework
test-uart-framework:
	@echo "$(COLOR_BLUE)Running UART tests with framework...$(COLOR_RESET)"
	$(MAKE) -C tests run-uart

test-iceboard-framework:
	@echo "$(COLOR_BLUE)Running Iceboard tests with framework...$(COLOR_RESET)"
	$(MAKE) -C tests run-iceboard

test-state-machine-framework:
	@echo "$(COLOR_BLUE)Running State Machine tests with framework...$(COLOR_RESET)"
	$(MAKE) -C tests run-state-machine

test-gpio-framework:
	@echo "$(COLOR_BLUE)Running GPIO tests with framework...$(COLOR_RESET)"
	$(MAKE) -C tests run-gpio

test-logger-framework:
	@echo "$(COLOR_BLUE)Running Logger tests with framework...$(COLOR_RESET)"
	$(MAKE) -C tests run-logger

test-config-framework:
	@echo "$(COLOR_BLUE)Running Configuration Manager tests with framework...$(COLOR_RESET)"
	$(MAKE) -C tests run-config

# Clean target
clean:
	@echo "$(COLOR_BLUE)Cleaning build files...$(COLOR_RESET)"
	@rm -rf $(BUILD_DIR)/*
	@echo "$(COLOR_GREEN)Build files cleaned successfully$(COLOR_RESET)"

# Clean all (including binaries)
clean-all: clean
	@echo "$(COLOR_BLUE)Cleaning all files...$(COLOR_RESET)"
	@rm -rf $(BIN_DIR)/*
	@rm -rf $(LIB_DIR)/*
	@echo "$(COLOR_GREEN)All files cleaned successfully$(COLOR_RESET)"

# Install target
install: release
	@echo "$(COLOR_BLUE)Installing $(PROJECT_NAME) to system...$(COLOR_RESET)"
	@mkdir -p /usr/local/bin
	@cp $(TARGET_RELEASE) /usr/local/bin/$(PROJECT_NAME)
	@chmod +x /usr/local/bin/$(PROJECT_NAME)
	@echo "$(COLOR_GREEN)$(PROJECT_NAME) installed successfully to /usr/local/bin$(COLOR_RESET)"

# Uninstall target
uninstall:
	@echo "$(COLOR_BLUE)Uninstalling $(PROJECT_NAME) from system...$(COLOR_RESET)"
	@rm -f /usr/local/bin/$(PROJECT_NAME)
	@echo "$(COLOR_GREEN)$(PROJECT_NAME) uninstalled successfully$(COLOR_RESET)"

# Size information
size: release
	@echo "$(COLOR_BLUE)Binary size information:$(COLOR_RESET)"
	$(SIZE) $(TARGET_RELEASE)

# Strip binary
strip: release
	@echo "$(COLOR_BLUE)Stripping binary...$(COLOR_RESET)"
	$(STRIP) $(TARGET_RELEASE)
	@echo "$(COLOR_GREEN)Binary stripped successfully$(COLOR_RESET)"

# Help target
help:
	@echo "$(COLOR_BLUE)$(PROJECT_NAME) Build System Help$(COLOR_RESET)"
	@echo ""
	@echo "$(COLOR_YELLOW)Targets:$(COLOR_RESET)"
	@echo "  $(COLOR_GREEN)all$(COLOR_RESET)             - Build release version (default)"
	@echo "  $(COLOR_GREEN)release$(COLOR_RESET)         - Build release version"
	@echo "  $(COLOR_GREEN)debug$(COLOR_RESET)           - Build debug version"
	@echo "  $(COLOR_GREEN)clean$(COLOR_RESET)           - Clean build files"
	@echo "  $(COLOR_GREEN)clean-all$(COLOR_RESET)       - Clean all files (including binaries)"
	@echo "  $(COLOR_GREEN)install$(COLOR_RESET)         - Install to system (/usr/local/bin)"
	@echo "  $(COLOR_GREEN)uninstall$(COLOR_RESET)       - Remove from system"
	@echo "  $(COLOR_GREEN)test$(COLOR_RESET)            - Build all test programs"
	@echo "  $(COLOR_GREEN)test-all$(COLOR_RESET)        - Build all test programs"
	@echo "  $(COLOR_GREEN)run-state-machine-test$(COLOR_RESET) - Run state machine test"
	@echo "  $(COLOR_GREEN)run-gpio-test$(COLOR_RESET)   - Run GPIO test"
	@echo "  $(COLOR_GREEN)run-iceboard-parse-test$(COLOR_RESET) - Run iceboard parse test"
	@echo "  $(COLOR_GREEN)run-config-manager-test$(COLOR_RESET) - Run configuration manager test"
	@echo "  $(COLOR_GREEN)test-all-framework$(COLOR_RESET) - Run all tests with new framework"
	@echo "  $(COLOR_GREEN)test-uart-framework$(COLOR_RESET) - Run UART tests with framework"
	@echo "  $(COLOR_GREEN)test-iceboard-framework$(COLOR_RESET) - Run Iceboard tests with framework"
	@echo "  $(COLOR_GREEN)test-state-machine-framework$(COLOR_RESET) - Run State Machine tests with framework"
	@echo "  $(COLOR_GREEN)test-gpio-framework$(COLOR_RESET) - Run GPIO tests with framework"
	@echo "  $(COLOR_GREEN)test-logger-framework$(COLOR_RESET) - Run Logger tests with framework"
	@echo "  $(COLOR_GREEN)test-config-framework$(COLOR_RESET) - Run Configuration tests with framework"
	@echo "  $(COLOR_GREEN)size$(COLOR_RESET)            - Show binary size information"
	@echo "  $(COLOR_GREEN)strip$(COLOR_RESET)           - Strip binary to reduce size"
	@echo "  $(COLOR_GREEN)help$(COLOR_RESET)            - Show this help message"
	@echo ""
	@echo "$(COLOR_YELLOW)Variables:$(COLOR_RESET)"
	@echo "  $(COLOR_GREEN)WIRINGPI_AVAILABLE=1$(COLOR_RESET) - Enable WiringPi library support"
	@echo ""
	@echo "$(COLOR_YELLOW)Examples:$(COLOR_RESET)"
	@echo "  make                    # Build release version"
	@echo "  make debug              # Build debug version"
	@echo "  make WIRINGPI_AVAILABLE=1  # Build with WiringPi support"
	@echo "  make clean install      # Clean and install"
	@echo "  make test               # Build all tests"
	@echo "  make run-gpio-test      # Run GPIO test"

# Display variables for debugging
print-variables:
	@echo "$(COLOR_BLUE)Makefile Variables:$(COLOR_RESET)"
	@echo "CC = $(CC)"
	@echo "CFLAGS = $(CFLAGS)"
	@echo "LDFLAGS = $(LDFLAGS)"
	@echo "SOURCES = $(SOURCES)"
	@echo "OBJECTS = $(OBJECTS)"
	@echo "TARGET_RELEASE = $(TARGET_RELEASE)"
	@echo "TARGET_DEBUG = $(TARGET_DEBUG)"