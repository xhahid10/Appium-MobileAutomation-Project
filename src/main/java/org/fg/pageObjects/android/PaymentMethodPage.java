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

public class PaymentMethodPage {
    private AndroidDriver driver;
    private WebDriverWait wait;
    private String deviceId;

    // Locators for Payment Method page
    private static final By PAYMENT_METHOD_UPI = By.xpath("//android.widget.TextView[@text='UPI']");
    private static final By PAYMENT_METHOD_CARD = By.xpath("//android.widget.TextView[@text='Card']");
    private static final By PAYMENT_METHOD_NETBANKING = By.xpath("//android.widget.TextView[@text='Net Banking']");
    private static final By PAYMENT_METHOD_WALLET = By.xpath("//android.widget.TextView[@text='Wallet']");
    private static final By PROCEED_BUTTON = By.id("com.paytm.paytmplay:id/proceed_button");
    private static final By PAY_NOW_BUTTON = By.id("com.paytm.paytmplay:id/pay_now_button");
    
    // Credit/Debit card specific locators
    private static final By CREDIT_DEBIT_CARD_RADIO_BUTTON = By.xpath("(//android.widget.ImageView[@resource-id=\"com.paytm.paytmplay:id/paytm_radio_button\"])[1]");
    private static final By PAY_BUTTON = By.id("com.paytm.paytmplay:id/action_button_text");
    
    // Additional comprehensive locators for Payment Method page
    private static final By PAGE_TITLE = By.xpath("//android.widget.TextView[contains(@text, 'Payment') or contains(@text, 'Choose') or contains(@text, 'Select')]");
    private static final By AMOUNT_DISPLAY = By.xpath("//android.widget.TextView[contains(@text, '₹') or contains(@text, 'Rs')]");
    private static final By BACK_BUTTON = By.xpath("//android.widget.ImageView[@content-desc='Navigate up'] | //android.widget.ImageButton[@content-desc='Navigate up']");
    private static final By CLOSE_BUTTON = By.xpath("//android.widget.ImageView[@content-desc='Close'] | //android.widget.ImageButton[@content-desc='Close']");
    
    // Payment method containers/cards
    private static final By PAYMENT_METHOD_CONTAINERS = By.xpath("//android.widget.RelativeLayout[contains(@resource-id, 'payment')] | //android.widget.LinearLayout[contains(@resource-id, 'payment')]");
    private static final By PAYMENT_METHOD_ICONS = By.xpath("//android.widget.ImageView[contains(@resource-id, 'payment') or contains(@resource-id, 'method')]");
    
    // Specific payment method options (expanded)
    private static final By PAYMENT_METHOD_CREDIT_CARD = By.xpath("//android.widget.TextView[@text='Credit Card'] | //android.widget.TextView[contains(@text, 'Credit')]");
    private static final By PAYMENT_METHOD_DEBIT_CARD = By.xpath("//android.widget.TextView[@text='Debit Card'] | //android.widget.TextView[contains(@text, 'Debit')]");
    private static final By PAYMENT_METHOD_UPI_ID = By.xpath("//android.widget.TextView[@text='UPI ID'] | //android.widget.TextView[contains(@text, 'UPI')]");
    private static final By PAYMENT_METHOD_PAYTM_WALLET = By.xpath("//android.widget.TextView[@text='Paytm Wallet'] | //android.widget.TextView[contains(@text, 'Wallet')]");
    private static final By PAYMENT_METHOD_BANK_TRANSFER = By.xpath("//android.widget.TextView[@text='Bank Transfer'] | //android.widget.TextView[contains(@text, 'Transfer')]");
    
    // Action buttons (expanded)
    private static final By CONTINUE_BUTTON = By.xpath("//android.widget.Button[@text='Continue'] | //android.widget.TextView[@text='Continue']");
    private static final By NEXT_BUTTON = By.xpath("//android.widget.Button[@text='Next'] | //android.widget.TextView[@text='Next']");
    private static final By CONFIRM_BUTTON = By.xpath("//android.widget.Button[@text='Confirm'] | //android.widget.TextView[@text='Confirm']");
    private static final By SUBMIT_BUTTON = By.xpath("//android.widget.Button[@text='Submit'] | //android.widget.TextView[@text='Submit']");
    
    // Information/description elements
    private static final By PAYMENT_INFO = By.xpath("//android.widget.TextView[contains(@text, 'Choose') or contains(@text, 'Select') or contains(@text, 'payment')]");
    private static final By TERMS_AND_CONDITIONS = By.xpath("//android.widget.TextView[contains(@text, 'Terms') or contains(@text, 'Conditions')]");
    private static final By PRIVACY_POLICY = By.xpath("//android.widget.TextView[contains(@text, 'Privacy') or contains(@text, 'Policy')]");
    
    // Loading/processing elements
    private static final By LOADING_SPINNER = By.xpath("//android.widget.ProgressBar | //android.widget.ImageView[contains(@resource-id, 'loading')]");
    private static final By PROCESSING_TEXT = By.xpath("//android.widget.TextView[contains(@text, 'Processing') or contains(@text, 'Loading')]");
    
    // Error/Success messages
    private static final By ERROR_MESSAGE = By.xpath("//android.widget.TextView[contains(@text, 'Error') or contains(@text, 'Failed') or contains(@text, 'Invalid')]");
    private static final By SUCCESS_MESSAGE = By.xpath("//android.widget.TextView[contains(@text, 'Success') or contains(@text, 'Valid')]");
    
    // Amount and fee related elements
    private static final By TRANSACTION_AMOUNT = By.xpath("//android.widget.TextView[contains(@text, 'Amount') or contains(@text, 'Total')]");
    private static final By PROCESSING_FEE = By.xpath("//android.widget.TextView[contains(@text, 'Fee') or contains(@text, 'Charges')]");
    private static final By TOTAL_AMOUNT = By.xpath("//android.widget.TextView[contains(@text, 'Total') or contains(@text, 'Payable')]");
    
    // Security/verification elements
    private static final By SECURE_PAYMENT_BADGE = By.xpath("//android.widget.ImageView[contains(@resource-id, 'secure')] | //android.widget.TextView[contains(@text, 'Secure')]");
    private static final By VERIFIED_BADGE = By.xpath("//android.widget.ImageView[contains(@resource-id, 'verified')] | //android.widget.TextView[contains(@text, 'Verified')]");
    
    // Additional action elements
    private static final By SAVE_PAYMENT_METHOD = By.xpath("//android.widget.CheckBox[contains(@text, 'Save')] | //android.widget.TextView[contains(@text, 'Save')]");
    private static final By APPLY_COUPON = By.xpath("//android.widget.TextView[contains(@text, 'Coupon') or contains(@text, 'Discount')]");
    private static final By VIEW_DETAILS = By.xpath("//android.widget.TextView[contains(@text, 'Details') or contains(@text, 'More')]");

    public PaymentMethodPage(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "AddCashPage", "PaymentMethodPage");
        TestReporter.logAction(deviceId, "Initialization", "PaymentMethodPage initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);
    }

    /**
     * Select payment method
     * @param paymentMethod - "upi", "card", "netbanking", or "wallet"
     */
    public void selectPaymentMethod(String paymentMethod) {
        TestReporter.startStep(deviceId, "Select Payment Method");
        try {
            TestReporter.logAction(deviceId, "Action", "Selecting payment method: " + paymentMethod, driver);
            
            By paymentMethodLocator;
            switch (paymentMethod.toLowerCase()) {
                case "upi":
                    paymentMethodLocator = PAYMENT_METHOD_UPI;
                    break;
                case "card":
                    paymentMethodLocator = PAYMENT_METHOD_CARD;
                    break;
                case "netbanking":
                    paymentMethodLocator = PAYMENT_METHOD_NETBANKING;
                    break;
                case "wallet":
                    paymentMethodLocator = PAYMENT_METHOD_WALLET;
                    break;
                default:
                    TestReporter.logAction(deviceId, "Warning", "Unknown payment method: " + paymentMethod + ", defaulting to UPI", driver);
                    paymentMethodLocator = PAYMENT_METHOD_UPI;
                    break;
            }
            
            TestReporter.logAction(deviceId, "Wait", "Waiting for payment method to be clickable", driver);
            WebElement paymentMethodElement = wait.until(ExpectedConditions.elementToBeClickable(paymentMethodLocator));
            
            TestReporter.logAction(deviceId, "Click", "Clicking payment method: " + paymentMethod, driver);
            paymentMethodElement.click();
            
            TestReporter.logAction(deviceId, "Success", "Payment method selected successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to select payment method", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Select Payment Method");
        }
    }

    /**
     * Select credit/debit card radio button
     */
    public void selectCreditDebitCard() {
        TestReporter.startStep(deviceId, "Select Credit/Debit Card");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for credit/debit card radio button to be clickable", driver);
            WebElement radioButton = wait.until(ExpectedConditions.elementToBeClickable(CREDIT_DEBIT_CARD_RADIO_BUTTON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking credit/debit card radio button", driver);
            radioButton.click();
            
            TestReporter.logAction(deviceId, "Success", "Credit/debit card selected successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to select credit/debit card", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Select Credit/Debit Card");
        }
    }

    /**
     * Verify pay button is clickable (without clicking it)
     * @return boolean - true if pay button is clickable, false otherwise
     */
    public boolean verifyPayButtonClickable() {
        TestReporter.startStep(deviceId, "Verify Pay Button Clickable");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for pay button to be clickable", driver);
            WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(PAY_BUTTON));
            
            if (payButton.isDisplayed() && payButton.isEnabled()) {
                TestReporter.logAction(deviceId, "Success", "Pay button is clickable and ready", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Pay button found but not clickable", driver);
                return false;
            }
            
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Pay button not found or not clickable: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Verify Pay Button Clickable");
        }
    }

    /**
     * Click proceed button and navigate to result page
     * @return AddCashResultPage - The result page showing the outcome of the add cash operation
     */
    public AddCashResultPage clickProceedButton() {
        TestReporter.startStep(deviceId, "Click Proceed Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for proceed button to be clickable", driver);
            WebElement proceedButton = wait.until(ExpectedConditions.elementToBeClickable(PROCEED_BUTTON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking proceed button", driver);
            proceedButton.click();
            
            TestReporter.logAction(deviceId, "Success", "Proceed button clicked successfully", driver);
            
            TestReporter.logPageTransition(deviceId, "PaymentMethodPage", "AddCashResultPage");
            return new AddCashResultPage(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click proceed button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Proceed Button");
        }
    }

    /**
     * Click pay now button (alternative to proceed button)
     * @return AddCashResultPage - The result page showing the outcome of the add cash operation
     */
    public AddCashResultPage clickPayNowButton() {
        TestReporter.startStep(deviceId, "Click Pay Now Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for pay now button to be clickable", driver);
            WebElement payNowButton = wait.until(ExpectedConditions.elementToBeClickable(PAY_NOW_BUTTON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking pay now button", driver);
            payNowButton.click();
            
            TestReporter.logAction(deviceId, "Success", "Pay now button clicked successfully", driver);
            
            TestReporter.logPageTransition(deviceId, "PaymentMethodPage", "AddCashResultPage");
            return new AddCashResultPage(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click pay now button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Pay Now Button");
        }
    }

    /**
     * Check if Payment Method page is loaded
     */
    public boolean isPaymentMethodPageLoaded() {
        TestReporter.startStep(deviceId, "Check Payment Method Page Loaded");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for UPI payment method to be visible", driver);
            WebElement upiMethod = wait.until(ExpectedConditions.visibilityOfElementLocated(PAYMENT_METHOD_UPI));
            
            if (upiMethod.isDisplayed()) {
                TestReporter.logAction(deviceId, "Success", "Payment Method page loaded successfully", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "UPI payment method found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Payment Method page not loaded: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Payment Method Page Loaded");
        }
    }

    /**
     * Collect and log all web elements present on the Payment Method page
     */
    public void collectAllWebElements() {
        TestReporter.startStep(deviceId, "Collect All Web Elements");
        try {
            TestReporter.logAction(deviceId, "Info", "Starting comprehensive web element collection on Payment Method page", driver);
            
            // Wait for page to load
            Thread.sleep(2000);
            
            // Collect all elements by different categories
            collectBasicElements();
            collectPaymentMethodElements();
            collectActionButtons();
            collectInformationElements();
            collectAmountElements();
            collectSecurityElements();
            collectAdditionalElements();
            
            TestReporter.logAction(deviceId, "Success", "Web element collection completed successfully", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to collect web elements", e);
        } finally {
            TestReporter.endStep(deviceId, "Collect All Web Elements");
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
                WebElement pageTitle = driver.findElement(PAGE_TITLE);
                TestReporter.logAction(deviceId, "Found", "Page Title: " + pageTitle.getText(), driver);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Page Title", driver);
            }
            
            // Amount display
            try {
                WebElement amountDisplay = driver.findElement(AMOUNT_DISPLAY);
                TestReporter.logAction(deviceId, "Found", "Amount Display: " + amountDisplay.getText(), driver);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Amount Display", driver);
            }
            
            // Back button
            try {
                WebElement backButton = driver.findElement(BACK_BUTTON);
                TestReporter.logAction(deviceId, "Found", "Back Button: " + backButton.getAttribute("content-desc"), driver);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Back Button", driver);
            }
            
            // Close button
            try {
                WebElement closeButton = driver.findElement(CLOSE_BUTTON);
                TestReporter.logAction(deviceId, "Found", "Close Button: " + closeButton.getAttribute("content-desc"), driver);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Close Button", driver);
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting basic elements", e);
        }
    }
    
    /**
     * Collect payment method elements
     */
    private void collectPaymentMethodElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting payment method elements", driver);
            
            // Payment method containers
            try {
                List<WebElement> containers = driver.findElements(PAYMENT_METHOD_CONTAINERS);
                TestReporter.logAction(deviceId, "Found", "Payment Method Containers: " + containers.size(), driver);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Payment Method Containers", driver);
            }
            
            // Payment method icons
            try {
                List<WebElement> icons = driver.findElements(PAYMENT_METHOD_ICONS);
                TestReporter.logAction(deviceId, "Found", "Payment Method Icons: " + icons.size(), driver);
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Not Found", "Payment Method Icons", driver);
            }
            
            // Specific payment methods
            String[] paymentMethods = {
                "UPI", "Card", "Net Banking", "Wallet", "Credit Card", "Debit Card", "UPI ID", "Paytm Wallet", "Bank Transfer"
            };
            
            for (String method : paymentMethods) {
                try {
                    By locator = By.xpath("//android.widget.TextView[contains(@text, '" + method + "')]");
                    WebElement element = driver.findElement(locator);
                    TestReporter.logAction(deviceId, "Found", "Payment Method: " + element.getText(), driver);
                } catch (Exception e) {
                    // Payment method not found - this is normal
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting payment method elements", e);
        }
    }
    
    /**
     * Collect action buttons
     */
    private void collectActionButtons() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting action buttons", driver);
            
            String[] actionButtons = {
                "Proceed", "Pay Now", "Continue", "Next", "Confirm", "Submit"
            };
            
            for (String button : actionButtons) {
                try {
                    By locator = By.xpath("//android.widget.Button[@text='" + button + "'] | //android.widget.TextView[@text='" + button + "']");
                    WebElement element = driver.findElement(locator);
                    TestReporter.logAction(deviceId, "Found", "Action Button: " + element.getText(), driver);
                } catch (Exception e) {
                    // Button not found - this is normal
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting action buttons", e);
        }
    }
    
    /**
     * Collect information elements
     */
    private void collectInformationElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting information elements", driver);
            
            String[] infoElements = {
                "Choose", "Select", "payment", "Terms", "Conditions", "Privacy", "Policy"
            };
            
            for (String info : infoElements) {
                try {
                    By locator = By.xpath("//android.widget.TextView[contains(@text, '" + info + "')]");
                    List<WebElement> elements = driver.findElements(locator);
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
    
    /**
     * Collect amount related elements
     */
    private void collectAmountElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting amount related elements", driver);
            
            String[] amountElements = {
                "Amount", "Total", "Fee", "Charges", "Payable"
            };
            
            for (String amount : amountElements) {
                try {
                    By locator = By.xpath("//android.widget.TextView[contains(@text, '" + amount + "')]");
                    List<WebElement> elements = driver.findElements(locator);
                    for (WebElement element : elements) {
                        TestReporter.logAction(deviceId, "Found", "Amount Element: " + element.getText(), driver);
                    }
                } catch (Exception e) {
                    // Element not found - this is normal
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting amount elements", e);
        }
    }
    
    /**
     * Collect security elements
     */
    private void collectSecurityElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting security elements", driver);
            
            String[] securityElements = {
                "Secure", "Verified", "Safe", "Protected"
            };
            
            for (String security : securityElements) {
                try {
                    By locator = By.xpath("//android.widget.TextView[contains(@text, '" + security + "')] | //android.widget.ImageView[contains(@resource-id, '" + security.toLowerCase() + "')]");
                    List<WebElement> elements = driver.findElements(locator);
                    for (WebElement element : elements) {
                        TestReporter.logAction(deviceId, "Found", "Security Element: " + element.getText(), driver);
                    }
                } catch (Exception e) {
                    // Element not found - this is normal
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting security elements", e);
        }
    }
    
    /**
     * Collect additional elements
     */
    private void collectAdditionalElements() {
        try {
            TestReporter.logAction(deviceId, "Elements", "Collecting additional elements", driver);
            
            String[] additionalElements = {
                "Save", "Coupon", "Discount", "Details", "More"
            };
            
            for (String additional : additionalElements) {
                try {
                    By locator = By.xpath("//android.widget.TextView[contains(@text, '" + additional + "')] | //android.widget.CheckBox[contains(@text, '" + additional + "')]");
                    List<WebElement> elements = driver.findElements(locator);
                    for (WebElement element : elements) {
                        TestReporter.logAction(deviceId, "Found", "Additional Element: " + element.getText(), driver);
                    }
                } catch (Exception e) {
                    // Element not found - this is normal
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Error collecting additional elements", e);
        }
    }
} 