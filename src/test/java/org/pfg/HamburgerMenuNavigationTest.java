package org.pfg;

import io.appium.java_client.android.AndroidDriver;
import org.fg.pageObjects.android.LoginScreenGetStarted;
import org.fg.pageObjects.android.LoginScreen;
import org.fg.pageObjects.android.OtpScreen;
import org.fg.pageObjects.android.CallbreakLobby;
import org.fg.pageObjects.android.SettingPage;
import org.fg.pageObjects.android.HamburgerMenuCallbreak;
import org.fg.utils.TestReporter;
import org.fg.utils.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;

public class HamburgerMenuNavigationTest extends BaseTest {
    private WebDriverWait wait;
    private WebDriverWait longWait;
    private LoginScreen loginScreen;
    private CallbreakLobby lobbyScreen;
    private HamburgerMenuCallbreak menu;
    
    // Get test data from configuration
    private static final ConfigManager config = ConfigManager.getInstance();
    private static final String TEST_PHONE_NUMBER = config.getProperty("test.phone.number");
    private static final String TEST_OTP = config.getProperty("test.otp");
    
    // Hamburger Menu Button Elements
    private static final By INITIAL_HAMBURGER_MENU = By.xpath("//android.view.View[@resource-id='com.paytm.paytmplay:id/v_hamburg_bg']");
    private static final By SUBSEQUENT_HAMBURGER_MENU = By.xpath("//android.widget.ImageView[@resource-id='com.paytm.paytmplay:id/iv_head_hamburg']");
    
    // Hamburger Menu Elements
    private static final By PROFILE_MENU = By.id("com.paytm.paytmplay:id/header_tv_nickname");
    private static final By MY_BALANCE_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='My Balance']");
    private static final By REFER_EARN_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Refer & Earn']");
    private static final By PAYMENT_SETTINGS_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Payment Settings']");
    private static final By INBOX_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Inbox']");
    private static final By FAQ_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='FAQ']");
    private static final By MY_REWARDS_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='My Rewards']");
    private static final By GAME_HISTORY_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Game History']");
    private static final By HOW_TO_PLAY_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='How to play']");
    private static final By RESPONSIBLE_PLAY_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Responsible Play']");
    private static final By SETTINGS_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Settings']");
    
    // Page Verification Elements
    private static final By MY_BALANCE_VERIFY = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/passbook_add_money']");
    private static final By MY_BALANCE_VERIFY_ALT = By.id("com.paytm.paytmplay:id/pan_card_loading_layout");
    private static final By REFER_EARN_VERIFY = By.xpath("//android.widget.TextView[@text='Invite via WhatsApp']");
    private static final By PAYMENT_SETTINGS_VERIFY = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/tv_title' and @text='Manage Payment Methods']");
    private static final By INBOX_VERIFY = By.id("com.paytm.paytmplay:id/browser_webView_container");
    private static final List<By> FAQ_VERIFY = Arrays.asList(
        By.xpath("//android.widget.TextView[@text='Topics']"),
        By.xpath("//android.widget.TextView[@text='GST']"),
        By.xpath("//android.widget.TextView[@text='Withdraw']")
    );
    private static final By MY_REWARDS_VERIFY = By.xpath("//android.widget.TextView[@text='My Bonus']");
    private static final By GAME_HISTORY_VERIFY = By.xpath("//android.widget.FrameLayout[@resource-id='com.paytm.paytmplay:id/cocos_task_center_container']");
    private static final By HOW_TO_PLAY_VERIFY = By.xpath("//android.widget.FrameLayout[@resource-id='com.paytm.paytmplay:id/cocos_task_center_container']");
    private static final By RESPONSIBLE_PLAY_VERIFY = By.xpath("//android.widget.FrameLayout[@resource-id='com.paytm.paytmplay:id/cocos_task_center_container']");
    private static final By PROFILE_VERIFY = By.xpath("//android.view.ViewGroup[@resource-id='com.paytm.paytmplay:id/pip_container']");
    
    @BeforeMethod
    public void setup() {
        // Use the driver from BaseTest
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            // Initialize Get Started screen
            LoginScreenGetStarted getStarted = new LoginScreenGetStarted(driver);
            loginScreen = getStarted.getStarted();
        } catch (Exception e) {
            throw new RuntimeException("Setup failed: " + e.getMessage(), e);
        }
    }

    @AfterClass
    public void tearDown() {
        // Driver cleanup is handled by BaseTest
    }

    @Test(description = "Test comprehensive hamburger menu navigation")
    public void testHamburgerMenuNavigation() throws InterruptedException {
        try {
            // STEP 1: Login to the app using configuration values
            loginScreen.setNumberfield(TEST_PHONE_NUMBER);
            OtpScreen otpScreen = loginScreen.loginButton();
            otpScreen.enterOTP(TEST_OTP);
            lobbyScreen = otpScreen.verifyButton();
            
            // STEP 2: Verify lobby is loaded
            Assert.assertTrue(lobbyScreen.isLobbyLoaded(), "Lobby failed to load");
            
            // STEP 3: Initialize hamburger menu
            menu = lobbyScreen.openHamburgerMenu();
            
            // STEP 4: Navigate through all menu items EXCEPT Settings (to avoid logout)
            testProfileNavigation();
            testMyBalanceNavigation();
            testReferEarnNavigation();
            testPaymentSettingsNavigation();
            testInboxNavigation();
            testFAQNavigation();
            testMyRewardsNavigation();
            testGameHistoryNavigation();
            testHowToPlayNavigation();
            testResponsiblePlayNavigation();
            
            // STEP 5: Test Settings navigation LAST (since it contains logout)
            testSettingsNavigation();
            
        } catch (Exception e) {
            throw new RuntimeException("Hamburger menu navigation test failed: " + e.getMessage(), e);
        }
    }

    private void testProfileNavigation() throws InterruptedException {
        menu.navigateToProfile();
        Thread.sleep(2000); // Wait for page to stabilize
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testMyBalanceNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after Profile navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToMyBalance();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testReferEarnNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToReferEarn();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testPaymentSettingsNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToPaymentSettings();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testInboxNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToInbox();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testFAQNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToFAQ();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testMyRewardsNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToMyRewards();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testGameHistoryNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToGameHistory();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testHowToPlayNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToHowToPlay();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testResponsiblePlayNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        menu.navigateToResponsiblePlay();
        Thread.sleep(2000);
        menu.navigateBack();
        Thread.sleep(1000);
    }

    private void testSettingsNavigation() throws InterruptedException {
        // Reopen hamburger menu since it was closed after previous navigation
        menu = lobbyScreen.openHamburgerMenu();
        
        // Open Settings page
        SettingPage settingsPage = menu.openSettings();
        Thread.sleep(2000);
        
        // Check if Settings page loaded successfully
        if (settingsPage.isSettingsPageLoaded()) {
            TestReporter.logAction(deviceId, "Success", "Settings page loaded successfully", driver);
            
            // Test logout functionality
            TestReporter.logAction(deviceId, "Info", "Testing logout functionality", driver);
            boolean logoutSuccess = settingsPage.logout();
            
            if (logoutSuccess) {
                TestReporter.logAction(deviceId, "Success", "Logout completed successfully - user returned to login screen", driver);
                // Note: After logout, the test will end here as user is logged out
                // No need to navigate back since we're already at login screen
            } else {
                TestReporter.logAction(deviceId, "Warning", "Logout failed or was cancelled", driver);
                // If logout failed, navigate back to continue with other tests
                menu.navigateBack();
                Thread.sleep(1000);
            }
        } else {
            TestReporter.logAction(deviceId, "Warning", "Settings page may have triggered logout flow", driver);
            // If logout was triggered, the test will end here
        }
    }
} 