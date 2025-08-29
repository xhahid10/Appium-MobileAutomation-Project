#!/bin/bash

# Exit on error
set -e

# Function to handle errors
handle_error() {
    echo "Error occurred in $1"
    exit 1
}

# Function to display usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -s, --suite SUITE_FILE    TestNG suite file (default: android-single.xml)"
    echo "  -b, --build BUILD_NAME    LambdaTest build name (default: auto-generated)"
    echo "  -h, --help               Show this help message"
    echo ""
    echo "Available suite files:"
    ls -1 src/test/java/org/RunFiles/*.xml 2>/dev/null | sed 's|.*/||' || echo "No suite files found"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Run default suite with auto build name"
    echo "  $0 -s deposit-money-test.xml         # Run deposit money tests"
    echo "  $0 -s withdrawal-tests.xml           # Run withdrawal tests"
    echo "  $0 -s deposit-money-test.xml -b \"Deposit Test Run\"  # Run with custom build name"
    echo ""
}

# Set error handling
trap 'handle_error $LINENO' ERR

# Default values
SUITE_FILE="src/test/java/org/RunFiles/android-single.xml"
BUILD_NAME=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -s|--suite)
            SUITE_FILE="src/test/java/org/RunFiles/$2"
            shift 2
            ;;
        -b|--build)
            BUILD_NAME="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Set environment variables
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/Users/mohammedshahid/Library/Android/sdk
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH
export MAVEN_HOME=/opt/homebrew/Cellar/maven/3.9.9/libexec
export PATH=$MAVEN_HOME/bin:$PATH

# Print environment information
echo "=== Environment Information ==="
echo "JAVA_HOME: $JAVA_HOME"
echo "ANDROID_HOME: $ANDROID_HOME"
echo "PATH: $PATH"
echo "Suite File: $SUITE_FILE"
echo "Build Name: ${BUILD_NAME:-"Auto-generated"}"
echo "============================="

# Create reports directory structure
echo "Setting up reports directory..."
mkdir -p reports/archive reports/screenshots reports/logs
chmod -R 755 reports

# Generate timestamp for this run
TIMESTAMP=$(date +"%Y.%m.%d.%H.%M.%S")
ARCHIVE_DIR="reports/archive/$TIMESTAMP"
mkdir -p "$ARCHIVE_DIR"
chmod 755 "$ARCHIVE_DIR"

# Debug information
echo "=== Debug Information ==="
echo "Current directory: $(pwd)"
echo "Listing RunFiles directory:"
ls -la src/test/java/org/RunFiles/
echo "============================="

# Verify TestNG suite file exists
if [ ! -f "$SUITE_FILE" ]; then
    echo "Error: TestNG suite file not found at $SUITE_FILE"
    echo "Available suite files:"
    ls -l src/test/java/org/RunFiles/*.xml
    exit 1
fi

# Print file contents for verification
echo "=== TestNG Suite File Contents ==="
cat "$SUITE_FILE"
echo "============================="

# Build Maven command with optional build name
MAVEN_CMD="mvn clean test -DsuiteXmlFile=\"$SUITE_FILE\" -X"
if [ ! -z "$BUILD_NAME" ]; then
    MAVEN_CMD="$MAVEN_CMD -DBUILD_NAME=\"$BUILD_NAME\""
fi

# Clean and run tests
echo "Running tests with command: $MAVEN_CMD"
eval $MAVEN_CMD

# Check if tests were executed
if [ $? -eq 0 ]; then
    echo "Tests completed successfully"
else
    echo "Test execution failed"
    exit 1
fi

# Wait for a moment to ensure all files are written
sleep 2

# Create screenshots directory in the archive directory
mkdir -p "$ARCHIVE_DIR/screenshots"
chmod 755 "$ARCHIVE_DIR/screenshots"

# Move screenshots to the archive directory
if [ -d "test-output/screenshots" ]; then
    echo "Moving screenshots to archive directory..."
    cp -r test-output/screenshots/* "$ARCHIVE_DIR/screenshots/"
    chmod -R 755 "$ARCHIVE_DIR/screenshots"
fi

# Find and copy the Extent report
echo "Processing Extent Report..."
EXTENT_REPORT_PATH=$(find test-output -name "*.html" -type f -print0 | xargs -0 ls -t | head -n 1)

if [ ! -z "$EXTENT_REPORT_PATH" ]; then
    # Copy the report to the archive directory
    cp "$EXTENT_REPORT_PATH" "$ARCHIVE_DIR/index.html"
    chmod 644 "$ARCHIVE_DIR/index.html"
    
    # Update screenshot paths in the report
    echo "Updating screenshot paths in the report..."
    sed -i '' "s|/Users/mohammedshahid/eclipse-workspace/PFGAutomation/test-output/screenshots/|./screenshots/|g" "$ARCHIVE_DIR/index.html"
    
    # Create a symbolic link to the latest report
    echo "Creating symbolic link to latest report..."
    ln -sf "$ARCHIVE_DIR" "reports/latest"
    
    # Get the absolute path for the file:// link
    ABSOLUTE_PATH=$(cd "$(dirname "$ARCHIVE_DIR/index.html")" && pwd)/$(basename "$ARCHIVE_DIR/index.html")
    
    echo "==============================================="
    echo "Extent Report has been generated and archived"
    echo "Report Location: $ARCHIVE_DIR/index.html"
    echo "Latest Report Link: reports/latest/index.html"
    echo "Direct Link: file://$ABSOLUTE_PATH"
    echo "==============================================="
else
    echo "==============================================="
    echo "Warning: Extent report not found in test-output directory"
    echo "==============================================="
    
    # List contents of test-output directory for debugging
    echo "Contents of test-output directory:"
    ls -la test-output/
fi

# Create execution summary
echo "Test Execution Summary" > "$ARCHIVE_DIR/execution_summary.txt"
echo "=====================" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Timestamp: $(date)" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Suite File: $SUITE_FILE" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Build Name: ${BUILD_NAME:-"Auto-generated"}" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Status: $([ $? -eq 0 ] && echo "SUCCESS" || echo "FAILURE")" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Report Location: $ARCHIVE_DIR/index.html" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Latest Report: reports/latest/index.html" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Direct Link: file://$ABSOLUTE_PATH" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Screenshots: $ARCHIVE_DIR/screenshots/" >> "$ARCHIVE_DIR/execution_summary.txt"
echo "Logs: reports/logs/" >> "$ARCHIVE_DIR/execution_summary.txt"
chmod 644 "$ARCHIVE_DIR/execution_summary.txt"

# Cleanup old reports (keeping last 10)
echo "Cleaning up old reports..."
find reports/archive -maxdepth 1 -type d | sort -r | tail -n +11 | xargs -r rm -rf

# Cleanup old screenshots (older than 7 days)
echo "Cleaning up old screenshots..."
find reports/screenshots -type f -mtime +7 -delete

# Display final report locations
echo "==============================================="
echo "Test Report is available at:"
echo "$ARCHIVE_DIR/index.html"
echo "Latest Report is available at:"
echo "reports/latest/index.html"
echo "Direct Link:"
echo "file://$ABSOLUTE_PATH"
echo "Execution summary is available at:"
echo "$ARCHIVE_DIR/execution_summary.txt"
echo "==============================================="

echo "Test execution completed!" 