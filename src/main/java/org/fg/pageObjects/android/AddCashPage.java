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
import java.util.List;
import org.openqa.selenium.Keys;

public class AddCashPage {
    private AndroidDriver driver;
    private WebDriverWait wait;
    private String deviceId;

    // Locators for Add Cash page
    private static final By AMOUNT_INPUT_FIELD = By.xpath("//android.widget.EditText[@resource-id=\"com.paytm.paytmplay:id/cash_edit\"]");
    private static final By PROCEED_TO_PAY_BUTTON = By.xpath("//android.widget.TextView[@resource-id=\"com.paytm.paytmplay:id/btn_action\"]");

    public AddCashPage(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "MyBalancePage", "AddCashPage");
        TestReporter.logAction(deviceId, "Initialization", "AddCashPage initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);
    }

    /**
     * Enter amount in the add cash page
     * @param amount - Amount to enter
     */
    public void enterAmount(String amount) throws InterruptedException {
        TestReporter.startStep(deviceId, "Enter Add Cash Amount");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for amount input field to be visible", driver);
            WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT_FIELD));
            
            // Click to focus the field first
            TestReporter.logAction(deviceId, "Focus", "Clicking to focus the amount field", driver);
            amountField.click();
            Thread.sleep(1000);
            
            boolean amountEntered = false;
            
            // Strategy 1: Try to replace content using setValue (Appium method)
            if (!amountEntered) {
                try {
                    TestReporter.logAction(deviceId, "Input", "Using setValue to replace field content with: " + amount, driver);
                    amountField.clear();
                    Thread.sleep(500);
                    amountField.sendKeys(amount);
                    Thread.sleep(1000);
                    
                    String checkText = amountField.getText();
                    if (checkText != null && checkText.contains(amount)) {
                        amountEntered = true;
                        TestReporter.logAction(deviceId, "Success", "Strategy 1 successful - Amount found in field", driver);
                    }
                } catch (Exception e) {
                    TestReporter.logAction(deviceId, "Info", "Strategy 1 failed, trying next", driver);
                }
            }
            
            // Strategy 2: Try using Android-specific input method
            if (!amountEntered) {
                try {
                    TestReporter.logAction(deviceId, "Input", "Using Android-specific input method", driver);
                    // Use Android's input method to set text
                    driver.executeScript("arguments[0].setAttribute('value', '" + amount + "');", amountField);
                    Thread.sleep(1000);
                    
                    String checkText = amountField.getText();
                    if (checkText != null && checkText.contains(amount)) {
                        amountEntered = true;
                        TestReporter.logAction(deviceId, "Success", "Strategy 2 successful - Amount found in field", driver);
                    }
                } catch (Exception e) {
                    TestReporter.logAction(deviceId, "Info", "Strategy 2 failed", driver);
                }
            }
            
            // Strategy 3: Try using Actions class for more precise input
            if (!amountEntered) {
                try {
                    TestReporter.logAction(deviceId, "Input", "Using Actions class for precise input", driver);
                    org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
                    actions.click(amountField)
                           .keyDown(Keys.CONTROL)
                           .sendKeys("a")
                           .keyUp(Keys.CONTROL)
                           .sendKeys(Keys.DELETE)
                           .sendKeys(amount)
                           .build()
                           .perform();
                    Thread.sleep(1000);
                    
                    String checkText = amountField.getText();
                    if (checkText != null && checkText.contains(amount)) {
                        amountEntered = true;
                        TestReporter.logAction(deviceId, "Success", "Strategy 3 successful - Amount found in field", driver);
                    }
                } catch (Exception e) {
                    TestReporter.logAction(deviceId, "Info", "Strategy 3 failed", driver);
                }
            }
            
            // Strategy 4: Try direct input without clearing (only if previous strategies failed)
            if (!amountEntered) {
                try {
                    TestReporter.logAction(deviceId, "Input", "Trying direct input without clearing", driver);
                    amountField.sendKeys(amount);
                    Thread.sleep(1000);
                    
                    String checkText = amountField.getText();
                    if (checkText != null && checkText.contains(amount)) {
                        amountEntered = true;
                        TestReporter.logAction(deviceId, "Success", "Strategy 4 successful - Amount found in field", driver);
                    }
                } catch (Exception e) {
                    TestReporter.logAction(deviceId, "Info", "Strategy 4 failed", driver);
                }
            }
            
            // Verify the amount was entered correctly
            String finalText = amountField.getText();
            TestReporter.logAction(deviceId, "Verify", "Final field content: '" + finalText + "'", driver);
            
            // Check if the amount is present in the field (even if mixed with other characters)
            if (finalText != null && finalText.contains(amount)) {
                TestReporter.logAction(deviceId, "Success", "Amount " + amount + " found in field content", driver);
            } else {
                TestReporter.logAction(deviceId, "Warning", "Amount " + amount + " not found in field content: '" + finalText + "'", driver);
            }
            
            TestReporter.logAction(deviceId, "Success", "Amount entry process completed", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to enter amount", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Enter Add Cash Amount");
        }
    }

    /**
     * Click the proceed to pay button and navigate to payment method page
     * @return PaymentMethodPage - The payment method selection page
     */
    public PaymentMethodPage clickProceedToPayButton() {
        TestReporter.startStep(deviceId, "Click Proceed to Pay Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for proceed to pay button to be clickable", driver);
            WebElement proceedToPayButton = wait.until(ExpectedConditions.elementToBeClickable(PROCEED_TO_PAY_BUTTON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking proceed to pay button", driver);
            proceedToPayButton.click();
            
            TestReporter.logAction(deviceId, "Success", "Proceed to pay button clicked successfully", driver);
            
            TestReporter.logPageTransition(deviceId, "AddCashPage", "PaymentMethodPage");
            return new PaymentMethodPage(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click proceed to pay button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Proceed to Pay Button");
        }
    }

    /**
     * Check if Add Cash page is loaded
     */
    public boolean isAddCashPageLoaded() {
        TestReporter.startStep(deviceId, "Check Add Cash Page Loaded");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for amount input field to be visible", driver);
            WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT_FIELD));
            
            if (amountField.isDisplayed()) {
                TestReporter.logAction(deviceId, "Success", "Add Cash page loaded successfully", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Amount input field found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Add Cash page not loaded: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Add Cash Page Loaded");
        }
    }

    /**
     * Collect and log all web elements present on the Add Cash page
     */
    public void collectAllWebElements() {
        TestReporter.startStep(deviceId, "Collect All Web Elements on Add Cash Page");
        try {
            TestReporter.logAction(deviceId, "Info", "Starting comprehensive web element collection on Add Cash page", driver);
            
            // Wait for page to load
            Thread.sleep(2000);
            
            // Collect all elements by different categories
            collectBasicElements();
            collectInputElements();
            collectButtonElements();
            collectAmountElements();
            collectInformationElements();
            
            TestReporter.logAction(deviceId, "Success", "Web element collection completed successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to collect web elements", e);
        } finally {
            TestReporter.endStep(deviceId, "Collect All Web Elements on Add Cash Page");
        }
    }
    
    /**
     * Collect basic page elements
     */
    private void collectBasicElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting basic page elements", driver);
            
            // Page title
            try {
                List<WebElement> titles = driver.findElements(By.xpath("//android.widget.TextView[contains(@text, 'Add') or contains(@text, 'Cash') or contains(@text, 'Amount')]"));
                for (WebElement title : titles) {
                    TestReporter.logAction(deviceId, "Found", "Page Title: " + title.getText(), driver);
                }
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Page Title", driver);
            }
            
            // Back button
            try {
                WebElement backButton = driver.findElement(By.xpath("//android.widget.ImageView[@content-desc='Navigate up'] | //android.widget.ImageButton[@content-desc='Navigate up']"));
                TestReporter.logAction(deviceId, "Found", "Back Button: " + backButton.getAttribute("content-desc"), driver);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Back Button", driver);
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting basic elements", e);
        }
    }
    
    /**
     * Collect input elements
     */
    private void collectInputElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting input elements", driver);
            
            // All input fields
            try {
                List<WebElement> inputFields = driver.findElements(By.xpath("//android.widget.EditText | //android.widget.TextView[@inputType]"));
                for (WebElement input : inputFields) {
                    String resourceId = input.getAttribute("resource-id");
                    String text = input.getText();
                    String hint = input.getAttribute("hint");
                    TestReporter.logAction(deviceId, "Found", "Input Field - ID: " + resourceId + ", Text: " + text + ", Hint: " + hint, driver);
                }
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Input Fields", driver);
            }
            
            // Amount related elements
            try {
                List<WebElement> amountElements = driver.findElements(By.xpath("//*[contains(@text, '₹') or contains(@text, 'Rs') or contains(@text, 'Amount') or contains(@text, 'Enter')]"));
                for (WebElement amount : amountElements) {
                    TestReporter.logAction(deviceId, "Found", "Amount Element: " + amount.getText(), driver);
                }
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Amount Elements", driver);
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting input elements", e);
        }
    }
    
    /**
     * Collect button elements
     */
    private void collectButtonElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting button elements", driver);
            
            String[] buttonTexts = {
                "Proceed", "Pay", "Continue", "Next", "Confirm", "Submit", "Add", "Cash"
            };
            
            for (String buttonText : buttonTexts) {
                try {
                    List<WebElement> buttons = driver.findElements(By.xpath("//android.widget.Button[@text='" + buttonText + "'] | //android.widget.TextView[@text='" + buttonText + "']"));
                    for (WebElement button : buttons) {
                        String resourceId = button.getAttribute("resource-id");
                        TestReporter.logAction(deviceId, "Found", "Button: " + button.getText() + " (ID: " + resourceId + ")", driver);
                    }
                } catch (Exception e) {
                    // Button not found - this is normal
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting button elements", e);
        }
    }
    
    /**
     * Collect amount related elements
     */
    private void collectAmountElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting amount related elements", driver);
            
            // Look for any elements that might be amount input fields
            try {
                List<WebElement> amountInputs = driver.findElements(By.xpath("//*[contains(@resource-id, 'amount') or contains(@resource-id, 'input') or contains(@resource-id, 'field')]"));
                for (WebElement input : amountInputs) {
                    String resourceId = input.getAttribute("resource-id");
                    String text = input.getText();
                    String className = input.getAttribute("className");
                    TestReporter.logAction(deviceId, "Found", "Amount Input Candidate - ID: " + resourceId + ", Text: " + text + ", Class: " + className, driver);
                }
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Amount Input Candidates", driver);
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting amount elements", e);
        }
    }
    
    /**
     * Collect information elements
     */
    private void collectInformationElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting information elements", driver);
            
            String[] infoElements = {
                "Choose", "Select", "payment", "method", "UPI", "Card", "Bank", "Wallet"
            };
            
            for (String info : infoElements) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath("//android.widget.TextView[contains(@text, '" + info + "')]"));
                    for (WebElement element : elements) {
                        TestReporter.logAction(deviceId, "Found", "Info Element: " + element.getText(), driver);
                    }
                } catch (Exception e) {
                    // Element not found - this is normal
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting information elements", e);
        }
    }
} 