#!/bin/bash

# Dogus Otomat Ice Cream Controller Build Script
# ====================================================

echo "===================================================="
echo "Dogus Otomat Ice Cream Controller Build Script"
echo "===================================================="

# Parse command line arguments
BUILD_TARGET="all"
WIRINGPI_SUPPORT=0

while [[ $# -gt 0 ]]; do
    case $1 in
        release)
            BUILD_TARGET="release"
            shift
            ;;
        debug)
            BUILD_TARGET="debug"
            shift
            ;;
        clean)
            BUILD_TARGET="clean"
            shift
            ;;
        clean-all)
            BUILD_TARGET="clean-all"
            shift
            ;;
        test)
            BUILD_TARGET="test-all"
            shift
            ;;
        install)
            BUILD_TARGET="install"
            shift
            ;;
        wiringpi)
            WIRINGPI_SUPPORT=1
            shift
            ;;
        help)
            BUILD_TARGET="help"
            shift
            ;;
        *)
            echo "Unknown option: $1"
            shift
            ;;
    esac
done

# Build with appropriate settings
if [ $WIRINGPI_SUPPORT -eq 1 ]; then
    echo "Building with WiringPi support..."
    if [ "$BUILD_TARGET" = "clean" ]; then
        make clean
    elif [ "$BUILD_TARGET" = "clean-all" ]; then
        make clean-all
    elif [ "$BUILD_TARGET" = "install" ]; then
        make install
    elif [ "$BUILD_TARGET" = "test-all" ]; then
        make WIRINGPI_AVAILABLE=1 test-all
    elif [ "$BUILD_TARGET" = "help" ]; then
        make help
    else
        make WIRINGPI_AVAILABLE=1 $BUILD_TARGET
    fi
else
    echo "Building without WiringPi support..."
    if [ "$BUILD_TARGET" = "clean" ]; then
        make clean
    elif [ "$BUILD_TARGET" = "clean-all" ]; then
        make clean-all
    elif [ "$BUILD_TARGET" = "install" ]; then
        make install
    elif [ "$BUILD_TARGET" = "test-all" ]; then
        make test-all
    elif [ "$BUILD_TARGET" = "help" ]; then
        make help
    else
        make $BUILD_TARGET
    fi
fi

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Build failed"
    exit 1
fi

echo ""
echo "Build completed successfully!"