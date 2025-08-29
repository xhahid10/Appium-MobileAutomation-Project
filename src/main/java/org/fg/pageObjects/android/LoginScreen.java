package org.fg.pageObjects.android;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.fg.utils.TestReporter;
import java.time.Duration;
import java.time.Instant;

public class LoginScreen {
	
	private final AndroidDriver driver;
	private final WebDriverWait wait;
	private final String deviceId;
	
	// Locators
	private static final By PHONE_NUMBER_FIELD = By.id("com.paytm.paytmplay:id/edt_number");
	private static final By CONTINUE_BUTTON = By.id("com.paytm.paytmplay:id/root_send_otp");
	private static final By OTP_FIELD = By.id("com.paytm.paytmplay:id/verify_input");
	private static final By VERIFY_BUTTON = By.id("com.paytm.paytmplay:id/root_verify_otp");
	
	@AndroidFindBy(id = "com.paytm.paytmplay:id/edt_number")
    private WebElement phoneNumberField;
	
	@AndroidFindBy(id = "com.paytm.paytmplay:id/root_send_otp")
	private WebElement loginButton;

	
    public LoginScreen(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "GetStarted", "LoginScreen");
        TestReporter.logAction(deviceId, "Initialization", "LoginScreen initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);
    }

    public void setNumberfield(String number) {
        TestReporter.startStep(deviceId, "Enter Phone Number");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for phone number field to be visible", driver);
            wait.until(ExpectedConditions.visibilityOf(phoneNumberField));
            
            // Hide keyboard if present
            try {
                TestReporter.logAction(deviceId, "Keyboard", "Attempting to hide keyboard", driver);
                driver.hideKeyboard();
            } catch (Exception e) {
                // Ignore if keyboard is not present
            }
            
            TestReporter.logAction(deviceId, "Field Clear", "Clearing phone number field", driver);
            phoneNumberField.clear();
            
            TestReporter.logAction(deviceId, "Input", "Entering phone number: " + number, driver);
            phoneNumberField.sendKeys(number);
            
            TestReporter.logAction(deviceId, "Input Complete", "Successfully entered phone number", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to enter phone number", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Enter Phone Number");
        }
    }

    public OtpScreen loginButton() {
        TestReporter.startStep(deviceId, "Click Login Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for login button to be clickable", driver);
            wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            
            TestReporter.logAction(deviceId, "Click", "Clicking login button", driver);
            loginButton.click();
            
            TestReporter.logPageTransition(deviceId, "LoginScreen", "OtpScreen");
            return new OtpScreen(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click login button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Login Button");
        }
    }

    public boolean isLoginScreenDisplayed() {
        TestReporter.startStep(deviceId, "Check Login Screen");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for Get Started button to be visible", driver);
            WebElement getStartedBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.paytm.paytmplay:id/root_start")));
            
            if (getStartedBtn.isDisplayed()) {
                TestReporter.logAction(deviceId, "Success", "Get Started screen displayed successfully", driver);
                return true;
            } else {
                TestReporter.logAction(deviceId, "Warning", "Get Started button found but not visible", driver);
                return false;
            }
        } catch (Exception e) {
            TestReporter.logAction(deviceId, "Error", "Get Started screen not displayed: " + e.getMessage(), driver);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Login Screen");
        }
    }
    
    public void clickGetStarted() {
        TestReporter.startStep(deviceId, "Click Get Started");
        try {
            TestReporter.logAction(deviceId, "Click", "Clicking Get Started button", driver);
            wait.until(ExpectedConditions.elementToBeClickable(By.id("com.paytm.paytmplay:id/root_start"))).click();
            TestReporter.logAction(deviceId, "Success", "Get Started button clicked successfully", driver);
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click Get Started button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Get Started");
        }
    }
    
    public void clickContinue() {
        TestReporter.startStep(deviceId, "Click Continue");
        try {
            TestReporter.logAction(deviceId, "Click", "Clicking Continue button", driver);
            wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_BUTTON)).click();
            TestReporter.logAction(deviceId, "Success", "Continue button clicked successfully", driver);
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click Continue button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Continue");
        }
    }
    
    public void enterOTP(String otp) {
        TestReporter.startStep(deviceId, "Enter OTP");
        try {
            TestReporter.logAction(deviceId, "Input", "Entering OTP: " + otp, driver);
            WebElement otpField = wait.until(ExpectedConditions.presenceOfElementLocated(OTP_FIELD));
            otpField.clear();
            otpField.sendKeys(otp);
            TestReporter.logAction(deviceId, "Success", "OTP entered successfully", driver);
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to enter OTP", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Enter OTP");
        }
    }
    
    public void clickVerify() {
        TestReporter.startStep(deviceId, "Click Verify");
        try {
            TestReporter.logAction(deviceId, "Click", "Clicking Verify button", driver);
            wait.until(ExpectedConditions.elementToBeClickable(VERIFY_BUTTON)).click();
            TestReporter.logAction(deviceId, "Success", "Verify button clicked successfully", driver);
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click Verify button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Verify");
        }
    }
}
