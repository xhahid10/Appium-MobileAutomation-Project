package org.fg.utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AndroidAct {
    
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    
    public AndroidAct(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void handleBannerPopup() {
        try {
            // Replace with the locator for the close button of the banner
            By closeButtonLocator = By.id("inapp_close_btn"); // Replace with actual locator
            driver.findElement(closeButtonLocator).click();
            // Banner closed successfully
        } catch (NoSuchElementException e) {
            // Banner not present, continuing with remaining code
        }
    }
}
