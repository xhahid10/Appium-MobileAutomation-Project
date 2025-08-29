package org.pfg;

import java.time.Duration;
import org.fg.pageObjects.android.CallbreakLobby;
import org.fg.pageObjects.android.LoginScreen;
import org.fg.pageObjects.android.LoginScreenGetStarted;
import org.fg.pageObjects.android.HamburgerMenuCallbreak;
import org.fg.pageObjects.android.SettingPage;
import org.fg.pageObjects.android.OtpScreen;
import org.fg.utils.ConfigManager;
import org.fg.utils.TestReporter;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginLogoutFlow extends BaseTest {
    // Configuration manager instance
    private static final ConfigManager config = ConfigManager.getInstance();
    
    // Test data from configuration
    private static final int WAIT_TIMEOUT = config.getIntProperty("wait.timeout.medium", 15);
    private static final String TEST_PHONE_NUMBER = config.getProperty("test.phone.number");
    private static final String TEST_OTP = config.getProperty("test.otp");
    
    private void takeScreenshot(String stepName) {
        try {
            File screenshot = driver.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String screenshotPath = config.getProperty("screenshot.path", "test-output/screenshots/") + stepName + "_" + timestamp + ".png";
            FileUtils.copyFile(screenshot, new File(screenshotPath));
            TestReporter.logAction(deviceId, "Screenshot", "Screenshot captured: " + screenshotPath, driver);
        } catch (IOException e) {
            TestReporter.logError(deviceId, "Failed to capture screenshot", e);
        }
    }
    
    @Test
    public void testLoginToLogoutFlow() throws InterruptedException {
        TestReporter.startTest(deviceId, "Login to Logout Flow Test");
        try {
            // Initialize wait
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
            TestReporter.logAction(deviceId, "Init", "Initialized WebDriverWait with timeout: " + WAIT_TIMEOUT + " seconds", driver);
            
            // STEP 1: Get Started Screen
            TestReporter.startStep(deviceId, "Get Started Screen");
            TestReporter.logAction(deviceId, "Action", "Initializing Get Started Screen", driver);
            LoginScreenGetStarted getStarted = new LoginScreenGetStarted(driver);
            takeScreenshot("GetStartedScreen_Initial");
            TestReporter.logAction(deviceId, "Action", "Clicking Get Started button", driver);
            LoginScreen loginScreen = getStarted.getStarted();
            takeScreenshot("GetStartedScreen_AfterClick");
            TestReporter.logAction(deviceId, "Success", "Successfully navigated to Login Screen", driver);
            TestReporter.endStep(deviceId, "Get Started Screen");
            
            // STEP 2: Login Screen
            TestReporter.startStep(deviceId, "Login Screen");
            TestReporter.logAction(deviceId, "Flow", "Login Screen - Entering Phone Number: " + TEST_PHONE_NUMBER, driver);
            loginScreen.setNumberfield(TEST_PHONE_NUMBER);
            // Wait for the number to be properly entered
            wait.until(ExpectedConditions.attributeToBeNotEmpty(
                driver.findElement(By.id("com.paytm.paytmplay:id/edt_number")), 
                "text"
            ));
            
            // STEP 3: OTP Screen
            TestReporter.logAction(deviceId, "Flow", "Login Screen - Clicking Login Button", driver);
            OtpScreen otpScreen = loginScreen.loginButton();
            
            TestReporter.logAction(deviceId, "Flow", "OTP Screen - Entering OTP: " + TEST_OTP, driver);
            otpScreen.enterOTP(TEST_OTP);
            // Wait for OTP to be entered
            wait.until(ExpectedConditions.attributeToBeNotEmpty(
                driver.findElement(By.id("com.paytm.paytmplay:id/verify_input")), 
                "text"
            ));
            
            // Verify OTP
            TestReporter.logAction(deviceId, "Flow", "OTP Screen - Verifying OTP", driver);
            CallbreakLobby lobby = otpScreen.verifyButton();
            TestReporter.endStep(deviceId, "Login Screen");
            
            // STEP 4: Lobby
            TestReporter.startStep(deviceId, "Lobby");
            TestReporter.logAction(deviceId, "Action", "Initializing lobby after OTP login", driver);
            takeScreenshot("Lobby_Initial");
            TestReporter.logAction(deviceId, "Verify", "Verifying lobby is loaded", driver);
            Assert.assertTrue(lobby.isLobbyLoaded(), "Lobby failed to load");
            takeScreenshot("Lobby_Loaded");
            TestReporter.logAction(deviceId, "Success", "Successfully loaded Lobby", driver);
            TestReporter.endStep(deviceId, "Lobby");
            
            // STEP 5: Hamburger Menu
            TestReporter.startStep(deviceId, "Hamburger Menu");
            TestReporter.logAction(deviceId, "Action", "Clicking hamburger menu button", driver);
            HamburgerMenuCallbreak hamburgerMenu = lobby.openHamburgerMenu();
            takeScreenshot("HamburgerMenu_Opened");
            TestReporter.logAction(deviceId, "Success", "Hamburger menu opened successfully", driver);
            TestReporter.endStep(deviceId, "Hamburger Menu");
            
            // STEP 6: Settings Page
            TestReporter.startStep(deviceId, "Settings Page");
            TestReporter.logAction(deviceId, "Action", "Clicking settings button in hamburger menu", driver);
            SettingPage settingPage = hamburgerMenu.openSettings();
            takeScreenshot("SettingsPage_Initial");
            TestReporter.logAction(deviceId, "Verify", "Verifying settings page is loaded", driver);
            Assert.assertTrue(settingPage.isSettingsPageLoaded(), "Settings page failed to load");
            takeScreenshot("SettingsPage_Loaded");
            TestReporter.logAction(deviceId, "Success", "Successfully navigated to Settings page", driver);
            TestReporter.endStep(deviceId, "Settings Page");
            
            // STEP 7: Logout
            TestReporter.startStep(deviceId, "Logout");
            TestReporter.logAction(deviceId, "Action", "Clicking logout button in settings page", driver);
            takeScreenshot("Logout_BeforeClick");
            settingPage.logout();
            takeScreenshot("Logout_AfterConfirmation");
            TestReporter.logAction(deviceId, "Verify", "Verifying logout is successful", driver);
            Assert.assertTrue(loginScreen.isLoginScreenDisplayed(), "Logout failed - Login screen not displayed");
            takeScreenshot("Logout_Complete");
            TestReporter.logAction(deviceId, "Success", "Successfully logged out", driver);
            TestReporter.endStep(deviceId, "Logout");
            
            TestReporter.logAction(deviceId, "Summary", "Test completed successfully - Login to Logout flow verified", driver);
            
        } catch (Exception e) {
            takeScreenshot("Error_" + e.getClass().getSimpleName());
            TestReporter.logError(deviceId, "Test Flow Failed", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Login to Logout Flow Test");
        }
    }
} 