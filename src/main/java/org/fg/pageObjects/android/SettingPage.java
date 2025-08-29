package org.fg.pageObjects.android;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import java.time.Duration;
import org.fg.utils.TestReporter;

public class SettingPage {
	
	private final AndroidDriver driver;
	private final WebDriverWait wait;
	private final String deviceId;
	
	// Locators
	private static final By LOGOUT_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/tv_lefttext' and @text='Log Out']");
	private static final By CONFIRM_LOGOUT = By.id("com.paytm.paytmplay:id/btn_sure");
	private static final By LOGIN_SCREEN = By.id("com.paytm.paytmplay:id/root_start");
	
	public SettingPage(AndroidDriver driver) {
		this.driver = driver;
		this.deviceId = String.format("%s_%s", 
			driver.getCapabilities().getCapability("deviceName"),
			driver.getCapabilities().getCapability("platformVersion"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}
	
	public boolean isSettingsPageLoaded() {
		TestReporter.startStep(deviceId, "Check Settings Loaded");
		try {
			TestReporter.logAction(deviceId, "Wait", "Waiting for logout button to be visible", driver);
			WebElement logoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(LOGOUT_BUTTON));
			
			if (logoutBtn.isDisplayed()) {
				TestReporter.logAction(deviceId, "Success", "Settings page loaded successfully", driver);
				return true;
			} else {
				TestReporter.logAction(deviceId, "Warning", "Logout button found but not visible", driver);
				return false;
			}
		} catch (Exception e) {
			TestReporter.logAction(deviceId, "Error", "Settings page not loaded: " + e.getMessage(), driver);
			return false;
		} finally {
			TestReporter.endStep(deviceId, "Check Settings Loaded");
		}
	}
	
	public boolean logout() {
		TestReporter.startStep(deviceId, "Logout");
		try {
			// Click logout button
			TestReporter.logAction(deviceId, "Click", "Clicking logout button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_BUTTON)).click();
			
			// Click confirm logout
			TestReporter.logAction(deviceId, "Click", "Clicking confirm logout button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_LOGOUT)).click();
			
			// Wait for return to login screen
			TestReporter.logAction(deviceId, "Wait", "Waiting for login screen to appear", driver);
			wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_SCREEN));
			
			TestReporter.logAction(deviceId, "Success", "Logout completed successfully", driver);
			return true;
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Logout failed", e);
			return false;
		} finally {
			TestReporter.endStep(deviceId, "Logout");
		}
	}
}
