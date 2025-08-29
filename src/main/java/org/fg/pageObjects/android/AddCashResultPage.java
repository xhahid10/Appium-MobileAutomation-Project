package org.fg.pageObjects.android;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.fg.utils.TestReporter;
import java.time.Instant;

public class AddCashResultPage {
    private AndroidDriver driver;
    private WebDriverWait wait;
    private String deviceId;

    // Locators for Add Cash Result page
    private static final By SUCCESS_MESSAGE = By.xpath("//android.widget.TextView[contains(@text, 'Success') or contains(@text, 'successful')]");
    private static final By ERROR_MESSAGE = By.xpath("//android.widget.TextView[contains(@text, 'Failed') or contains(@text, 'Error') or contains(@text, 'Denied')]");
    private static final By IN_PROGRESS_MESSAGE = By.xpath("//android.widget.TextView[contains(@text, 'Processing') or contains(@text, 'In Progress')]");
    private static final By DAILY_LIMIT_MESSAGE = By.xpath("//android.widget.TextView[contains(@text, 'limit') or contains(@text, 'Limit')]");
    private static final By OK_BUTTON = By.id("com.paytm.paytmplay:id/ok_button");
    private static final By CLOSE_BUTTON = By.id("com.paytm.paytmplay:id/close_button");
    private static final By BACK_BUTTON = By.id("com.paytm.paytmplay:id/back_button");

    public AddCashResultPage(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "PaymentMethodPage", "AddCashResultPage");
        TestReporter.logAction(deviceId, "Initialization", "AddCashResultPage initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);
    }

    /**
     * Check if add cash was successful
     */
    public boolean isAddCashSuccessful() {
        TestReporter.startStep(deviceId, "Check Add Cash Success");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for success message", driver);
            WebElement successElement = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
            
            if (successElement.isDisplayed()) {
                String successText = successElement.getText();
                TestReporter.logAction(deviceId, "Success", "Add cash successful: " + successText, driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Success message found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Info", "Success message not found: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Add Cash Success");
        }
    }

    /**
     * Check if add cash was denied
     */
    public boolean isAddCashDenied() {
        TestReporter.startStep(deviceId, "Check Add Cash Denied");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for error message", driver);
            WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
            
            if (errorElement.isDisplayed()) {
                String errorText = errorElement.getText();
                TestReporter.logAction(deviceId, "Warning", "Add cash denied: " + errorText, driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Info", "Error message found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Info", "Error message not found: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Add Cash Denied");
        }
    }

    /**
     * Check if add cash is in progress
     */
    public boolean isAddCashInProgress() {
        TestReporter.startStep(deviceId, "Check Add Cash In Progress");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for in progress message", driver);
            WebElement progressElement = wait.until(ExpectedConditions.visibilityOfElementLocated(IN_PROGRESS_MESSAGE));
            
            if (progressElement.isDisplayed()) {
                String progressText = progressElement.getText();
                TestReporter.logAction(deviceId, "Info", "Add cash in progress: " + progressText, driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Info", "In progress message found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Info", "In progress message not found: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Add Cash In Progress");
        }
    }

    /**
     * Check if daily add cash limit is reached
     */
    public boolean isDailyAddCashLimitReached() {
        TestReporter.startStep(deviceId, "Check Daily Add Cash Limit");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for daily limit message", driver);
            WebElement limitElement = wait.until(ExpectedConditions.visibilityOfElementLocated(DAILY_LIMIT_MESSAGE));
            
            if (limitElement.isDisplayed()) {
                String limitText = limitElement.getText();
                TestReporter.logAction(deviceId, "Info", "Daily add cash limit reached: " + limitText, driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Info", "Daily limit message found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Info", "Daily limit message not found: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Daily Add Cash Limit");
        }
    }

    /**
     * Capture daily limit message
     */
    public String captureDailyLimitMessage() {
        TestReporter.startStep(deviceId, "Capture Daily Limit Message");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for daily limit message", driver);
            WebElement limitElement = wait.until(ExpectedConditions.visibilityOfElementLocated(DAILY_LIMIT_MESSAGE));
            
            String limitText = limitElement.getText();
            TestReporter.logAction(deviceId, "Info", "Captured daily limit message: " + limitText, driver);
            return limitText;
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to capture daily limit message", e);
            return "Daily limit message not found";
        } finally {
            TestReporter.endStep(deviceId, "Capture Daily Limit Message");
        }
    }

    /**
     * Handle daily limit popup
     */
    public void handleDailyLimitPopup() {
        TestReporter.startStep(deviceId, "Handle Daily Limit Popup");
        try {
            TestReporter.logAction(deviceId, "Action", "Handling daily limit popup", driver);
            
            // Try to click OK button
            try {
                WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(OK_BUTTON));
                TestReporter.logAction(deviceId, "Click", "Clicking OK button", driver);
                okButton.click();
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Info", "OK button not found, trying close button", driver);
                
                // Try to click close button
                try {
                    WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BUTTON));
                    TestReporter.logAction(deviceId, "Click", "Clicking close button", driver);
                    closeButton.click();
                } catch (Exception e2) {
                    TestReporter.logAction(deviceId, "Info", "Close button not found, trying back button", driver);
                    
                    // Try to click back button
                    try {
                        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(BACK_BUTTON));
                        TestReporter.logAction(deviceId, "Click", "Clicking back button", driver);
                        backButton.click();
                    } catch (Exception e3) {
                        TestReporter.logAction(deviceId, "Warning", "No popup handling buttons found", driver);
                    }
                }
            }
            
            TestReporter.logAction(deviceId, "Success", "Daily limit popup handled successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to handle daily limit popup", e);
        } finally {
            TestReporter.endStep(deviceId, "Handle Daily Limit Popup");
        }
    }

    /**
     * Check if Add Cash Result page is loaded
     */
    public boolean isAddCashResultPageLoaded() {
        TestReporter.startStep(deviceId, "Check Add Cash Result Page Loaded");
        try {
            // Check for any of the result messages
            boolean hasSuccess = isAddCashSuccessful();
            boolean hasError = isAddCashDenied();
            boolean hasProgress = isAddCashInProgress();
            boolean hasLimit = isDailyAddCashLimitReached();
            
            if (hasSuccess || hasError || hasProgress || hasLimit) {
                TestReporter.logAction(deviceId, "Success", "Add Cash Result page loaded successfully", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "No result messages found", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Add Cash Result page not loaded: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Add Cash Result Page Loaded");
        }
    }
} 