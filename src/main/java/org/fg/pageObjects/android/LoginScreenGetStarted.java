package org.fg.pageObjects.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.fg.utils.TestReporter;
import java.time.Instant;
import org.fg.utils.AppiumUtils;
import com.aventstack.extentreports.Status;

public class LoginScreenGetStarted {

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private final String deviceId;
    private static final int WAIT_TIMEOUT = 30;
    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String REPORTS_PATH = BASE_PATH + "/reports";
    private static final String SCREENSHOTS_PATH = REPORTS_PATH + "/screenshots";

    @AndroidFindBy(id = "com.paytm.paytmplay:id/root_start")
    private WebElement getStartedButton;

    public LoginScreenGetStarted(AndroidDriver driver) {
        Instant startTime = Instant.now();
        this.driver = driver;
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logAction(deviceId, "Initialization", "LoginScreenGetStarted initialized in " + Duration.between(startTime, Instant.now()).toMillis() + "ms", driver);
    }

    public LoginScreen getStarted() {
        TestReporter.startStep(deviceId, "Get Started Flow");
        try {
            TestReporter.logAction(deviceId, "Flow", "Initializing Get Started Screen", driver);
            
            // Wait for Get Started button to be present and visible
            TestReporter.logAction(deviceId, "Wait", "Waiting for Get Started button", driver);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.paytm.paytmplay:id/root_start")));
            wait.until(ExpectedConditions.visibilityOf(getStartedButton));
            
            // Log button state
            TestReporter.logAction(deviceId, "Info", String.format(
                "Get Started Button State - Displayed: %b, Enabled: %b",
                getStartedButton.isDisplayed(),
                getStartedButton.isEnabled()
            ), driver);
            
            // Wait for button to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(getStartedButton));
            
            // Click Get Started
            TestReporter.logAction(deviceId, "Click", "Clicking Get Started button", driver);
            getStartedButton.click();
            
            try {
                // Wait for transition
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                TestReporter.logAction(deviceId, "Warning", "Sleep interrupted during transition", driver);
            }
            
            TestReporter.logAction(deviceId, "Flow", "Get Started Screen Completed", driver);
            return new LoginScreen(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Get Started flow failed", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Get Started Flow");
        }
    }
}
