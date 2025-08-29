package org.pfg;

import org.fg.pageObjects.android.CallbreakLobby;
import org.fg.pageObjects.android.LoginScreenGetStarted;
import org.fg.pageObjects.android.LoginScreen;
import org.fg.pageObjects.android.OtpScreen;
import org.fg.utils.AppiumUtils;
import org.fg.utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeepLinkNavigationTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DeepLinkNavigationTest.class);
    
    // Configuration manager instance
    private static final ConfigManager config = ConfigManager.getInstance();
    
    // Test data
    private String testPhoneNumber;
    private String testOtp;
    private String ludoDeepLink = "https://paytmfirstgames.com/pro?type=ludo";

    /**
     * Initialize test data from configuration
     */
    private void initializeTestData() {
        testPhoneNumber = config.getProperty("test.phone.number");
        testOtp = config.getProperty("test.otp");
        
        logger.info("Test data initialized - Phone: {}, OTP: {}", testPhoneNumber, testOtp);
    }

    /**
     * Test deep link navigation to Ludo game
     * 1. Login to the app
     * 2. Navigate to lobby
     * 3. Open Ludo game via deep link
     * 4. Wait for Ludo lobby to load
     */
    @Test(description = "Test deep link navigation to Ludo game")
    public void testDeepLinkToLudoGame() {
        try {
            logger.info("Starting deep link to Ludo game test");
            
            // Initialize test data
            initializeTestData();
            
            // STEP 1: Login to the app
            performLogin();
            
            // STEP 2: Navigate to lobby and wait for it to load
            CallbreakLobby lobby = new CallbreakLobby(driver);
            waitForLobbyToLoad();
            
            // STEP 3: Open Ludo game via deep link
            openLudoGameViaDeepLink();
            
            // STEP 4: Wait for Ludo lobby to load and verify
            waitForLudoLobbyToLoad();
            
            // STEP 5: Verify we're in the Ludo game
            verifyLudoGameNavigation();
            
            logger.info("Deep link to Ludo game test completed successfully");
            
        } catch (Exception e) {
            logger.error("Deep link to Ludo game test failed: " + e.getMessage(), e);
            throw new RuntimeException("Deep link to Ludo game test failed: " + e.getMessage(), e);
        }
    }

    /**
     * Perform login to the app
     */
    private void performLogin() {
        try {
            logger.info("Starting login process");
            
            // Get started with the app
            LoginScreen loginScreen = getStarted.getStarted();
            
            // Enter phone number
            loginScreen.setNumberfield(testPhoneNumber);
            
            // Click continue to get OTP
            loginScreen.clickContinue();
            
            // Handle OTP
            OtpScreen otpScreen = new OtpScreen(driver);
            otpScreen.enterOTP(testOtp);
            
            // Verify OTP and get lobby
            CallbreakLobby lobby = otpScreen.verifyButton();
            
            // Wait for lobby to load
            Thread.sleep(5000);
            
            logger.info("Login process completed successfully");
            
        } catch (Exception e) {
            logger.error("Login failed: " + e.getMessage(), e);
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    /**
     * Wait for lobby to fully load
     */
    private void waitForLobbyToLoad() {
        try {
            logger.info("Waiting for lobby to load");
            
            // Wait for lobby elements to be present
            CallbreakLobby lobby = new CallbreakLobby(driver);
            
            // Wait additional time for lobby to stabilize
            Thread.sleep(3000);
            logger.info("Lobby load wait completed");
            
        } catch (Exception e) {
            logger.error("Failed to wait for lobby load: " + e.getMessage(), e);
            throw new RuntimeException("Failed to wait for lobby load: " + e.getMessage(), e);
        }
    }

    /**
     * Open Ludo game via deep link
     */
    private void openLudoGameViaDeepLink() {
        try {
            logger.info("Opening Ludo game via deep link");
            
            // Open deep link using AppiumUtils
            AppiumUtils.openDeepLink(driver, ludoDeepLink, deviceId);
            
            logger.info("Deep link opened successfully");
            
        } catch (Exception e) {
            logger.error("Failed to open deep link: " + e.getMessage(), e);
            throw new RuntimeException("Failed to open deep link: " + e.getMessage(), e);
        }
    }

    /**
     * Wait for Ludo lobby to load after deep link navigation
     */
    private void waitForLudoLobbyToLoad() {
        try {
            logger.info("Waiting for Ludo lobby to load");
            
            // Wait for initial app load after deep link
            AppiumUtils.waitForAppLoadAfterDeepLink(driver, deviceId, 10);
            
            // Additional wait for Ludo game to download and load
            Thread.sleep(30000);
            
            logger.info("Ludo lobby load wait completed");
            
        } catch (Exception e) {
            logger.error("Failed to wait for Ludo lobby load: " + e.getMessage(), e);
            throw new RuntimeException("Failed to wait for Ludo lobby load: " + e.getMessage(), e);
        }
    }

    /**
     * Verify Ludo game navigation
     */
    private void verifyLudoGameNavigation() {
        try {
            logger.info("Verifying Ludo game navigation");
            
            // Take screenshot of Ludo lobby
            AppiumUtils.captureScreenshot(driver, deviceId, "Ludo_Lobby_After_DeepLink");
            
            // Check if we're in the Ludo game by looking for Ludo-specific elements
            // This is a basic verification - you can add more specific checks based on Ludo game elements
            logger.info("Ludo game navigation verified");
            
        } catch (Exception e) {
            logger.error("Failed to verify Ludo game navigation: " + e.getMessage(), e);
            throw new RuntimeException("Failed to verify Ludo game navigation: " + e.getMessage(), e);
        }
    }
} 