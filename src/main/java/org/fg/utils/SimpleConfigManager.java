package org.fg.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Simple Configuration Manager - Loads only from test-data.properties
 * This makes configuration changes much simpler - just update one file!
 */
public class SimpleConfigManager {
    private static final Logger logger = Logger.getLogger(SimpleConfigManager.class.getName());
    private static SimpleConfigManager instance;
    private Properties properties;

    // Single configuration file
    private static final String TEST_DATA_FILE = "src/main/java/org/fg/resources/test-data.properties";

    private SimpleConfigManager() {
        properties = new Properties();
        loadTestData();
    }

    /**
     * Get singleton instance
     */
    public static synchronized SimpleConfigManager getInstance() {
        if (instance == null) {
            instance = new SimpleConfigManager();
        }
        return instance;
    }

    /**
     * Load test data from single file
     */
    private void loadTestData() {
        try (FileInputStream input = new FileInputStream(TEST_DATA_FILE)) {
            properties.load(input);
            logger.info("Loaded test data from: " + TEST_DATA_FILE);
            logger.info("Total properties loaded: " + properties.size());
        } catch (IOException e) {
            logger.severe("Failed to load test data from " + TEST_DATA_FILE + ": " + e.getMessage());
            throw new RuntimeException("Failed to load test data", e);
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
     * Print current configuration (for debugging)
     */
    public void printConfiguration() {
        logger.info("=== Test Data Configuration ===");
        for (String key : properties.stringPropertyNames()) {
            logger.info(key + " = " + properties.getProperty(key));
        }
        logger.info("=== End Configuration ===");
    }

    /**
     * Reload configuration (useful for testing)
     */
    public void reloadConfiguration() {
        properties.clear();
        loadTestData();
    }
} 