#!/bin/bash

# =============================================================================
# LOCAL ENVIRONMENT SETUP SCRIPT
# =============================================================================
# This script sets up local environment variables for local testing
# It uses the new configuration management system

echo "Setting up local environment for local testing..."

# =============================================================================
# LOCAL TESTING CONFIGURATION
# =============================================================================
# Set environment for local testing
export TEST_ENVIRONMENT="local"

# =============================================================================
# APPIUM CONFIGURATION
# =============================================================================
# Local Appium server configuration
export APPIUM_SERVER_IP="127.0.0.1"
export APPIUM_SERVER_PORT="4723"
export APPIUM_SERVER_PATH="/"

# =============================================================================
# TEST DATA CONFIGURATION
# =============================================================================
# Test data for local environment
export TEST_PHONE_NUMBER="8130704808"
export TEST_OTP="912912"
export TEST_WITHDRAW_AMOUNT="100"

# =============================================================================
# DEVICE CONFIGURATION
# =============================================================================
# Device capabilities for local testing
export DEVICE_PLATFORM_NAME="Android"
export DEVICE_PLATFORM_VERSION="13.0"
export DEVICE_NAME="Galaxy S23"
export DEVICE_AUTOMATION_NAME="UiAutomator2"
export DEVICE_APP_PACKAGE="com.paytm.paytmplay"
export DEVICE_APP_ACTIVITY="com.gamepind.login.ui.LoginActivity"

# =============================================================================
# WAIT CONFIGURATION
# =============================================================================
# Wait timeouts for local environment (shorter for local testing)
export WAIT_TIMEOUT_SHORT="3"
export WAIT_TIMEOUT_MEDIUM="8"
export WAIT_TIMEOUT_LONG="20"
export WAIT_TIMEOUT_VERY_LONG="40"

# =============================================================================
# REPORTING CONFIGURATION
# =============================================================================
# Local reporting settings
export REPORT_TITLE="PFG Automation Test Report (Local)"
export REPORT_NAME="PFG Mobile App Test Report - Local Environment"
export REPORT_DOCUMENT_TITLE="PFG Test Results - Local"
export REPORT_THEME="standard"

# Screenshot configuration for local testing
export SCREENSHOT_ON_FAILURE="true"
export SCREENSHOT_ON_SUCCESS="true"
export SCREENSHOT_PATH="test-output/screenshots/local/"

# =============================================================================
# LOGGING CONFIGURATION
# =============================================================================
# More detailed logging for local debugging
export LOGGING_LEVEL_ROOT="DEBUG"
export LOGGING_LEVEL_ORG_FG="DEBUG"
export LOGGING_LEVEL_ORG_PFG="DEBUG"
export LOGGING_LEVEL_ORG_TESTNG="DEBUG"

# =============================================================================
# TEST EXECUTION CONFIGURATION
# =============================================================================
# Local test execution settings
export TEST_PARALLEL_ENABLED="false"
export TEST_THREAD_COUNT="1"
export TEST_RETRY_COUNT="0"
export TEST_TIMEOUT="180"

# =============================================================================
# VALIDATION
# =============================================================================
echo "Local environment variables set:"
echo "Environment: $TEST_ENVIRONMENT"
echo "Appium Server: $APPIUM_SERVER_IP:$APPIUM_SERVER_PORT"
echo "Test Phone: $TEST_PHONE_NUMBER"
echo "Device: $DEVICE_NAME ($DEVICE_PLATFORM_VERSION)"
echo "Parallel Execution: $TEST_PARALLEL_ENABLED"
echo "Thread Count: $TEST_THREAD_COUNT"
echo "Screenshot Path: $SCREENSHOT_PATH"

echo "Environment setup completed successfully!"
echo ""
echo "To run tests with this configuration:"
echo "  mvn test -Dtest.environment=local"
echo ""
echo "Or use the run scripts:"
echo "  ./run-tests.sh"
echo "  ./run-android-tests.sh"
echo ""
echo "Note: Make sure Appium server is running on $APPIUM_SERVER_IP:$APPIUM_SERVER_PORT" 