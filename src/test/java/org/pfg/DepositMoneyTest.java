package org.pfg;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.ITestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fg.pageObjects.android.LoginScreen;
import org.fg.pageObjects.android.OtpScreen;
import org.fg.pageObjects.android.CallbreakLobby;
import org.fg.pageObjects.android.MyBalancePage;
import org.fg.pageObjects.android.AddCashPage;
import org.fg.pageObjects.android.PaymentMethodPage;
import org.fg.pageObjects.android.AddCashResultPage;
import org.fg.utils.ConfigManager;

public class DepositMoneyTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DepositMoneyTest.class);
    
    // Configuration manager instance
    private static final ConfigManager config = ConfigManager.getInstance();
    
    // Test data from configuration - will be initialized at runtime
    private String testPhoneNumber;
    private String testOtp;
    private String depositAmount;

    /**
     * Initialize test data from configuration
     */
    private void initializeTestData() {
        testPhoneNumber = config.getProperty("test.phone.number");
        testOtp = config.getProperty("test.otp");
        depositAmount = config.getProperty("test.deposit.amount");
        
        logger.info("Test data initialized - Phone: {}, OTP: {}, Amount: {}", 
                   testPhoneNumber, testOtp, depositAmount);
    }



    /**
     * Navigate to Add Cash page using wallet header plus icon
     */
    private AddCashPage navigateToAddCash() {
        try {
            logger.info("Navigating to Add Cash page using wallet header plus icon");
            
            // Use the direct wallet header plus icon from lobby
            CallbreakLobby lobby = new CallbreakLobby(driver);
            AddCashPage addCashPage = lobby.clickWalletHeaderPlusIcon();
            
            // Wait for Add Cash page to load
            Thread.sleep(3000);
            
            logger.info("Successfully navigated to Add Cash page");
            return addCashPage;
            
        } catch (Exception e) {
            logger.error("Failed to navigate to Add Cash page: " + e.getMessage(), e);
            throw new RuntimeException("Failed to navigate to Add Cash page: " + e.getMessage(), e);
        }
    }

    /**
     * Test money deposit flow
     */
    @Test(description = "Test money deposit flow")
    public void testDepositMoney() {
        try {
            logger.info("Starting money deposit test");
            
            // Initialize test data
            initializeTestData();
            
            logger.info("Starting money deposit test - Amount: ₹{}", depositAmount);
            
            // STEP 1: Login to the app
            performLogin();
            
            // STEP 2: Navigate to Add Cash page using wallet header plus icon
            AddCashPage addCashPage = navigateToAddCash();
            
            // STEP 3: Perform deposit
            MyBalancePage.DepositResult result = performDepositFlow(addCashPage, depositAmount);
            
            // STEP 4: Verify deposit result
            boolean isSuccess = false;
            String resultStatus = result.getStatus();
            
            if (result.isSuccess()) {
                logger.info("Deposit completed successfully");
                isSuccess = true;
            } else if (resultStatus.equals("limit_reached")) {
                logger.info("Daily deposit limit reached (expected behavior)");
                isSuccess = true;
            } else if (resultStatus.equals("denied")) {
                logger.warn("Deposit was denied");
                isSuccess = false;
            } else if (resultStatus.equals("in_progress")) {
                logger.info("Deposit is in progress");
                isSuccess = true; // In progress is considered a valid state
            } else {
                logger.warn("Unknown deposit result status: {}", resultStatus);
                isSuccess = false;
            }
            
            // Assert based on the result
            if (isSuccess) {
                logger.info("Deposit result validation passed");
            } else {
                logger.error("Deposit result validation failed - Status: {}", resultStatus);
                Assert.fail("Deposit failed with status: " + resultStatus);
            }
            
            logger.info("Deposit money test completed successfully");
            
        } catch (Exception e) {
            logger.error("Deposit money test failed: " + e.getMessage(), e);
            throw new RuntimeException("Deposit money test failed: " + e.getMessage(), e);
        }
    }

    /**
     * Perform the complete deposit flow
     */
    private MyBalancePage.DepositResult performDepositFlow(AddCashPage addCashPage, String amount) {
        try {
            logger.info("Starting deposit flow for amount: ₹{}", amount);
            
            // Step 1: Enter the deposit amount
            logger.info("Entering deposit amount: ₹{}", amount);
            addCashPage.enterAmount(amount);
            
            // Step 2: Click proceed to pay button to go to payment method page
            PaymentMethodPage paymentMethodPage = addCashPage.clickProceedToPayButton();
            
            // Step 3: Select credit/debit card radio button
            logger.info("Selecting credit/debit card payment method");
            paymentMethodPage.selectCreditDebitCard();
            
            // Step 4: Verify pay button is clickable (without clicking it)
            logger.info("Verifying pay button is clickable");
            boolean isPayButtonClickable = paymentMethodPage.verifyPayButtonClickable();
            
            if (isPayButtonClickable) {
                logger.info("Pay button is clickable - test completed successfully");
                return new MyBalancePage.DepositResult(true, "success", "Pay button is clickable and ready");
            } else {
                logger.warn("Pay button is not clickable");
                return new MyBalancePage.DepositResult(false, "failed", "Pay button is not clickable");
            }
            
        } catch (InterruptedException e) {
            logger.error("Deposit flow interrupted: " + e.getMessage(), e);
            Thread.currentThread().interrupt();
            return new MyBalancePage.DepositResult(false, "interrupted", "Deposit flow was interrupted");
        } catch (Exception e) {
            logger.error("Failed to perform deposit flow: " + e.getMessage(), e);
            return new MyBalancePage.DepositResult(false, "error", "Add cash failed: " + e.getMessage());
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
} 