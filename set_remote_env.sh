#!/bin/bash

# Set environment variables for LambdaTest execution
export EXECUTION_MODE="remote"
export LT_USERNAME="ashish1.gusain"
export LT_ACCESS_KEY="3BzsKlO7JoZ7fBpfQgmsXY2u09aY6pIjcMio3KtLXBHEgotpah"
export LT_APP_ID="lt://APP1016032111750525162899896"
export LT_GRID_URL="mobile-hub.lambdatest.com"

# Device Farm Type
export DEVICE_FARM="lambdatest"

# Test Type
export TEST_TYPE="android-parallel"

echo "Environment variables set for LambdaTest:"
echo "EXECUTION_MODE: $EXECUTION_MODE"
echo "LT_USERNAME: $LT_USERNAME"
echo "LT_APP_ID: $LT_APP_ID"
echo "LT_GRID_URL: $LT_GRID_URL" 