package org.fg.pageObjects.android;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import com.google.common.collect.ImmutableMap;
import org.fg.utils.TestReporter;
import java.time.Instant;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WithdrawalPage {
    private AndroidDriver driver;
    private WebDriverWait wait;
    private String deviceId;
    
    // Dynamic withdrawal amount - can be easily modified
    private static final String DEFAULT_WITHDRAW_AMOUNT = "100";
    
    // Amount input field
    private static final By AMOUNT_INPUT_FIELD = By.xpath("//android.widget.EditText[@resource-id='com.paytm.paytmplay:id/cash_edit']");
    
    // Transfer option XPaths
    private static final By TRANSFER_TO_DEPOSIT_RADIO = By.xpath("//android.widget.RadioButton[@resource-id='com.paytm.paytmplay:id/rb_check']");
    private static final By TRANSFER_TO_BANK_RADIO = By.xpath("//android.widget.RadioButton[@resource-id='com.paytm.paytmplay:id/bank_account']");
    private static final By TRANSFER_TO_UPI_RADIO = By.xpath("//android.widget.RadioButton[@resource-id='com.paytm.paytmplay:id/upi_account']");
    
    // Final withdraw button XPaths
    private static final By TRANSFER_TO_DEPOSIT_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/withdraw_action_button_text']");
    private static final By WITHDRAW_NOW_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/withdraw_action_button_text']");
    
    // Confirmation popup XPaths
    private static final By CONFIRM_BUTTON = By.xpath("//android.widget.Button[@resource-id='com.paytm.paytmplay:id/btn_transfer']");
    private static final By CANCEL_BUTTON = By.xpath("//android.widget.ImageView[@resource-id='com.paytm.paytmplay:id/back_icon']");
    
    // Additional confirmation popup for bank/UPI transfers
    private static final By CONTINUE_TO_WITHDRAW_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/tv_action']");
    
    // Track selected transfer type
    private String selectedTransferType = null;

    public WithdrawalPage(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "MyBalance", "WithdrawalPage");
        TestReporter.logAction(deviceId, "Initialization", "WithdrawalPage initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);
        
        // Take screenshot of initial withdrawal page state
        TestReporter.logAction(deviceId, "Info", "Taking screenshot of initial withdrawal page", driver);
        takeScreenshot("Initial Withdrawal Page");
    }

    /**
     * Enter withdrawal amount with dynamic value and handle number pad overlay
     * @param amount - The amount to withdraw (defaults to DEFAULT_WITHDRAW_AMOUNT if null/empty)
     */
    public void enterWithdrawAmount(String amount) {
        TestReporter.startStep(deviceId, "Enter Withdrawal Amount");
        try {
            // Use default amount if none provided
            String withdrawAmount = (amount == null || amount.trim().isEmpty()) ? DEFAULT_WITHDRAW_AMOUNT : amount;
            
            TestReporter.logAction(deviceId, "Info", "Entering withdrawal amount: ₹" + withdrawAmount, driver);
            
            TestReporter.logAction(deviceId, "Wait", "Waiting for amount field to be clickable", driver);
            WebElement amountField = wait.until(ExpectedConditions.elementToBeClickable(AMOUNT_INPUT_FIELD));
            
            TestReporter.logAction(deviceId, "Clear", "Clearing amount field", driver);
            amountField.clear();
            
            TestReporter.logAction(deviceId, "Input", "Entering withdrawal amount: " + withdrawAmount, driver);
            amountField.sendKeys(withdrawAmount);
            
            // Handle keyboard
            handleNumberPadOverlay();
            
            // Take screenshot after amount entry
            takeScreenshot("Amount Entered");
            
            TestReporter.logAction(deviceId, "Success", "Amount entry process completed successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to enter withdrawal amount", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Enter Withdrawal Amount");
        }
    }

    /**
     * Handle number pad overlay that might hide UI elements
     */
    private void handleNumberPadOverlay() {
        TestReporter.startStep(deviceId, "Handle Number Pad Overlay");
        try {
            Thread.sleep(1000);
            
            // Strategy 1: Try to hide keyboard using driver
            try {
                driver.hideKeyboard();
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Warning", "Could not hide keyboard using driver method: " + e.getMessage(), driver);
            }
            
            // Strategy 2: Tap outside the input field to dismiss keyboard
            try {
                driver.executeScript("mobile: tapGesture", ImmutableMap.of(
                    "x", 500,
                    "y", 100
                ));
                Thread.sleep(500);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Warning", "Could not tap outside input field: " + e.getMessage(), driver);
            }
            
            // Strategy 3: Scroll to ensure transfer options are visible
            try {
                driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
                    "left", 100, "top", 100, "width", 600, "height", 800,
                    "direction", "down", "percent", 0.5
                ));
                Thread.sleep(500);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Warning", "Could not scroll: " + e.getMessage(), driver);
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error handling number pad overlay", e);
        } finally {
            TestReporter.endStep(deviceId, "Handle Number Pad Overlay");
        }
    }

    /**
     * Select transfer option by type
     * @param transferType - "deposit", "bank", or "upi"
     */
    public void selectTransferOption(String transferType) {
        TestReporter.startStep(deviceId, "Select Transfer Option: " + transferType);
        try {
            TestReporter.logAction(deviceId, "Info", "Selecting transfer type: " + transferType.toUpperCase(), driver);
            
            switch (transferType.toLowerCase()) {
                case "deposit":
                    handleDepositTransfer();
                    break;
                case "bank":
                    handleBankTransfer();
                    break;
                case "upi":
                    handleUPITransfer();
                    break;
                default:
                    TestReporter.logAction(deviceId, "Error", "Invalid transfer type: " + transferType, driver);
                    throw new IllegalArgumentException("Invalid transfer type: " + transferType + ". Use 'deposit', 'bank', or 'upi'");
            }
            
            TestReporter.logAction(deviceId, "Success", "Transfer option selected successfully: " + transferType.toUpperCase(), driver);
            
            // Take screenshot after transfer method selection
            takeScreenshot("Transfer Method Selected");
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to select transfer option", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Select Transfer Option: " + transferType);
        }
    }

    /**
     * Handle deposit transfer flow
     */
    private void handleDepositTransfer() {
        // Wait for page to fully load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Scroll to ensure transfer options are visible
        try {
            driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
                "left", 100, "top", 100, "width", 600, "height", 800,
                "direction", "down", "percent", 0.3
            ));
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Warning", "Could not scroll: " + e.getMessage(), driver);
        }
        
        // Try multiple approaches to find and click the deposit radio button
        boolean depositSelected = false;
        
        // Approach 1: Direct click with wait
        try {
            WebElement depositRadio = wait.until(ExpectedConditions.elementToBeClickable(TRANSFER_TO_DEPOSIT_RADIO));
            depositRadio.click();
            depositSelected = true;
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Warning", "Approach 1 failed: " + e.getMessage(), driver);
        }
        
        // Approach 2: JavaScript click if direct click failed
        if (!depositSelected) {
            try {
                WebElement depositRadio = driver.findElement(TRANSFER_TO_DEPOSIT_RADIO);
                driver.executeScript("arguments[0].click();", depositRadio);
                depositSelected = true;
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Warning", "Approach 2 failed: " + e.getMessage(), driver);
            }
        }
        
        // Approach 3: Tap gesture if other approaches failed
        if (!depositSelected) {
            try {
                driver.executeScript("mobile: tapGesture", ImmutableMap.of(
                    "x", 300,
                    "y", 600
                ));
                depositSelected = true;
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Warning", "Approach 3 failed: " + e.getMessage(), driver);
            }
        }
        
        if (!depositSelected) {
            throw new RuntimeException("Could not select deposit transfer option with any approach");
        }
        
        selectedTransferType = "deposit";
        
        // Wait for selection to register
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Handle bank transfer flow
     */
    private void handleBankTransfer() {
        TestReporter.logAction(deviceId, "Action", "Starting bank transfer selection", driver);
        
        // Wait for page to load
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        
        // Select bank transfer option
        WebElement bankRadio = wait.until(ExpectedConditions.elementToBeClickable(TRANSFER_TO_BANK_RADIO));
        bankRadio.click();
        
        selectedTransferType = "bank";
        
        // Wait for selection to register
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Handle UPI transfer flow
     */
    private void handleUPITransfer() {
        TestReporter.logAction(deviceId, "Action", "Starting UPI transfer selection", driver);
        
        // Wait for page to load
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        
        // Select UPI transfer option
        WebElement upiRadio = wait.until(ExpectedConditions.elementToBeClickable(TRANSFER_TO_UPI_RADIO));
        upiRadio.click();
        
        selectedTransferType = "upi";
        
        // Wait for selection to register
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Click the final withdraw button and handle confirmation popups
     * @return WithdrawalResultPage - The result page after withdrawal
     */
    public WithdrawalResultPage clickFinalWithdrawButton() {
        TestReporter.startStep(deviceId, "Click Final Withdraw Button");
        try {
            TestReporter.logAction(deviceId, "Info", "Starting final withdrawal process for " + selectedTransferType.toUpperCase(), driver);
            
            // Wait for the appropriate button to be clickable
            TestReporter.logAction(deviceId, "Wait", "Waiting for withdraw button to be clickable", driver);
            WebElement withdrawButton = wait.until(ExpectedConditions.elementToBeClickable(TRANSFER_TO_DEPOSIT_BUTTON));
            
            // Take screenshot before final withdrawal
            takeScreenshot("Before Final Withdrawal");
            
            // Click the withdraw button
            TestReporter.logAction(deviceId, "Click", "Clicking final withdraw button", driver);
            withdrawButton.click();
            
            // Take screenshot after withdraw button click
            takeScreenshot("After Withdraw Button Click");
            
            // Try to handle confirmation popup, but don't fail if it doesn't appear
            boolean popupHandled = false;
            
            try {
                if ("deposit".equals(selectedTransferType)) {
                    handleDepositConfirmationPopup();
                    popupHandled = true;
                } else {
                    handleBankUPIConfirmationPopup();
                    popupHandled = true;
                }
            } catch (Exception popupException) {
                TestReporter.logAction(deviceId, "Info", "No confirmation popup appeared - withdrawal might have been processed immediately", driver);
                popupHandled = false;
            }
            
            if (popupHandled) {
                TestReporter.logAction(deviceId, "Success", "Confirmation popup handled successfully", driver);
            } else {
                TestReporter.logAction(deviceId, "Info", "Proceeding without confirmation popup - checking for result page", driver);
            }
            
            TestReporter.logPageTransition(deviceId, "WithdrawalPage", "WithdrawalResultPage");
            return new WithdrawalResultPage(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click final withdraw button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Final Withdraw Button");
        }
    }

    /**
     * Handle deposit confirmation popup
     */
    private void handleDepositConfirmationPopup() {
        TestReporter.startStep(deviceId, "Handle Deposit Confirmation Popup");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for deposit confirmation popup", driver);
            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_BUTTON));
            
            // Take screenshot of deposit confirmation popup
            takeScreenshot("Deposit Confirmation Popup");
            
            TestReporter.logAction(deviceId, "Click", "Clicking confirm button", driver);
            confirmButton.click();
            
            // Take screenshot after deposit confirmation
            takeScreenshot("After Deposit Confirmation");
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to handle deposit confirmation popup", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Handle Deposit Confirmation Popup");
        }
    }

    /**
     * Handle bank/UPI confirmation popup
     */
    private void handleBankUPIConfirmationPopup() {
        TestReporter.startStep(deviceId, "Handle Bank/UPI Confirmation Popup");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for bank/UPI confirmation popup", driver);
            
            // First check if we're already on the result page (indicating withdrawal was denied)
            try {
                // Check for result page elements that would indicate we're already on the result page
                WebElement resultPageElement = driver.findElement(By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/withdraw_error_title' or @resource-id='com.paytm.paytmplay:id/tv_payment_success']"));
                if (resultPageElement.isDisplayed()) {
                    TestReporter.logAction(deviceId, "Info", "Already on result page - withdrawal was processed immediately (likely denied)", driver);
                    return; // Exit early, no need to look for confirmation popup
                }
            } catch (Exception e) {
                // Result page elements not found, continue with popup check
            }
            
            // Wait for confirmation popup button
            WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_TO_WITHDRAW_BUTTON));
            
            // Take screenshot of bank/UPI confirmation popup
            takeScreenshot("Bank UPI Confirmation Popup");
            
            TestReporter.logAction(deviceId, "Click", "Clicking continue to withdraw button", driver);
            continueButton.click();
            
            // Take screenshot after bank/UPI confirmation
            takeScreenshot("After Bank UPI Confirmation");
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to handle bank/UPI confirmation popup", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Handle Bank/UPI Confirmation Popup");
        }
    }

    /**
     * Get default withdrawal amount
     */
    public static String getDefaultWithdrawAmount() {
        return DEFAULT_WITHDRAW_AMOUNT;
    }

    /**
     * Check if withdrawal page is loaded
     */
    public boolean isWithdrawalPageLoaded() {
        TestReporter.startStep(deviceId, "Check Withdrawal Page Loaded");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for amount input field to be visible", driver);
            WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT_FIELD));
            
            if (amountField.isDisplayed()) {
                TestReporter.logAction(deviceId, "Success", "Withdrawal page loaded successfully", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Amount field found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Withdrawal page not loaded: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Withdrawal Page Loaded");
        }
    }

    /**
     * Take a screenshot with descriptive name
     * @param description - Description of what the screenshot captures
     */
    private void takeScreenshot(String description) {
        try {
            // Create timestamp for unique filename
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            
            // Create descriptive filename
            String filename = String.format("withdrawal_%s_%s_%s.png", 
                description.toLowerCase().replace(" ", "_"), 
                selectedTransferType != null ? selectedTransferType : "unknown",
                timestamp);
            
            // Create screenshots directory if it doesn't exist
            File screenshotsDir = new File("test-output/screenshots/withdrawal");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }
            
            // Take screenshot
            File screenshotFile = new File(screenshotsDir, filename);
            driver.getScreenshotAs(org.openqa.selenium.OutputType.FILE).renameTo(screenshotFile);
            
            TestReporter.logAction(deviceId, "Screenshot", "Screenshot captured: " + filename + " - " + description, driver);
            
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Warning", "Failed to take screenshot: " + e.getMessage(), driver);
        }
    }
} 