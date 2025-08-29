package org.fg.pageObjects.android;

import org.fg.utils.AndroidAct;
import org.fg.utils.TestReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import java.time.Duration;

import java.util.List;

public class OtpScreen {
	
	private final AndroidDriver driver;
	private final String deviceId;
	private final WebDriverWait longWait;
	private final WebDriverWait shortWait;
	private static final int LONG_WAIT_SECONDS = 30;
	private static final int SHORT_WAIT_SECONDS = 10;
	
	@AndroidFindBy(id = "com.paytm.paytmplay:id/verify_input")
    private WebElement otpField;

    @AndroidFindBy(id = "com.paytm.paytmplay:id/root_verify_otp")
    private WebElement verifyButton;

    @AndroidFindBy(id = "com.paytm.paytmplay:id/tv_error_message")
    private WebElement errorMessage;
    
    public OtpScreen(AndroidDriver driver) {
        this.driver = driver;
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(LONG_WAIT_SECONDS));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_WAIT_SECONDS));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "LoginScreen", "OTPScreen");
        TestReporter.logAction(deviceId, "Info", "OTP Page opened", driver);
    }

    public void enterOTP(String otp) {
        TestReporter.startStep(deviceId, "Enter OTP");
        try {
            // Check for OTP limit error
            try {
                if (errorMessage.isDisplayed() && errorMessage.getText().contains("OTP limit")) {
                    TestReporter.logAction(deviceId, "Error", "OTP limit reached. Please try again later.", driver);
                    throw new RuntimeException("OTP limit reached. Please try again later.");
                }
            } catch (Exception e) {
                // Error message not found, continue with OTP entry
            }

            TestReporter.logAction(deviceId, "Wait", "Waiting for OTP field to be visible", driver);
            longWait.until(ExpectedConditions.visibilityOf(otpField));
            
            TestReporter.logAction(deviceId, "Input", "Entering OTP: " + otp, driver);
            otpField.sendKeys(otp);
            
            TestReporter.logAction(deviceId, "Input Complete", "Successfully entered OTP", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to enter OTP: " + e.getMessage(), e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Enter OTP");
        }
    }

    public CallbreakLobby verifyButton() {
        TestReporter.startStep(deviceId, "Click Verify Button");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for verify button to be clickable", driver);
            shortWait.until(ExpectedConditions.elementToBeClickable(verifyButton));
            
            TestReporter.logAction(deviceId, "Click", "Clicking verify button", driver);
            verifyButton.click();
            
            // Add a short wait for the app transition
            TestReporter.logAction(deviceId, "Wait", "Waiting for app transition after verify", driver);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                TestReporter.logError(deviceId, "Sleep interrupted during app transition", e);
            }
            
            TestReporter.logPageTransition(deviceId, "OTPScreen", "CallbreakLobby");
            return new CallbreakLobby(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click verify button", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Verify Button");
        }
    }
}
