# Appium Framework Project Documentation

## Project Overview
This is an Appium-based test automation framework for mobile application testing.

## Technical Specifications

### Java Version
- Java 11

### Key Dependencies
1. **Selenium**
   - Version: 4.15.0
   - Purpose: Web automation and testing

2. **Appium Java Client**
   - Version: 8.6.0
   - Purpose: Mobile application testing

3. **TestNG**
   - Version: 7.8.0
   - Purpose: Test execution and reporting

4. **SLF4J**
   - Version: 2.0.12
   - Purpose: Logging framework

5. **Other Dependencies**
   - Commons IO: 2.16.1
   - Jackson Databind: 2.17.2
   - JSON: 20211205

### Build Configuration
- Maven Compiler Plugin: 3.11.0
- Maven Surefire Plugin: 3.2.5

### Test Execution Profiles
1. Android Testing
   - Single device execution
   - Parallel execution

2. iOS Testing
   - Single device execution
   - Parallel execution

3. Multi-device Testing
   - Sequential execution across multiple devices

## Project Structure
The framework follows a modular architecture with separate components for:
- Test execution
- Reporting
- Device management
- Test data handling

## Running Tests
Tests can be executed using different Maven profiles:
```bash
# For Android single device testing
mvn clean test -P android-single

# For Android parallel testing
mvn clean test -P android-parallel

# For iOS single device testing
mvn clean test -P ios-single

# For iOS parallel testing
mvn clean test -P ios-parallel

# For multi-device sequential testing
mvn clean test -P multi-device
``` 