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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WithdrawalResultPage {
    private AndroidDriver driver;
    private WebDriverWait wait;
    private String deviceId;
    
    // Result page XPaths
    private static final By SUCCESS_MESSAGE = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/tv_payment_success']");
    private static final By BACK_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/action_back']");
    
    // Withdraw denied XPaths
    private static final By WITHDRAW_DENIED_TITLE = By.xpath("//android.widget.TextView[@resource-id=\"com.paytm.paytmplay:id/withdraw_error_title\"]");
    private static final By WITHDRAW_DENIED_MESSAGE = By.xpath("//android.widget.TextView[@resource-id=\"com.paytm.paytmplay:id/withdraw_error_msg\"]");
    
    // Daily withdrawal limit info message XPaths
    private static final By DAILY_LIMIT_INFO_MESSAGE = By.xpath("//android.widget.TextView[contains(@text, 'daily') or contains(@text, 'limit') or contains(@text, 'already') or contains(@text, 'withdrawn')]");
    private static final By INFO_MESSAGE_CONTAINER = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/tv_message' or @resource-id='com.paytm.paytmplay:id/message_text' or @resource-id='com.paytm.paytmplay:id/info_text']");
    private static final By INFO_POPUP_OK_BUTTON = By.xpath("//android.widget.Button[@resource-id='com.paytm.paytmplay:id/btn_ok' or @resource-id='com.paytm.paytmplay:id/ok_button' or contains(@text, 'OK') or contains(@text, 'Ok')]");
    private static final By INFO_POPUP_CLOSE_BUTTON = By.xpath("//android.widget.ImageView[@resource-id='com.paytm.paytmplay:id/close_icon' or @resource-id='com.paytm.paytmplay:id/back_icon']");

    public WithdrawalResultPage(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "WithdrawalPage", "WithdrawalResultPage");
        TestReporter.logAction(deviceId, "Initialization", "WithdrawalResultPage initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);
        
        // Take screenshot of initial result page state
        TestReporter.logAction(deviceId, "Info", "Taking screenshot of initial withdrawal result page", driver);
        takeScreenshot("Initial Result Page");
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
            String filename = String.format("withdrawal_result_%s_%s.png", 
                description.toLowerCase().replace(" ", "_"), 
                timestamp);
            
            // Create screenshots directory if it doesn't exist
            File screenshotsDir = new File("test-output/screenshots/withdrawal");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }
            
            // Take screenshot
            File screenshotFile = new File(screenshotsDir, filename);
            driver.getScreenshotAs(org.openqa.selenium.OutputType.FILE).renameTo(screenshotFile);
            
            TestReporter.logAction(deviceId, "Screenshot", "📸 Result page screenshot captured: " + filename + " - " + description, driver);
            
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Warning", "Failed to take result page screenshot: " + e.getMessage(), driver);
        }
    }

    /**
     * Check if withdrawal was successful with extended wait for processing
     */
    public boolean isWithdrawalSuccessful() {
        TestReporter.startStep(deviceId, "Check Withdrawal Success");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for success message (allowing time for UPI/bank processing)", driver);
            
            // Wait for processing to complete and success message to appear
            WebElement successElement = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
            
            if (successElement.isDisplayed()) {
                String successText = successElement.getText();
                TestReporter.logAction(deviceId, "Success", "Withdrawal successful. Message: " + successText, driver);
                
                // Take screenshot of successful withdrawal result
                TestReporter.logAction(deviceId, "Info", "Taking screenshot of successful withdrawal result", driver);
                takeScreenshot("Successful Withdrawal");
                
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Success element found but not visible", driver);
                
                // Take screenshot of failed withdrawal result
                TestReporter.logAction(deviceId, "Info", "Taking screenshot of failed withdrawal result", driver);
                takeScreenshot("Failed Withdrawal");
                
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Withdrawal not successful after processing time: " + e.getMessage(), driver);
            
            // Take screenshot of error state
            TestReporter.logAction(deviceId, "Info", "Taking screenshot of withdrawal error state", driver);
            takeScreenshot("Withdrawal Error");
            
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Withdrawal Success");
        }
    }

    /**
     * Check if withdrawal was denied
     */
    public boolean isWithdrawalDenied() {
        TestReporter.startStep(deviceId, "Check Withdrawal Denied");
        try {
            TestReporter.logAction(deviceId, "Wait", "Checking for withdrawal denied message", driver);
            
            // Check for withdraw denied title
            WebElement deniedTitleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(WITHDRAW_DENIED_TITLE));
            
            if (deniedTitleElement.isDisplayed()) {
                String deniedTitle = deniedTitleElement.getText();
                TestReporter.logAction(deviceId, "Info", "Withdrawal denied title found: " + deniedTitle, driver);
                
                // Capture and log the error message
                String errorMessage = captureWithdrawalDeniedMessage();
                
                // Take screenshot of denied withdrawal result
                TestReporter.logAction(deviceId, "Info", "Taking screenshot of denied withdrawal result", driver);
                takeScreenshot("Denied Withdrawal");
                
                return true;
            } else {
                TestReporter.logAction(deviceId, "Info", "Withdrawal denied title not found", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Info", "Withdrawal denied check completed - no denied message found: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Withdrawal Denied");
        }
    }

    /**
     * Capture withdrawal denied error message
     */
    public String captureWithdrawalDeniedMessage() {
        TestReporter.startStep(deviceId, "Capture Withdrawal Denied Message");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for withdrawal denied error message", driver);
            
            // Wait for error message to be visible
            WebElement errorMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(WITHDRAW_DENIED_MESSAGE));
            
            if (errorMessageElement.isDisplayed()) {
                String errorMessage = errorMessageElement.getText();
                
                // Log to test reporter only (removed duplicate console logging)
                TestReporter.logAction(deviceId, "Error", "❌ WITHDRAWAL DENIED - Error Message: " + errorMessage, driver);
                
                return errorMessage;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Error message element found but not visible", driver);
                return "Error message not visible";
            }
        } catch (Exception e) {
            String errorMsg = "Could not capture error message: " + e.getMessage();
            TestReporter.logAction(deviceId, "Error", "❌ WITHDRAWAL DENIED - " + errorMsg, driver);
            return errorMsg;
        } finally {
            TestReporter.endStep(deviceId, "Capture Withdrawal Denied Message");
        }
    }

    /**
     * Check if withdrawal is in progress
     */
    public boolean isWithdrawalInProgress() {
        TestReporter.startStep(deviceId, "Check Withdrawal In Progress");
        try {
            TestReporter.logAction(deviceId, "Wait", "Checking for withdrawal in progress indicators", driver);
            
            // Check for common "in progress" or "processing" indicators
            String[] progressIndicators = {
                "//android.widget.TextView[contains(@text, 'processing') or contains(@text, 'Processing')]",
                "//android.widget.TextView[contains(@text, 'in progress') or contains(@text, 'In Progress')]",
                "//android.widget.TextView[contains(@text, 'pending') or contains(@text, 'Pending')]",
                "//android.widget.TextView[contains(@text, 'wait') or contains(@text, 'Wait')]"
            };
            
            for (String indicator : progressIndicators) {
                try {
                    WebElement progressElement = driver.findElement(By.xpath(indicator));
                    if (progressElement.isDisplayed()) {
                        String progressText = progressElement.getText();
                        TestReporter.logAction(deviceId, "Info", "Withdrawal in progress indicator found: " + progressText, driver);
                        
                        // Take screenshot of in progress state
                        TestReporter.logAction(deviceId, "Info", "Taking screenshot of withdrawal in progress", driver);
                        takeScreenshot("Withdrawal In Progress");
                        
                        return true;
                    }
                } catch (Exception e) {
                    // Continue to next indicator
                }
            }
            
            TestReporter.logAction(deviceId, "Info", "No withdrawal in progress indicators found", driver);
            return false;
            
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Info", "Withdrawal in progress check completed: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Withdrawal In Progress");
        }
    }

    /**
     * Validate withdrawal result page
     */
    public boolean validateWithdrawalResult(String expectedAmount) {
        TestReporter.startStep(deviceId, "Validate Withdrawal Result");
        try {
            TestReporter.logAction(deviceId, "Validate", "Validating withdrawal result for amount: " + expectedAmount, driver);
            
            // Check if success message is present
            boolean successMessageValid = validateSuccessMessage();
            if (!successMessageValid) {
                TestReporter.logAction(deviceId, "Error", "Success message validation failed", driver);
                return false;
            }
            
            // Check if back button is present
            boolean backButtonValid = validateBackButton();
            if (!backButtonValid) {
                TestReporter.logAction(deviceId, "Error", "Back button validation failed", driver);
                return false;
            }
            
            TestReporter.logAction(deviceId, "Success", "Withdrawal result validation passed", driver);
            return true;
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to validate withdrawal result", e);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Validate Withdrawal Result");
        }
    }

    /**
     * Validate success message
     */
    private boolean validateSuccessMessage() {
        TestReporter.startStep(deviceId, "Validate Success Message");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for success message", driver);
            WebElement successElement = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
            
            if (successElement.isDisplayed()) {
                String successText = successElement.getText();
                TestReporter.logAction(deviceId, "Success", "Success message found: " + successText, driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Success element found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Success message not found: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Validate Success Message");
        }
    }

    /**
     * Validate back button
     */
    private boolean validateBackButton() {
        TestReporter.startStep(deviceId, "Validate Back Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for back button", driver);
            WebElement backElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BACK_BUTTON));
            
            if (backElement.isDisplayed() && backElement.isEnabled()) {
                TestReporter.logAction(deviceId, "Success", "Back button found and enabled", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Back button found but not enabled", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Back button not found: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Validate Back Button");
        }
    }

    /**
     * Click back button to return to previous page
     */
    public void clickBackButton() {
        TestReporter.startStep(deviceId, "Click Back Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for back button to be clickable", driver);
            WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(BACK_BUTTON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking back button", driver);
            backButton.click();
            
            TestReporter.logAction(deviceId, "Success", "Back button clicked successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click back button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Back Button");
        }
    }

    /**
     * Check if daily withdrawal limit is reached with extended wait for processing
     */
    public boolean isDailyWithdrawalLimitReached() {
        TestReporter.startStep(deviceId, "Check Daily Withdrawal Limit");
        try {
            TestReporter.logAction(deviceId, "Check", "Checking for daily limit message (allowing time for processing)", driver);
            
            // Wait longer for any popup to appear after processing
            Thread.sleep(5000);
            
            // Check for daily limit message
            try {
                WebElement limitMessage = driver.findElement(DAILY_LIMIT_INFO_MESSAGE);
                if (limitMessage.isDisplayed()) {
                    String message = limitMessage.getText();
                    TestReporter.logAction(deviceId, "Success", "Daily limit message found: " + message, driver);
                    
                    // Take screenshot of daily limit reached
                    TestReporter.logAction(deviceId, "Info", "Taking screenshot of daily limit reached", driver);
                    takeScreenshot("Daily Limit Reached");
                    
                    return true;
                }
            } catch (Exception e) {
                // Daily limit message not found, continue checking
            }
            
            // Check for info message container
            try {
                WebElement infoContainer = driver.findElement(INFO_MESSAGE_CONTAINER);
                if (infoContainer.isDisplayed()) {
                    String message = infoContainer.getText();
                    if (message.toLowerCase().contains("daily") || message.toLowerCase().contains("limit") || 
                        message.toLowerCase().contains("already") || message.toLowerCase().contains("withdrawn")) {
                        TestReporter.logAction(deviceId, "Success", "Daily limit message found in info container: " + message, driver);
                        
                        // Take screenshot of daily limit reached
                        TestReporter.logAction(deviceId, "Info", "Taking screenshot of daily limit reached", driver);
                        takeScreenshot("Daily Limit Reached");
                        
                        return true;
                    }
                }
            } catch (Exception e) {
                // Info container not found, continue checking
            }
            
            TestReporter.logAction(deviceId, "Info", "No daily limit message found", driver);
            
            // Take screenshot of no limit message state
            TestReporter.logAction(deviceId, "Info", "Taking screenshot of no limit message state", driver);
            takeScreenshot("No Limit Message");
            
            return false;
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error checking daily withdrawal limit", e);
            
            // Take screenshot of error state
            TestReporter.logAction(deviceId, "Info", "Taking screenshot of limit check error state", driver);
            takeScreenshot("Limit Check Error");
            
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Daily Withdrawal Limit");
        }
    }

    /**
     * Capture daily limit message
     */
    public String captureDailyLimitMessage() {
        TestReporter.startStep(deviceId, "Capture Daily Limit Message");
        try {
            TestReporter.logAction(deviceId, "Capture", "Capturing daily limit message", driver);
            
            // Wait a moment for any popup to appear
            Thread.sleep(2000);
            
            // Try to find daily limit message
            try {
                WebElement limitMessage = driver.findElement(DAILY_LIMIT_INFO_MESSAGE);
                if (limitMessage.isDisplayed()) {
                    String message = limitMessage.getText();
                    TestReporter.logAction(deviceId, "Success", "Daily limit message captured: " + message, driver);
                    return message;
                }
            } catch (Exception e) {
                // Daily limit message not found
            }
            
            // Try to find info message container
            try {
                WebElement infoContainer = driver.findElement(INFO_MESSAGE_CONTAINER);
                if (infoContainer.isDisplayed()) {
                    String message = infoContainer.getText();
                    if (message.toLowerCase().contains("daily") || message.toLowerCase().contains("limit")) {
                        TestReporter.logAction(deviceId, "Success", "Daily limit message captured from info container: " + message, driver);
                        return message;
                    }
                }
            } catch (Exception e) {
                // Info container not found
            }
            
            TestReporter.logAction(deviceId, "Warning", "No daily limit message found", driver);
            return "No daily limit message found";
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to capture daily limit message", e);
            return "Error capturing daily limit message: " + e.getMessage();
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
            TestReporter.logAction(deviceId, "Handle", "Handling daily limit popup", driver);
            
            // Wait for popup to appear
            Thread.sleep(2000);
            
            // Take screenshot of daily limit popup
            TestReporter.logAction(deviceId, "Info", "Taking screenshot of daily limit popup", driver);
            takeScreenshot("Daily Limit Popup");
            
            // Try to click OK button
            try {
                WebElement okButton = driver.findElement(INFO_POPUP_OK_BUTTON);
                if (okButton.isDisplayed() && okButton.isEnabled()) {
                    TestReporter.logAction(deviceId, "Click", "Clicking OK button on daily limit popup", driver);
                    okButton.click();
                    TestReporter.logAction(deviceId, "Success", "OK button clicked successfully", driver);
                    
                    // Take screenshot after handling popup
                    TestReporter.logAction(deviceId, "Info", "Taking screenshot after handling daily limit popup", driver);
                    takeScreenshot("After Daily Limit Popup Handled");
                    
                    return;
                }
            } catch (Exception e) {
                // OK button not found
            }
            
            // Try to click close button
            try {
                WebElement closeButton = driver.findElement(INFO_POPUP_CLOSE_BUTTON);
                if (closeButton.isDisplayed() && closeButton.isEnabled()) {
                    TestReporter.logAction(deviceId, "Click", "Clicking close button on daily limit popup", driver);
                    closeButton.click();
                    TestReporter.logAction(deviceId, "Success", "Close button clicked successfully", driver);
                    
                    // Take screenshot after handling popup
                    TestReporter.logAction(deviceId, "Info", "Taking screenshot after handling daily limit popup", driver);
                    takeScreenshot("After Daily Limit Popup Handled");
                    
                    return;
                }
            } catch (Exception e) {
                // Close button not found
            }
            
            // Try to press back key
            try {
                driver.navigate().back();
                TestReporter.logAction(deviceId, "Success", "Back navigation used to close popup", driver);
                
                // Take screenshot after handling popup
                TestReporter.logAction(deviceId, "Info", "Taking screenshot after handling daily limit popup", driver);
                takeScreenshot("After Daily Limit Popup Handled");
                
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Warning", "Could not close popup with any method", driver);
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to handle daily limit popup", e);
        } finally {
            TestReporter.endStep(deviceId, "Handle Daily Limit Popup");
        }
    }

    /**
     * Check if result page is loaded
     */
    public boolean isResultPageLoaded() {
        TestReporter.startStep(deviceId, "Check Result Page Loaded");
        try {
            TestReporter.logAction(deviceId, "Check", "Checking if result page is loaded", driver);
            
            // Check for either success message or daily limit message
            boolean successMessagePresent = false;
            boolean dailyLimitMessagePresent = false;
            
            try {
                WebElement successElement = driver.findElement(SUCCESS_MESSAGE);
                successMessagePresent = successElement.isDisplayed();
            } catch (Exception e) {
                // Success message not found
            }
            
            try {
                WebElement limitElement = driver.findElement(DAILY_LIMIT_INFO_MESSAGE);
                dailyLimitMessagePresent = limitElement.isDisplayed();
            } catch (Exception e) {
                // Daily limit message not found
            }
            
            if (successMessagePresent || dailyLimitMessagePresent) {
                TestReporter.logAction(deviceId, "Success", "Result page loaded successfully", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Result page not loaded - no success or limit message found", driver);
                return false;
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to check if result page is loaded", e);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Result Page Loaded");
        }
    }
} 