@echo off
setlocal enabledelayedexpansion

echo ====================================================
echo Dogus Otomat Ice Cream Controller Build Script
echo ====================================================

REM Check if make is available
where make >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: GNU Make not found in PATH
    echo Please install MinGW-w64 or MSYS2 and add make to your PATH
    echo Download MinGW-w64 from: https://www.mingw-w64.org/
    echo Download MSYS2 from: https://www.msys2.org/
    exit /b 1
)

REM Parse command line arguments
set BUILD_TARGET=all
set WIRINGPI_SUPPORT=0

:parse_args
if "%1"=="" goto args_done
if "%1"=="release" (
    set BUILD_TARGET=release
    shift
    goto parse_args
)
if "%1"=="debug" (
    set BUILD_TARGET=debug
    shift
    goto parse_args
)
if "%1"=="clean" (
    set BUILD_TARGET=clean
    shift
    goto parse_args
)
if "%1"=="clean-all" (
    set BUILD_TARGET=clean-all
    shift
    goto parse_args
)
if "%1"=="test" (
    set BUILD_TARGET=test-all
    shift
    goto parse_args
)
if "%1"=="install" (
    set BUILD_TARGET=install
    shift
    goto parse_args
)
if "%1"=="wiringpi" (
    set WIRINGPI_SUPPORT=1
    shift
    goto parse_args
)
if "%1"=="help" (
    set BUILD_TARGET=help
    shift
    goto parse_args
)
shift
goto parse_args

:args_done

REM Build with appropriate settings
if %WIRINGPI_SUPPORT%==1 (
    echo Building with WiringPi support...
    if "!BUILD_TARGET!"=="clean" (
        make clean
    ) else if "!BUILD_TARGET!"=="clean-all" (
        make clean-all
    ) else if "!BUILD_TARGET!"=="install" (
        make install
    ) else if "!BUILD_TARGET!"=="test-all" (
        make WIRINGPI_AVAILABLE=1 test-all
    ) else if "!BUILD_TARGET!"=="help" (
        make help
    ) else (
        make WIRINGPI_AVAILABLE=1 !BUILD_TARGET!
    )
) else (
    echo Building without WiringPi support...
    if "!BUILD_TARGET!"=="clean" (
        make clean
    ) else if "!BUILD_TARGET!"=="clean-all" (
        make clean-all
    ) else if "!BUILD_TARGET!"=="install" (
        make install
    ) else if "!BUILD_TARGET!"=="test-all" (
        make test-all
    ) else if "!BUILD_TARGET!"=="help" (
        make help
    ) else (
        make !BUILD_TARGET!
    )
)

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Build failed with error code %errorlevel%
    exit /b %errorlevel%
)

echo.
echo Build completed successfully!