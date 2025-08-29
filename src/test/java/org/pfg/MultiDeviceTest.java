package org.pfg;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestContext;
import org.fg.utils.TestReporter;
import org.fg.utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multi-Device Test Class
 * 
 * This class runs the same test on multiple devices sequentially.
 * It's designed to be used in the sanity suite to verify app compatibility
 * across different devices and Android versions.
 */
public class MultiDeviceTest extends BaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(MultiDeviceTest.class);
    private static final ConfigManager config = ConfigManager.getInstance();
    
    // Test data from configuration
    private String testPhoneNumber;
    private String testOtp;
    
    /**
     * Initialize test data from configuration
     */
    private void initializeTestData() {
        testPhoneNumber = config.getProperty("test.phone.number");
        testOtp = config.getProperty("test.otp");
        
        logger.info("Test data initialized - Phone: {}, OTP: {}", testPhoneNumber, testOtp);
    }

    /**
     * Multi-device login to logout flow test
     * This test runs on multiple devices to verify app compatibility
     */
    @Test(description = "Multi-Device Login to Logout Flow Test")
    public void testMultiDeviceLoginToLogoutFlow() throws InterruptedException {
        try {
            logger.info("=== STARTING MULTI-DEVICE TEST ON DEVICE: {} ===", deviceId);
            TestReporter.startTest(deviceId, "Multi-Device Login to Logout Flow Test");
            
            // Initialize test data
            initializeTestData();
            
            // Create login logout test instance for this device
            LoginLogoutFlow loginLogoutTest = new LoginLogoutFlow();
            
            // Set the driver for the test class using reflection
            setDriverForTestClass(loginLogoutTest);
            
            // Execute the login to logout flow
            loginLogoutTest.testLoginToLogoutFlow();
            
            TestReporter.logAction(deviceId, "Success", "Multi-device login to logout flow completed successfully", driver);
            logger.info("Multi-device test completed successfully on device: {}", deviceId);
            
        } catch (Exception e) {
            logger.error("Multi-device test failed on device {}: {}", deviceId, e.getMessage(), e);
            TestReporter.logError(deviceId, "Multi-Device Test Failed", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Multi-Device Login to Logout Flow Test");
        }
    }
    
    /**
     * Set driver for a specific test class using reflection
     */
    private void setDriverForTestClass(Object testClass) {
        try {
            java.lang.reflect.Field driverField = testClass.getClass().getDeclaredField("driver");
            driverField.setAccessible(true);
            driverField.set(testClass, driver);
        } catch (Exception e) {
            logger.warn("Could not set driver for {}: {}", testClass.getClass().getSimpleName(), e.getMessage());
        }
    }
} 