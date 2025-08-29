# Configuration Management System

## Overview

The PFG Automation project now uses a comprehensive configuration management system that eliminates hardcoded values and provides environment-specific configurations. This system supports multiple environments (local, remote, CI/CD) and follows security best practices.

## Architecture

### Configuration Hierarchy

The configuration system follows a hierarchical approach with the following precedence (highest to lowest):

1. **System Properties** (Runtime override)
2. **Environment Variables** (CI/CD, local environment)
3. **Environment-specific Properties Files** (local.properties, remote.properties, ci.properties)
4. **Main Configuration File** (config.properties)
5. **Default Values** (Hardcoded fallbacks)

### File Structure

```
src/main/java/org/fg/resources/
├── config.properties                    # Main configuration (committed)
├── data.properties                      # Legacy test data (deprecated)
├── multi_device_data.properties         # Multi-device test data
└── environments/
    ├── local.properties                 # Local environment (gitignored)
    ├── remote.properties                # Remote environment (gitignored)
    ├── ci.properties                    # CI/CD environment (gitignored)
    ├── local.template.properties        # Local template (committed)
    └── remote.template.properties       # Remote template (committed)
```

## Configuration Categories

### 1. LambdaTest Configuration
```properties
lt.username=your_username
lt.access.key=your_access_key
lt.app.id=your_app_id
lt.grid.url=https://mobile-hub.lambdatest.com/wd/hub
```

### 2. Appium Configuration
```properties
appium.server.ip=127.0.0.1
appium.server.port=4723
appium.server.path=/
```

### 3. Test Data Configuration
```properties
test.phone.number=8130704808
test.otp=912912
test.withdraw.amount=100
```

### 4. Device Configuration
```properties
device.platform.name=Android
device.platform.version=13.0
device.name=Galaxy S23
device.automation.name=UiAutomator2
device.app.package=com.paytm.paytmplay
device.app.activity=com.gamepind.login.ui.LoginActivity
```

### 5. Wait Configuration
```properties
wait.timeout.short=5
wait.timeout.medium=10
wait.timeout.long=30
wait.timeout.very.long=60
```

### 6. Reporting Configuration
```properties
report.title=PFG Automation Test Report
report.name=PFG Mobile App Test Report
report.document.title=PFG Test Results
report.theme=standard
screenshot.on.failure=true
screenshot.on.success=false
screenshot.path=test-output/screenshots/
```

### 7. Logging Configuration
```properties
logging.level.root=INFO
logging.level.org.fg=DEBUG
logging.level.org.pfg=DEBUG
logging.level.org.testng=INFO
```

### 8. Test Execution Configuration
```properties
test.parallel.enabled=true
test.thread.count=2
test.retry.count=1
test.timeout=300
```

## Environment Detection

The system automatically detects the environment based on:

1. **System Property**: `-Dtest.environment=local`
2. **Environment Variable**: `TEST_ENVIRONMENT=remote`
3. **CI/CD Detection**: Presence of `JENKINS_URL` or `BUILD_NUMBER`
4. **LambdaTest Credentials**: Presence of `LT_USERNAME` and `LT_ACCESS_KEY`
5. **Default**: Falls back to `local`

## Usage Examples

### 1. In Test Classes

```java
public class MyTest extends BaseTest {
    private static final ConfigManager config = ConfigManager.getInstance();
    
    // Get string properties
    private static final String PHONE_NUMBER = config.getProperty("test.phone.number");
    private static final String OTP = config.getProperty("test.otp", "912912");
    
    // Get numeric properties
    private static final int TIMEOUT = config.getIntProperty("wait.timeout.medium", 10);
    private static final long LONG_TIMEOUT = config.getLongProperty("wait.timeout.long", 30L);
    
    // Get boolean properties
    private static final boolean SCREENSHOT_ON_FAILURE = config.getBooleanProperty("screenshot.on.failure", true);
    
    @Test
    public void testExample() {
        // Use configuration values
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        // ... test logic
    }
}
```

### 2. In Page Objects

```java
public class LoginPage {
    private static final ConfigManager config = ConfigManager.getInstance();
    private static final int WAIT_TIMEOUT = config.getIntProperty("wait.timeout.medium");
    
    public void login(String phoneNumber) {
        // Use configured timeout
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
        // ... login logic
    }
}
```

### 3. Environment-Specific Configuration

```java
public class BaseTest {
    private static final ConfigManager config = ConfigManager.getInstance();
    
    @BeforeClass
    public void setUp() {
        // Log current environment
        System.out.println("Running in environment: " + config.getEnvironment());
        
        // Environment-specific logic
        if (config.isLocalEnvironment()) {
            // Local-specific setup
        } else if (config.isRemoteEnvironment()) {
            // Remote-specific setup
        } else if (config.isCIEnvironment()) {
            // CI/CD-specific setup
        }
    }
}
```

## Environment Setup

### 1. Local Development

1. Copy the template file:
   ```bash
   cp src/main/java/org/fg/resources/environments/local.template.properties \
      src/main/java/org/fg/resources/environments/local.properties
   ```

2. Edit `local.properties` with your actual values:
   ```properties
   lt.username=your_actual_username
   lt.access.key=your_actual_access_key
   lt.app.id=your_actual_app_id
   test.phone.number=8130704808
   test.otp=912912
   ```

3. Run tests:
   ```bash
   mvn test -Dtest.environment=local
   ```

### 2. Remote Testing (LambdaTest)

1. Copy the template file:
   ```bash
   cp src/main/java/org/fg/resources/environments/remote.template.properties \
      src/main/java/org/fg/resources/environments/remote.properties
   ```

2. Set environment variables:
   ```bash
   export LT_USERNAME=your_username
   export LT_ACCESS_KEY=your_access_key
   export LT_APP_ID=your_app_id
   ```

3. Run tests:
   ```bash
   mvn test -Dtest.environment=remote
   ```

### 3. CI/CD Pipeline

1. Set Jenkins credentials:
   - `LAMBDATEST_USERNAME`
   - `LAMBDATEST_ACCESS_KEY`
   - `LAMBDATEST_APP_ID`

2. The system automatically detects CI environment and uses appropriate configuration.

## Security Best Practices

### 1. Never Commit Sensitive Data

- ✅ Template files with placeholder values
- ❌ Actual configuration files with real credentials
- ❌ Hardcoded credentials in source code

### 2. Use Environment Variables

```bash
# Local development
export LT_USERNAME=your_username
export LT_ACCESS_KEY=your_access_key

# CI/CD
LT_USERNAME: ${{ secrets.LAMBDATEST_USERNAME }}
LT_ACCESS_KEY: ${{ secrets.LAMBDATEST_ACCESS_KEY }}
```

### 3. Use .gitignore

The `.gitignore` file excludes sensitive configuration files:
```gitignore
src/main/java/org/fg/resources/environments/*.properties
!src/main/java/org/fg/resources/environments/*.template.properties
```

### 4. Mask Sensitive Data in Logs

The `ConfigManager` automatically masks sensitive information in logs:
```java
config.printConfiguration(); // Passwords and keys are masked
```

## Migration Guide

### From Hardcoded Values

**Before:**
```java
private static final String USERNAME = "ashish1.gusain";
private static final String ACCESS_KEY = "3BzsKlO7JoZ7fBpfQgmsXY2u09aY6pIjcMio3KtLXBHEgotpah";
private static final String PHONE_NUMBER = "8130704808";
```

**After:**
```java
private static final ConfigManager config = ConfigManager.getInstance();
private static final String USERNAME = config.getProperty("lt.username");
private static final String ACCESS_KEY = config.getProperty("lt.access.key");
private static final String PHONE_NUMBER = config.getProperty("test.phone.number");
```

### From System.getProperty()

**Before:**
```java
private static String getConfigValue(String key, String defaultValue) {
    String value = System.getProperty(key);
    if (value != null && !value.trim().isEmpty()) {
        return value;
    }
    value = System.getenv(key);
    if (value != null && !value.trim().isEmpty()) {
        return value;
    }
    return defaultValue;
}
```

**After:**
```java
private static final ConfigManager config = ConfigManager.getInstance();
String value = config.getProperty(key, defaultValue);
```

## Troubleshooting

### 1. Configuration Not Loading

```java
// Debug configuration loading
ConfigManager config = ConfigManager.getInstance();
config.printConfiguration();
```

### 2. Environment Detection Issues

```java
// Check current environment
System.out.println("Environment: " + config.getEnvironment());
System.out.println("Is Local: " + config.isLocalEnvironment());
System.out.println("Is Remote: " + config.isRemoteEnvironment());
System.out.println("Is CI: " + config.isCIEnvironment());
```

### 3. Property Not Found

```java
// Use default values
String value = config.getProperty("missing.property", "default_value");
int timeout = config.getIntProperty("missing.timeout", 30);
boolean flag = config.getBooleanProperty("missing.flag", false);
```

### 4. Reload Configuration

```java
// Force reload configuration (useful for testing)
config.reloadConfiguration();
```

## Benefits

1. **Security**: No hardcoded credentials in source code
2. **Flexibility**: Environment-specific configurations
3. **Maintainability**: Centralized configuration management
4. **Scalability**: Easy to add new environments
5. **Debugging**: Comprehensive logging and debugging capabilities
6. **CI/CD Ready**: Seamless integration with CI/CD pipelines
7. **Team Collaboration**: Template files for easy setup

## Future Enhancements

1. **Configuration Validation**: Validate required properties
2. **Encryption**: Encrypt sensitive configuration values
3. **Dynamic Configuration**: Runtime configuration updates
4. **Configuration UI**: Web-based configuration management
5. **Configuration Versioning**: Track configuration changes 