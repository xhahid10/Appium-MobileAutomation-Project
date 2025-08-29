package org.fg.pageObjects.android;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.fg.utils.TestReporter;
import java.time.Instant;

public class MyBalancePage {
    private AndroidDriver driver;
    private WebDriverWait wait;
    private String deviceId;

    // Withdraw button locator
    private static final By WITHDRAW_BUTTON = By.xpath("//android.widget.RelativeLayout[@resource-id='com.paytm.paytmplay:id/pan_card_loading_layout']");
    
    // Deposit button locator
    private static final By DEPOSIT_BUTTON = By.xpath("//android.widget.Button[@text='Deposit']");

    public MyBalancePage(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "HamburgerMenu", "MyBalancePage");
        TestReporter.logAction(deviceId, "Initialization", "MyBalancePage initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);

        // After initializing the page, add three taps on the screen
        performThreeTaps();
    }

    /**
     * Perform three taps on the balance page
     */
    public void performThreeTaps() {
        TestReporter.startStep(deviceId, "Perform Three Taps");
        try {
            TestReporter.logAction(deviceId, "Action", "Performing three taps on the balance page", driver);
            
            TouchAction touchAction = new TouchAction(driver);
            for (int i = 0; i < 3; i++) {
                touchAction.tap(TapOptions.tapOptions().withPosition(PointOption.point(500, 500))).perform();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            TestReporter.logAction(deviceId, "Success", "Three taps performed successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to perform three taps", e);
        } finally {
            TestReporter.endStep(deviceId, "Perform Three Taps");
        }
    }

    /**
     * Click withdraw button and navigate to withdrawal page
     * @return WithdrawalPage - The withdrawal page for entering amount and selecting transfer options
     */
    public WithdrawalPage clickWithdrawButton() {
        TestReporter.startStep(deviceId, "Click Withdraw Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for withdraw button to be clickable", driver);
            WebElement withdrawButton = wait.until(ExpectedConditions.elementToBeClickable(WITHDRAW_BUTTON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking withdraw button", driver);
            withdrawButton.click();
            
            TestReporter.logAction(deviceId, "Success", "Withdraw button clicked successfully", driver);
            
            TestReporter.logPageTransition(deviceId, "MyBalancePage", "WithdrawalPage");
            return new WithdrawalPage(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click withdraw button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Withdraw Button");
        }
    }

    /**
     * Check if My Balance page is loaded
     */
    public boolean isMyBalancePageLoaded() {
        TestReporter.startStep(deviceId, "Check My Balance Page Loaded");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for withdraw button to be visible", driver);
            WebElement withdrawButton = wait.until(ExpectedConditions.visibilityOfElementLocated(WITHDRAW_BUTTON));
            
            if (withdrawButton.isDisplayed()) {
                TestReporter.logAction(deviceId, "Success", "My Balance page loaded successfully", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Withdraw button found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "My Balance page not loaded: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check My Balance Page Loaded");
        }
    }

    /**
     * Perform complete withdrawal flow with daily limit check
     * @param transferType - "deposit", "bank", or "upi"
     * @param amount - Amount to withdraw (optional, uses default if null)
     * @return WithdrawalResult - Result of the withdrawal operation
     */
    public WithdrawalResult performWithdrawalWithDailyLimitCheck(String transferType, String amount) {
        TestReporter.startStep(deviceId, "Perform Withdrawal with Daily Limit Check");
        try {
            TestReporter.logAction(deviceId, "Action", "Starting withdrawal process for transfer type: " + transferType, driver);
            
            // Step 1: Click withdraw button to go to withdrawal page
            WithdrawalPage withdrawalPage = clickWithdrawButton();
            
            // Step 2: Enter withdrawal amount
            withdrawalPage.enterWithdrawAmount(amount);
            
            // Step 3: Select transfer option
            withdrawalPage.selectTransferOption(transferType);
            
            // Step 4: Click final withdraw button and get result page
            WithdrawalResultPage resultPage = withdrawalPage.clickFinalWithdrawButton();
            
            // Step 5: Check all possible result states on the result page
            // Check for denial first, since if denied, success message won't be present
            if (resultPage.isWithdrawalDenied()) {
                // Error message is already captured and logged in WithdrawalResultPage
                return new WithdrawalResult(false, "denied", "Withdrawal was denied");
            } else if (resultPage.isWithdrawalSuccessful()) {
                TestReporter.logAction(deviceId, "Success", "Withdrawal completed successfully", driver);
                return new WithdrawalResult(true, "success", "Withdrawal completed successfully");
            } else if (resultPage.isWithdrawalInProgress()) {
                TestReporter.logAction(deviceId, "Info", "Withdrawal is in progress", driver);
                return new WithdrawalResult(true, "in_progress", "Withdrawal is in progress");
            } else if (resultPage.isDailyWithdrawalLimitReached()) {
                String limitMessage = resultPage.captureDailyLimitMessage();
                TestReporter.logAction(deviceId, "Info", "Daily withdrawal limit reached: " + limitMessage, driver);
                resultPage.handleDailyLimitPopup();
                return new WithdrawalResult(false, "limit_reached", limitMessage);
            } else {
                TestReporter.logAction(deviceId, "Error", "Withdrawal failed - unknown reason", driver);
                return new WithdrawalResult(false, "failed", "Withdrawal failed - unknown reason");
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to perform withdrawal", e);
            return new WithdrawalResult(false, "error", "Withdrawal failed: " + e.getMessage());
        } finally {
            TestReporter.endStep(deviceId, "Perform Withdrawal with Daily Limit Check");
        }
    }

    /**
     * Perform complete withdrawal flow with daily limit check (using default amount)
     * @param transferType - "deposit", "bank", or "upi"
     * @return WithdrawalResult - Result of the withdrawal operation
     */
    public WithdrawalResult performWithdrawalWithDailyLimitCheck(String transferType) {
        return performWithdrawalWithDailyLimitCheck(transferType, null);
    }

    /**
     * Click deposit button and navigate to deposit page
     * @return AddCashPage - The add cash page for entering amount and selecting payment options
     */
    public AddCashPage clickDepositButton() {
        TestReporter.startStep(deviceId, "Click Deposit Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for deposit button to be clickable", driver);
            WebElement depositButton = wait.until(ExpectedConditions.elementToBeClickable(DEPOSIT_BUTTON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking deposit button", driver);
            depositButton.click();
            
            TestReporter.logAction(deviceId, "Success", "Deposit button clicked successfully", driver);
            
            TestReporter.logPageTransition(deviceId, "MyBalancePage", "AddCashPage");
            return new AddCashPage(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click deposit button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Deposit Button");
        }
    }

    /**
     * Perform complete deposit flow with daily limit check
     * @param amount - Amount to deposit
     * @return DepositResult - Result of the deposit operation
     */
    public DepositResult performDeposit(String amount) {
        TestReporter.startStep(deviceId, "Perform Deposit");
        try {
            TestReporter.logAction(deviceId, "Action", "Starting deposit process for amount: " + amount, driver);
            
            // Step 1: Click deposit button to go to add cash page
            AddCashPage addCashPage = clickDepositButton();
            
            // Step 2: Enter deposit amount
            addCashPage.enterAmount(amount);
            
            // Step 3: Click proceed to pay button to go to payment method page
            PaymentMethodPage paymentMethodPage = addCashPage.clickProceedToPayButton();
            
            // Step 4: Select payment method (default to UPI)
            paymentMethodPage.selectPaymentMethod("upi");
            
            // Step 5: Click proceed button and get result page
            AddCashResultPage resultPage = paymentMethodPage.clickProceedButton();
            
            // Step 6: Check all possible result states on the result page
            if (resultPage.isAddCashDenied()) {
                return new DepositResult(false, "denied", "Add cash was denied");
            } else if (resultPage.isAddCashSuccessful()) {
                TestReporter.logAction(deviceId, "Success", "Add cash completed successfully", driver);
                return new DepositResult(true, "success", "Add cash completed successfully");
            } else if (resultPage.isAddCashInProgress()) {
                TestReporter.logAction(deviceId, "Info", "Add cash is in progress", driver);
                return new DepositResult(true, "in_progress", "Add cash is in progress");
            } else if (resultPage.isDailyAddCashLimitReached()) {
                String limitMessage = resultPage.captureDailyLimitMessage();
                TestReporter.logAction(deviceId, "Info", "Daily add cash limit reached: " + limitMessage, driver);
                resultPage.handleDailyLimitPopup();
                return new DepositResult(false, "limit_reached", limitMessage);
            } else {
                TestReporter.logAction(deviceId, "Error", "Add cash failed - unknown reason", driver);
                return new DepositResult(false, "failed", "Add cash failed - unknown reason");
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to perform deposit", e);
            return new DepositResult(false, "error", "Add cash failed: " + e.getMessage());
        } finally {
            TestReporter.endStep(deviceId, "Perform Deposit");
        }
    }

    /**
     * Result class for withdrawal operations
     */
    public static class WithdrawalResult {
        private final boolean success;
        private final String status;
        private final String message;

        public WithdrawalResult(boolean success, String status, String message) {
            this.success = success;
            this.status = status;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return "WithdrawalResult{" +
                    "success=" + success +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    /**
     * Result class for deposit operations
     */
    public static class DepositResult {
        private final boolean success;
        private final String status;
        private final String message;

        public DepositResult(boolean success, String status, String message) {
            this.success = success;
            this.status = status;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return "DepositResult{" +
                    "success=" + success +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
} 