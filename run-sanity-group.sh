#!/bin/bash

# PFG Sanity Group Test Runner
# This script runs the sanity group tests using the sanity-suite.xml

echo "=========================================="
echo "PFG Sanity Group Test Suite"
echo "=========================================="
echo ""

# Set environment variables
export BUILD_NAME="PFG_Sanity_Group_$(date +%Y%m%d_%H%M%S)"

echo "Build Name: $BUILD_NAME"
echo ""

# Function to run sanity group tests
run_sanity_group_tests() {
    echo "Starting Sanity Group Test Suite..."
    echo "This will run:"
    echo "├── Test 1: Login to Logout Flow"
    echo "├── Test 2: Deposit Money Flow"
    echo "├── Test 3: Withdrawal Money Flow - Deposit Transfer"
    echo "├── Test 4: Withdrawal Money Flow - Bank Transfer"
    echo "├── Test 5: Withdrawal Money Flow - UPI Transfer"
    echo "├── Test 6: Hamburger Menu Navigation"
    echo "└── Test 7: Deep Link Navigation"
    echo ""
    echo "Device: Galaxy S23 (Android 13.0)"
    echo "Estimated Time: 8-12 minutes"
    echo ""
    
    # Run the sanity group suite
    mvn test -DsuiteXmlFile=src/test/resources/sanity-suite.xml \
        -Dlt.username="$LT_USERNAME" \
        -Dlt.access.key="$LT_ACCESS_KEY" \
        -Dlt.app.id="$LT_APP_ID" \
        -Dlt.grid.url="$LT_GRID_URL" \
        -Dtest.timeout=300 \
        -Dwait.timeout.medium=15 \
        -Dtest.phone.number="$TEST_PHONE_NUMBER" \
        -Dtest.otp="$TEST_OTP" \
        -Dtest.deposit.amount="$TEST_DEPOSIT_AMOUNT" \
        -Dtest.withdraw.amount="$TEST_WITHDRAW_AMOUNT" \
        -Dscreenshot.path="test-output/screenshots/" \
        -Dextent.report.path="test-output/reports/" \
        -Dlog.level=INFO
    
    # Check exit status
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Sanity Group Test Suite completed successfully!"
        echo "📊 Reports generated in: test-output/reports/"
        echo "📸 Screenshots saved in: test-output/screenshots/"
    else
        echo ""
        echo "❌ Sanity Group Test Suite failed!"
        echo "📊 Check reports in: test-output/reports/"
        echo "📸 Screenshots saved in: test-output/screenshots/"
        exit 1
    fi
}

# Function to show test execution plan
show_execution_plan() {
    echo "📋 Sanity Group Test Execution Plan:"
    echo "===================================="
    echo ""
    echo "Single Device Sanity Tests (Galaxy S23)"
    echo "├── Test 1: Login to Logout Flow"
    echo "├── Test 2: Deposit Money Flow"
    echo "├── Test 3: Withdrawal Money Flow - Deposit Transfer"
    echo "├── Test 4: Withdrawal Money Flow - Bank Transfer"
    echo "├── Test 5: Withdrawal Money Flow - UPI Transfer"
    echo "├── Test 6: Hamburger Menu Navigation"
    echo "└── Test 7: Deep Link Navigation"
    echo ""
    echo "⏱️  Estimated Time: 8-12 minutes"
    echo "💰 Estimated Cost: ~12 device-minutes"
    echo "📱 Device: Galaxy S23 (Android 13.0)"
    echo ""
}

# Function to show help
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -p, --plan          Show test execution plan"
    echo "  -r, --run           Run the sanity group tests"
    echo "  -a, --all           Show plan and run tests"
    echo ""
    echo "Environment Variables Required:"
    echo "  LT_USERNAME         LambdaTest username"
    echo "  LT_ACCESS_KEY       LambdaTest access key"
    echo "  LT_APP_ID           LambdaTest app ID"
    echo "  LT_GRID_URL         LambdaTest grid URL"
    echo "  TEST_PHONE_NUMBER   Test phone number"
    echo "  TEST_OTP            Test OTP code"
    echo "  TEST_DEPOSIT_AMOUNT Test deposit amount"
    echo "  TEST_WITHDRAW_AMOUNT Test withdrawal amount"
    echo ""
    echo "Examples:"
    echo "  $0 --plan                    # Show execution plan"
    echo "  $0 --run                     # Run sanity group tests"
    echo "  $0 --all                     # Show plan and run tests"
    echo ""
}

# Check if environment variables are set
check_environment() {
    local missing_vars=()
    
    if [ -z "$LT_USERNAME" ]; then missing_vars+=("LT_USERNAME"); fi
    if [ -z "$LT_ACCESS_KEY" ]; then missing_vars+=("LT_ACCESS_KEY"); fi
    if [ -z "$LT_APP_ID" ]; then missing_vars+=("LT_APP_ID"); fi
    if [ -z "$LT_GRID_URL" ]; then missing_vars+=("LT_GRID_URL"); fi
    if [ -z "$TEST_PHONE_NUMBER" ]; then missing_vars+=("TEST_PHONE_NUMBER"); fi
    if [ -z "$TEST_OTP" ]; then missing_vars+=("TEST_OTP"); fi
    if [ -z "$TEST_DEPOSIT_AMOUNT" ]; then missing_vars+=("TEST_DEPOSIT_AMOUNT"); fi
    if [ -z "$TEST_WITHDRAW_AMOUNT" ]; then missing_vars+=("TEST_WITHDRAW_AMOUNT"); fi
    
    if [ ${#missing_vars[@]} -ne 0 ]; then
        echo "❌ Error: Missing required environment variables:"
        for var in "${missing_vars[@]}"; do
            echo "   - $var"
        done
        echo ""
        echo "Please set these variables before running the tests."
        echo "You can use the set_remote_env.sh script to set them."
        exit 1
    fi
}

# Main script logic
case "${1:-}" in
    -h|--help)
        show_help
        ;;
    -p|--plan)
        show_execution_plan
        ;;
    -r|--run)
        check_environment
        run_sanity_group_tests
        ;;
    -a|--all)
        check_environment
        show_execution_plan
        echo "Press Enter to continue with test execution..."
        read
        run_sanity_group_tests
        ;;
    "")
        echo "❌ Error: No option specified"
        echo ""
        show_help
        exit 1
        ;;
    *)
        echo "❌ Error: Unknown option '$1'"
        echo ""
        show_help
        exit 1
        ;;
esac 