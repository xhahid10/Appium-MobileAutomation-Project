package org.pfg;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.Assert;
import org.testng.ITestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fg.pageObjects.android.LoginScreen;
import org.fg.pageObjects.android.OtpScreen;
import org.fg.pageObjects.android.CallbreakLobby;
import org.fg.pageObjects.android.HamburgerMenuCallbreak;
import org.fg.pageObjects.android.MyBalancePage;
import org.fg.utils.ConfigManager;
import org.fg.utils.TestReporter;

public class WithdrawMoneyTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawMoneyTest.class);
    
    // Configuration manager instance
    private static final ConfigManager config = ConfigManager.getInstance();
    
    // Test data from configuration - will be initialized at runtime
    private String testPhoneNumber;
    private String testOtp;
    private String withdrawAmount;

    /**
     * Initialize test data from configuration
     */
    private void initializeTestData() {
        testPhoneNumber = config.getProperty("test.phone.number");
        testOtp = config.getProperty("test.otp");
        withdrawAmount = config.getProperty("test.withdraw.amount");
        
        logger.info("Test data initialized - Phone: {}, OTP: {}, Amount: {}", 
                   testPhoneNumber, testOtp, withdrawAmount);
    }



    /**
     * Test money withdrawal flow with deposit transfer
     */
    @Test(description = "Test money withdrawal flow with deposit transfer")
    public void testWithdrawMoney() {
        try {
            logger.info("Starting money withdrawal test with deposit transfer");
            
            // Initialize test data
            initializeTestData();
            
            // STEP 1: Login to the app
            performLogin();
            
            // STEP 2: Navigate to My Balance
            MyBalancePage myBalancePage = navigateToMyBalance();
            
            // STEP 3: Perform withdrawal with deposit transfer
            MyBalancePage.WithdrawalResult result = myBalancePage.performWithdrawalWithDailyLimitCheck("deposit");
            
            // STEP 4: Verify withdrawal result
            Assert.assertTrue(result.isSuccess() || result.getStatus().equals("limit_reached"), 
                "Withdrawal should be successful or hit daily limit. Status: " + result.getStatus());
            
            logger.info("Money withdrawal test with deposit transfer completed successfully");
            
        } catch (Exception e) {
            logger.error("Money withdrawal test with deposit transfer failed: " + e.getMessage(), e);
            throw new RuntimeException("Money withdrawal test with deposit transfer failed: " + e.getMessage(), e);
        }
    }

    /**
     * Test money withdrawal flow with bank transfer
     */
    @Test(description = "Test money withdrawal flow with bank transfer")
    public void testWithdrawMoneyBankTransfer() {
        try {
            logger.info("Starting money withdrawal test with bank transfer");
            
            // Initialize test data
            initializeTestData();
            
            // STEP 1: Login to the app
            performLogin();
            
            // STEP 2: Navigate to My Balance
            MyBalancePage myBalancePage = navigateToMyBalance();
            
            // STEP 3: Perform withdrawal with bank transfer
            MyBalancePage.WithdrawalResult result = myBalancePage.performWithdrawalWithDailyLimitCheck("bank");
            
            // STEP 4: Verify withdrawal result
            Assert.assertTrue(result.isSuccess() || result.getStatus().equals("limit_reached"), 
                "Withdrawal should be successful or hit daily limit. Status: " + result.getStatus());
            
            logger.info("Money withdrawal test with bank transfer completed successfully");
            
        } catch (Exception e) {
            logger.error("Money withdrawal test with bank transfer failed: " + e.getMessage(), e);
            throw new RuntimeException("Money withdrawal test with bank transfer failed: " + e.getMessage(), e);
        }
    }

    /**
     * Test money withdrawal flow with UPI transfer
     */
    @Test(description = "Test money withdrawal flow with UPI transfer")
    public void testWithdrawMoneyUPITransfer() {
        try {
            logger.info("Starting UPI withdrawal test");
            
            // Initialize test data
            initializeTestData();
            
            logger.info("Starting UPI withdrawal test - Amount: ₹{}", withdrawAmount);
            
            // STEP 1: Login to the app
            performLogin();
            
            // STEP 2: Navigate to My Balance
            MyBalancePage myBalancePage = navigateToMyBalance();
            
            // STEP 3: Perform withdrawal with UPI transfer
            MyBalancePage.WithdrawalResult result = myBalancePage.performWithdrawalWithDailyLimitCheck("upi", withdrawAmount);
            
            // STEP 4: Verify withdrawal result
            boolean isSuccess = false;
            String resultStatus = result.getStatus();
            
            if (result.isSuccess()) {
                logger.info("UPI withdrawal completed successfully");
                isSuccess = true;
            } else if (resultStatus.equals("limit_reached")) {
                logger.info("Daily withdrawal limit reached (expected behavior)");
                isSuccess = true;
            } else if (resultStatus.equals("denied")) {
                // The error message is already captured and logged in the WithdrawalResultPage
                isSuccess = false;
            } else if (resultStatus.equals("in_progress")) {
                logger.info("UPI withdrawal is in progress");
                isSuccess = true; // In progress is considered a valid state
            } else {
                logger.warn("Unknown withdrawal result status: {}", resultStatus);
                isSuccess = false;
            }
            
            // Assert based on the result
            if (isSuccess) {
                logger.info("Withdrawal result validation passed");
            } else {
                logger.error("Withdrawal result validation failed - Status: {}", resultStatus);
                Assert.fail("Withdrawal failed with status: " + resultStatus);
            }
            
            logger.info("UPI withdrawal test completed successfully");
            
        } catch (Exception e) {
            logger.error("UPI withdrawal test failed: " + e.getMessage(), e);
            throw new RuntimeException("Money withdrawal test with UPI transfer failed: " + e.getMessage(), e);
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
     * Navigate to My Balance page
     */
    private MyBalancePage navigateToMyBalance() {
        try {
            logger.info("Navigating to My Balance page");
            
            // Open hamburger menu
            CallbreakLobby lobby = new CallbreakLobby(driver);
            HamburgerMenuCallbreak hamburgerMenu = lobby.openHamburgerMenu();
            
            // Navigate to My Balance
            MyBalancePage myBalancePage = hamburgerMenu.openMyBalance();
            
            // Wait for My Balance page to load
            Thread.sleep(3000);
            
            logger.info("Successfully navigated to My Balance page");
            return myBalancePage;
            
        } catch (Exception e) {
            logger.error("Failed to navigate to My Balance: " + e.getMessage(), e);
            throw new RuntimeException("Failed to navigate to My Balance: " + e.getMessage(), e);
        }
    }
} 