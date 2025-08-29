package org.fg.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Configuration Manager for handling all configuration properties
 * Supports loading from properties files, environment variables, and system properties
 * with proper fallback mechanisms and environment-specific configurations.
 */
public class ConfigManager {
    private static final Logger logger = Logger.getLogger(ConfigManager.class.getName());
    private static ConfigManager instance;
    private Properties properties;
    private String environment;

    // Configuration file paths
    private static final String MAIN_CONFIG_FILE = "src/main/java/org/fg/resources/config.properties";
    private static final String LOCAL_CONFIG_FILE = "src/main/java/org/fg/resources/environments/local.properties";
    private static final String REMOTE_CONFIG_FILE = "src/main/java/org/fg/resources/environments/remote.properties";
    private static final String CI_CONFIG_FILE = "src/main/java/org/fg/resources/environments/ci.properties";

    private ConfigManager() {
        properties = new Properties();
        loadConfiguration();
    }

    /**
     * Get singleton instance of ConfigManager
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Load configuration from properties files and environment variables
     */
    private void loadConfiguration() {
        try {
            // Determine environment
            environment = determineEnvironment();
            logger.info("Loading configuration for environment: " + environment);

            // Load main configuration file
            loadPropertiesFile(MAIN_CONFIG_FILE, "Main configuration");

            // Load environment-specific configuration file
            String envConfigFile = getEnvironmentConfigFile();
            if (envConfigFile != null) {
                loadPropertiesFile(envConfigFile, "Environment-specific configuration");
            }

            // Override with environment variables
            overrideWithEnvironmentVariables();

            // Override with system properties
            overrideWithSystemProperties();

            logger.info("Configuration loaded successfully. Total properties: " + properties.size());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load configuration", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Determine the current environment
     */
    private String determineEnvironment() {
        // Check system property first
        String env = System.getProperty("test.environment");
        if (env != null && !env.trim().isEmpty()) {
            return env.toLowerCase();
        }

        // Check environment variable
        env = System.getenv("TEST_ENVIRONMENT");
        if (env != null && !env.trim().isEmpty()) {
            return env.toLowerCase();
        }

        // Check if running in CI/CD
        if (System.getenv("JENKINS_URL") != null || System.getenv("BUILD_NUMBER") != null) {
            return "ci";
        }

        // Default to remote (LambdaTest) instead of local
        return "remote";
    }

    /**
     * Get environment-specific configuration file path
     */
    private String getEnvironmentConfigFile() {
        switch (environment) {
            case "local":
                return LOCAL_CONFIG_FILE;
            case "remote":
                return REMOTE_CONFIG_FILE;
            case "ci":
                return CI_CONFIG_FILE;
            default:
                logger.warning("Unknown environment: " + environment + ". Using local configuration.");
                return LOCAL_CONFIG_FILE;
        }
    }

    /**
     * Load properties from a file
     */
    private void loadPropertiesFile(String filePath, String description) {
        try (InputStream input = new FileInputStream(filePath)) {
            Properties fileProperties = new Properties();
            fileProperties.load(input);
            
            // Merge with existing properties (environment-specific overrides main)
            properties.putAll(fileProperties);
            
            logger.info("Loaded " + fileProperties.size() + " properties from " + description + ": " + filePath);
            
        } catch (IOException e) {
            logger.warning("Could not load " + description + " from " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Override properties with environment variables
     */
    private void overrideWithEnvironmentVariables() {
        // LambdaTest credentials
        overrideProperty("lt.username", "LT_USERNAME");
        overrideProperty("lt.access.key", "LT_ACCESS_KEY");
        overrideProperty("lt.app.id", "LT_APP_ID");
        overrideProperty("lt.grid.url", "LT_GRID_URL");

        // Appium configuration
        overrideProperty("appium.server.ip", "APPIUM_SERVER_IP");
        overrideProperty("appium.server.port", "APPIUM_SERVER_PORT");
        overrideProperty("appium.server.path", "APPIUM_SERVER_PATH");

        // Test data
        overrideProperty("test.phone.number", "TEST_PHONE_NUMBER");
        overrideProperty("test.otp", "TEST_OTP");
        overrideProperty("test.withdraw.amount", "TEST_WITHDRAW_AMOUNT");

        // Device configuration
        overrideProperty("device.platform.name", "DEVICE_PLATFORM_NAME");
        overrideProperty("device.platform.version", "DEVICE_PLATFORM_VERSION");
        overrideProperty("device.name", "DEVICE_NAME");
        overrideProperty("device.automation.name", "DEVICE_AUTOMATION_NAME");
        overrideProperty("device.app.package", "DEVICE_APP_PACKAGE");
        overrideProperty("device.app.activity", "DEVICE_APP_ACTIVITY");

        // Wait configuration
        overrideProperty("wait.timeout.short", "WAIT_TIMEOUT_SHORT");
        overrideProperty("wait.timeout.medium", "WAIT_TIMEOUT_MEDIUM");
        overrideProperty("wait.timeout.long", "WAIT_TIMEOUT_LONG");
        overrideProperty("wait.timeout.very.long", "WAIT_TIMEOUT_VERY_LONG");

        // Reporting configuration
        overrideProperty("report.title", "REPORT_TITLE");
        overrideProperty("report.name", "REPORT_NAME");
        overrideProperty("report.document.title", "REPORT_DOCUMENT_TITLE");
        overrideProperty("report.theme", "REPORT_THEME");

        // Screenshot configuration
        overrideProperty("screenshot.on.failure", "SCREENSHOT_ON_FAILURE");
        overrideProperty("screenshot.on.success", "SCREENSHOT_ON_SUCCESS");
        overrideProperty("screenshot.path", "SCREENSHOT_PATH");

        // Logging configuration
        overrideProperty("logging.level.root", "LOGGING_LEVEL_ROOT");
        overrideProperty("logging.level.org.fg", "LOGGING_LEVEL_ORG_FG");
        overrideProperty("logging.level.org.pfg", "LOGGING_LEVEL_ORG_PFG");
        overrideProperty("logging.level.org.testng", "LOGGING_LEVEL_ORG_TESTNG");

        // Test execution configuration
        overrideProperty("test.parallel.enabled", "TEST_PARALLEL_ENABLED");
        overrideProperty("test.thread.count", "TEST_THREAD_COUNT");
        overrideProperty("test.retry.count", "TEST_RETRY_COUNT");
        overrideProperty("test.timeout", "TEST_TIMEOUT");

        // Browser configuration
        overrideProperty("browser.name", "BROWSER_NAME");
        overrideProperty("browser.headless", "BROWSER_HEADLESS");
        overrideProperty("browser.window.size", "BROWSER_WINDOW_SIZE");

        logger.info("Environment variables override completed");
    }

    /**
     * Override a property with environment variable if it exists
     */
    private void overrideProperty(String propertyKey, String envVarName) {
        String envValue = System.getenv(envVarName);
        if (envValue != null && !envValue.trim().isEmpty()) {
            properties.setProperty(propertyKey, envValue.trim());
            logger.fine("Overridden " + propertyKey + " with environment variable " + envVarName);
        }
    }

    /**
     * Override properties with system properties
     */
    private void overrideWithSystemProperties() {
        // System properties take highest precedence
        for (String key : properties.stringPropertyNames()) {
            String sysValue = System.getProperty(key);
            if (sysValue != null && !sysValue.trim().isEmpty()) {
                properties.setProperty(key, sysValue.trim());
                logger.fine("Overridden " + key + " with system property");
            }
        }
    }

    /**
     * Get a string property value
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Get a string property value with default
     */
    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warning("Property not found: " + key + ". Using default: " + defaultValue);
            return defaultValue;
        }
        // Add debug logging for test data properties
        if (key.contains("test.") || key.contains("phone") || key.contains("otp")) {
            logger.info("DEBUG: Retrieved property " + key + " = " + value);
        }
        return value;
    }

    /**
     * Get an integer property value
     */
    public int getIntProperty(String key) {
        return getIntProperty(key, 0);
    }

    /**
     * Get an integer property value with default
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid integer value for property " + key + ": " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Get a boolean property value
     */
    public boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, false);
    }

    /**
     * Get a boolean property value with default
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Get a long property value
     */
    public long getLongProperty(String key) {
        return getLongProperty(key, 0L);
    }

    /**
     * Get a long property value with default
     */
    public long getLongProperty(String key, long defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid long value for property " + key + ": " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Get the current environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Check if running in local environment
     */
    public boolean isLocalEnvironment() {
        return "local".equals(environment);
    }

    /**
     * Check if running in remote environment
     */
    public boolean isRemoteEnvironment() {
        return "remote".equals(environment);
    }

    /**
     * Check if running in CI/CD environment
     */
    public boolean isCIEnvironment() {
        return "ci".equals(environment);
    }

    /**
     * Get all properties as a copy
     */
    public Properties getAllProperties() {
        return new Properties(properties);
    }

    /**
     * Reload configuration (useful for testing)
     */
    public void reloadConfiguration() {
        properties.clear();
        loadConfiguration();
    }

    /**
     * Print current configuration (for debugging)
     */
    public void printConfiguration() {
        logger.info("=== Current Configuration ===");
        logger.info("Environment: " + environment);
        for (String key : properties.stringPropertyNames()) {
            // Mask sensitive information
            String value = key.contains("password") || key.contains("key") || key.contains("secret") 
                ? "***MASKED***" 
                : properties.getProperty(key);
            logger.info(key + " = " + value);
        }
        logger.info("=== End Configuration ===");
    }
} 