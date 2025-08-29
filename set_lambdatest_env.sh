#!/bin/bash

# =============================================================================
# LAMBDATEST ENVIRONMENT SETUP SCRIPT
# =============================================================================
# This script sets up LambdaTest environment variables for remote testing
# It uses the new configuration management system

echo "Setting up LambdaTest environment for remote testing..."

# =============================================================================
# LAMBDATEST CREDENTIALS
# =============================================================================
# Set LambdaTest credentials - These should be updated with your actual values
# For security, consider using environment variables or a secure credential manager

# Option 1: Direct assignment (update with your actual values)
export LT_USERNAME="ashish1.gusain"
export LT_ACCESS_KEY="3BzsKlO7JoZ7fBpfQgmsXY2u09aY6pIjcMio3KtLXBHEgotpah"
export LT_APP_ID="lt://APP1016032111750525162899896"

# Option 2: Read from file (create a secure credentials file)
# if [ -f ".lambdatest-credentials" ]; then
#     source .lambdatest-credentials
# fi

# Option 3: Prompt user for credentials (interactive mode)
# read -p "Enter LambdaTest Username: " LT_USERNAME
# read -s -p "Enter LambdaTest Access Key: " LT_ACCESS_KEY
# echo
# read -p "Enter LambdaTest App ID: " LT_APP_ID
# export LT_USERNAME
# export LT_ACCESS_KEY
# export LT_APP_ID

# =============================================================================
# ADDITIONAL CONFIGURATION
# =============================================================================
export LT_GRID_URL="mobile-hub.lambdatest.com"
export BUILD_NAME="Android Single Device Test"
export TEST_ENVIRONMENT="remote"

# =============================================================================
# VALIDATION
# =============================================================================
echo "LambdaTest environment variables set:"
echo "Username: $LT_USERNAME"
echo "Access Key: ${LT_ACCESS_KEY:0:8}..." # Mask sensitive data
echo "App ID: $LT_APP_ID"
echo "Grid URL: $LT_GRID_URL"
echo "Environment: $TEST_ENVIRONMENT"
echo "Build Name: $BUILD_NAME"

# Validate required variables
if [ -z "$LT_USERNAME" ] || [ -z "$LT_ACCESS_KEY" ] || [ -z "$LT_APP_ID" ]; then
    echo "ERROR: Required LambdaTest credentials are missing!"
    echo "Please update this script with your actual LambdaTest credentials."
    exit 1
fi

echo "Environment setup completed successfully!"
echo ""
echo "To run tests with this configuration:"
echo "  mvn test -Dtest.environment=remote"
echo ""
echo "Or use the run scripts:"
echo "  ./run-tests.sh"
echo "  ./run-android-tests.sh" 