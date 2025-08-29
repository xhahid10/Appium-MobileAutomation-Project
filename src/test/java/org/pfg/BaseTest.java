package org.pfg;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.fg.pageObjects.android.LoginScreenGetStarted;
import org.fg.utils.ConfigManager;
import org.fg.utils.TestReporter;
import org.testng.annotations.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;

public class BaseTest {
    // Configuration manager instance
    private static final ConfigManager config = ConfigManager.getInstance();
    
    // LambdaTest configuration
    private static final String USERNAME = config.getProperty("lt.username");
    private static final String ACCESS_KEY = config.getProperty("lt.access.key");
    private static final String APP_ID = config.getProperty("lt.app.id");
    private static final String GRID_URL = config.getProperty("lt.grid.url", "https://mobile-hub.lambdatest.com/wd/hub");

    protected AndroidDriver driver;
    protected LoginScreenGetStarted getStarted;
    protected String deviceId;
    protected Map<String, Object> additionalCapabilities = new HashMap<>();

    private void initializeDeviceId(String deviceName, String platformVersion) {
        deviceId = String.format("%s_%s", deviceName, platformVersion);
    }

    /**
     * Generate a unique build name based on test context and class
     */
    private String generateBuildName(ITestContext context) {
        // Priority 1: Use BUILD_NAME system property if set
        String buildName = System.getProperty("BUILD_NAME");
        if (buildName != null && !buildName.trim().isEmpty()) {
            return buildName;
        }
        
        // Priority 2: Use test name from XML
        String testName = context.getCurrentXmlTest().getName();
        if (testName != null && !testName.trim().isEmpty()) {
            return testName.replace("_", " ").replace("-", " ");
        }
        
        // Priority 3: Use class name
        String className = this.getClass().getSimpleName();
        if (className != null && !className.trim().isEmpty()) {
            return className.replace("Test", "").replace("Flow", "").replace("Navigation", "");
        }
        
        // Priority 4: Use default
        return "PFG Automation Test";
    }

    @BeforeMethod
    public void setUp(ITestContext context) throws MalformedURLException {
        try {
            // Log configuration for debugging
            System.out.println("[DEBUG] === Starting Test Setup ===");
            config.printConfiguration();
            
            Map<String, String> xmlParams = context.getCurrentXmlTest().getAllParameters();
            System.out.println("[DEBUG] XML Params: " + xmlParams);

            String platformName = xmlParams.get("platformName");
            String deviceName = xmlParams.get("deviceName");
            String platformVersion = xmlParams.get("platformVersion");

            System.out.println("[DEBUG] Platform Name: " + platformName);
            System.out.println("[DEBUG] Device Name: " + deviceName);
            System.out.println("[DEBUG] Platform Version: " + platformVersion);

            // Debug test data loading
            System.out.println("[DEBUG] Test Phone Number from config: " + config.getProperty("test.phone.number"));
            System.out.println("[DEBUG] Test OTP from config: " + config.getProperty("test.otp"));
            System.out.println("[DEBUG] Withdraw Amount from config: " + config.getProperty("test.withdraw.amount"));
            System.out.println("[DEBUG] Deposit Amount from config: " + config.getProperty("test.deposit.amount"));

            if (deviceName == null || platformVersion == null) {
                throw new IllegalArgumentException("deviceName and platformVersion must be provided in the test XML");
            }

            initializeDeviceId(deviceName, platformVersion);

            System.setProperty("webdriver.http.factory", "jdk-http-client");

            // Generate unique build name
            String buildName = generateBuildName(context);

            // Debug LambdaTest credentials
            System.out.println("[DEBUG] USERNAME: " + USERNAME);
            System.out.println("[DEBUG] ACCESS_KEY: " + (ACCESS_KEY != null ? "***SET***" : "***NULL***"));
            System.out.println("[DEBUG] APP_ID: " + APP_ID);
            System.out.println("[DEBUG] GRID_URL: " + GRID_URL);

            Map<String, Object> ltOptions = new HashMap<>();
            ltOptions.put("username", USERNAME);
            ltOptions.put("accessKey", ACCESS_KEY);
            ltOptions.put("platformName", platformName);
            ltOptions.put("deviceName", deviceName);
            ltOptions.put("platformVersion", platformVersion);
            ltOptions.put("app", APP_ID);
            ltOptions.put("project", "PFG Automation");
            ltOptions.put("build", buildName);
            ltOptions.put("isRealMobile", true);
            ltOptions.put("network", true);
            ltOptions.put("visual", true);
            ltOptions.put("video", true);
            ltOptions.put("console", true);
            ltOptions.put("w3c", true);
            ltOptions.put("autoGrantPermissions", true);
            ltOptions.put("autoAcceptAlerts", true);
            ltOptions.put("browserName", "chrome");
            ltOptions.put("gpsEnabled", true);
            ltOptions.put("enableNetworkInformation", true);
            ltOptions.put("autoAcceptPermissions", true);
            ltOptions.put("locationServicesEnabled", true);
            ltOptions.put("enableLocation", true);
            ltOptions.put("devicelog", true);
            ltOptions.put("terminalLog", true);
            ltOptions.put("networkLog", true);
            ltOptions.put("visualLog", true);
            ltOptions.put("deviceOrientation", "PORTRAIT");
            ltOptions.put("idleTimeout", config.getIntProperty("test.timeout", 300));
            ltOptions.put("newCommandTimeout", config.getIntProperty("test.timeout", 300));

            for (Map.Entry<String, String> entry : xmlParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.equals("platformName") || key.equals("deviceName") || key.equals("platformVersion")) {
                    continue;
                }

                try {
                    if (value.contains(".")) {
                        ltOptions.put(key, Double.parseDouble(value));
                    } else {
                        ltOptions.put(key, Integer.parseInt(value));
                    }
                } catch (NumberFormatException e) {
                    ltOptions.put(key, value);
                }
            }

            ltOptions.putAll(additionalCapabilities);

            UiAutomator2Options options = new UiAutomator2Options();
            options.setCapability("lt:options", ltOptions);

            // Set app-specific capabilities from configuration
            String appPackage = config.getProperty("device.app.package", "com.paytm.paytmplay");
            String appActivity = config.getProperty("device.app.activity", "com.gamepind.login.ui.LoginActivity");
            
            System.out.println("[DEBUG] App Package: " + appPackage);
            System.out.println("[DEBUG] App Activity: " + appActivity);
            
            options.setAppPackage(appPackage);
            options.setAppActivity(appActivity);
            options.setNoReset(true);
            options.setFullReset(false);

            System.out.println("[DEBUG] About to create AndroidDriver with URL: " + GRID_URL);
            driver = new AndroidDriver(new URL(GRID_URL), options);
            System.out.println("[DEBUG] AndroidDriver created successfully");
            
            getStarted = new LoginScreenGetStarted(driver);
            System.out.println("[DEBUG] LoginScreenGetStarted initialized");

            TestReporter.startStep(deviceId, "Test Setup");
            TestReporter.logAction(deviceId, "Setup", "Driver initialized successfully", driver);
            TestReporter.logAction(deviceId, "Parameters", "XML Parameters: " + xmlParams.toString(), driver);
            TestReporter.logAction(deviceId, "Configuration", "Environment: " + config.getEnvironment(), driver);
            TestReporter.logAction(deviceId, "Build Name", "Using build name: " + buildName, driver);
            TestReporter.endStep(deviceId, "Test Setup");
            
            System.out.println("[DEBUG] === Test Setup Completed Successfully ===");

        } catch (Exception e) {
            System.out.println("[DEBUG] === Test Setup Failed ===");
            System.out.println("[DEBUG] Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            TestReporter.logError(deviceId, "Setup failed", e);
            throw new RuntimeException("Test setup failed: " + e.getMessage(), e);
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            try {
                TestReporter.startStep(deviceId, "Test Cleanup");
                driver.quit();
                TestReporter.logAction(deviceId, "Success", "Driver quit successfully", driver);
            } catch (Exception e) {
                TestReporter.logError(deviceId, "Failed to quit driver", e);
            } finally {
                TestReporter.endStep(deviceId, "Test Cleanup");
            }
        }
    }

    public AndroidDriver getDriver() {
        return driver;
    }

    public String getDeviceId() {
        return deviceId;
    }
    
    /**
     * Get configuration manager instance
     */
    public static ConfigManager getConfig() {
        return config;
    }
}
