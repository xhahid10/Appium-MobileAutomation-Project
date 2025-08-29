package org.fg.pageObjects.android;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.fg.utils.TestReporter;
import java.time.Duration;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import java.time.Instant;
import java.util.List;
import com.google.common.collect.ImmutableMap;

public class CallbreakLobby {
    
    private final AndroidDriver driver;
    private final String deviceId;
    private final WebDriverWait wait;
    private final WebDriverWait shortWait;
    private static final int WAIT_TIMEOUT = 15;
    private static final int SHORT_TIMEOUT = 1;
    
    // Locators
    public static final By HAMBURGER_MENU = By.id("com.paytm.paytmplay:id/v_hamburg_bg");
    public static final By HAMBURGER_MENU_SECOND = By.xpath("//android.view.View[@resource-id=\"com.paytm.paytmplay:id/v_hamburg_bg\"]");
    public static final By HAMBURGER_MENU_THIRD = By.xpath("//android.widget.ImageView[@resource-id=\"com.paytm.paytmplay:id/iv_head_hamburg\"]");
    private static final By LOBBY_CONTAINER = By.id("com.paytm.paytmplay:id/lobby_container");
    private static final By BANNER_CLOSE = By.xpath("//android.widget.Button[@content-desc='close' or @content-desc='Close' or @content-desc='inapp_close_btn']");
    private static final By WALLET_HEADER_PLUS_ICON = By.xpath("//android.widget.ImageView[@resource-id=\"com.paytm.paytmplay:id/iv_head_wallet_add\"]");
    
    @AndroidFindBy(id = "com.paytm.paytmplay:id/v_hamburg_bg")
    private WebElement hamburgerMenuButton;
    
    @AndroidFindBy(id = "com.paytm.paytmplay:id/lobby_container")
    private WebElement lobbyContainer;
    
    public CallbreakLobby(AndroidDriver driver) {
        this.driver = driver;
        this.deviceId = String.format("%s_%s", 
            driver.getCapabilities().getCapability("deviceName"),
            driver.getCapabilities().getCapability("platformVersion"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_TIMEOUT));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        TestReporter.logPageTransition(deviceId, "OTPScreen", "CallbreakLobby");
        handleBanner();
    }

    private void handleBanner() {
        TestReporter.startStep(deviceId, "Handle Banner");
        try {
            Instant startTime = Instant.now();
            
            // Check for CleverTap banner (pip_container)
            try {
                WebElement cleverTapBanner = shortWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.view.ViewGroup[@resource-id=\"com.paytm.paytmplay:id/pip_container\"]")
                ));
                
                if (cleverTapBanner.isDisplayed()) {
                    TestReporter.logAction(deviceId, "Banner", "CleverTap banner found, attempting to close", driver);
                    
                    // Try multiple strategies to close the banner
                    boolean bannerClosed = false;
                    
                    // Strategy 1: Try common close button patterns
                    String[] closeButtonPatterns = {
                        ".//android.widget.ImageView[@content-desc='close' or @content-desc='Close' or @content-desc='inapp_close_btn' or @content-desc='Close Button']",
                        ".//android.widget.Button[@content-desc='close' or @content-desc='Close' or @content-desc='inapp_close_btn']",
                        ".//android.widget.ImageView[contains(@resource-id, 'close') or contains(@resource-id, 'dismiss')]",
                        ".//android.widget.Button[contains(@resource-id, 'close') or contains(@resource-id, 'dismiss')]",
                        ".//*[@content-desc='close' or @content-desc='Close' or @content-desc='inapp_close_btn']"
                    };
                    
                    for (String pattern : closeButtonPatterns) {
                        if (!bannerClosed) {
                            try {
                                WebElement closeButton = cleverTapBanner.findElement(By.xpath(pattern));
                                if (closeButton.isDisplayed() && closeButton.isEnabled()) {
                                    closeButton.click();
                                    bannerClosed = true;
                                    break;
                                }
                            } catch (NoSuchElementException e) {
                                // Continue to next pattern
                            }
                        }
                    }
                    
                    // Strategy 2: Try clicking on the banner container itself (might close it)
                    if (!bannerClosed) {
                        try {
                            cleverTapBanner.click();
                            Thread.sleep(1000); // Wait to see if it closes
                            bannerClosed = true;
                        } catch (Exception e) {
                            TestReporter.logAction(deviceId, "Warning", "Failed to click on CleverTap banner container: " + e.getMessage(), driver);
                        }
                    }
                    
                    // Strategy 3: Fallback - Navigate through challenges section
                    if (!bannerClosed) {
                        TestReporter.logAction(deviceId, "Info", "Using fallback navigation through challenges", driver);
                        navigateThroughChallengesSection();
                        bannerClosed = true;
                    }
                    
                    if (bannerClosed) {
                        TestReporter.logAction(deviceId, "Success", "CleverTap banner handled successfully", driver);
                    } else {
                        TestReporter.logAction(deviceId, "Warning", "Could not close CleverTap banner, continuing anyway", driver);
                    }
                }
            } catch (TimeoutException e) {
                // No CleverTap banner found - this is normal
            }
            
            // Quick check for other banner presence (original logic)
            try {
                boolean isBannerPresent = shortWait.until(driver -> {
                    try {
                        return driver.findElement(BANNER_CLOSE).isDisplayed();
                    } catch (NoSuchElementException e) {
                        return false;
                    }
                });
                
                if (isBannerPresent) {
                    Instant clickStart = Instant.now();
                    driver.findElement(BANNER_CLOSE).click();
                    Duration clickDuration = Duration.between(clickStart, Instant.now());
                    TestReporter.logAction(deviceId, "Success", "Other banner closed successfully in " + clickDuration.toMillis() + "ms", driver);
                }
            } catch (TimeoutException e) {
                // No banner appeared within short timeout - this is expected
            }
            
            Duration checkDuration = Duration.between(startTime, Instant.now());
            TestReporter.logAction(deviceId, "Info", "Banner check completed in " + checkDuration.toMillis() + "ms", driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Unexpected error while handling banner", e);
        } finally {
            TestReporter.endStep(deviceId, "Handle Banner");
        }
    }
    
    /**
     * Fallback method to navigate through challenges section when banner cannot be closed
     */
    private void navigateThroughChallengesSection() {
        TestReporter.startStep(deviceId, "Navigate Through Challenges Section");
        try {
            // Step 1: Click in the center of the screen to navigate to challenges
            int screenWidth = driver.manage().window().getSize().getWidth();
            int screenHeight = driver.manage().window().getSize().getHeight();
            int centerX = screenWidth / 2;
            int centerY = screenHeight / 2;
            
            driver.executeScript("mobile: tapGesture", ImmutableMap.of(
                "x", centerX, "y", centerY
            ));
            
            Thread.sleep(2000); // Wait for navigation
            
            // Step 2: Verify we are on challenges page
            try {
                WebElement challengesIcon = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.Image[@text=\"ic_challenges_4ba44e\"]")
                ));
                
                if (!challengesIcon.isDisplayed()) {
                    TestReporter.logAction(deviceId, "Warning", "Challenges icon not visible, but continuing", driver);
                }
            } catch (TimeoutException e) {
                TestReporter.logAction(deviceId, "Warning", "Could not verify challenges page, but continuing: " + e.getMessage(), driver);
            }
            
            // Step 3: Click on lobby icon in footer to return to lobby
            try {
                WebElement lobbyIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@resource-id=\"com.paytm.paytmplay:id/sec_item_1\"]")
                ));
                
                lobbyIcon.click();
                
                // Wait for lobby to load
                Thread.sleep(3000);
                
                // Verify we're back in lobby
                try {
                    WebElement hamburgerMenu = wait.until(ExpectedConditions.presenceOfElementLocated(HAMBURGER_MENU));
                    if (hamburgerMenu.isDisplayed()) {
                        TestReporter.logAction(deviceId, "Success", "Successfully returned to lobby through challenges navigation", driver);
                    } else {
                        TestReporter.logAction(deviceId, "Warning", "Lobby verification failed after challenges navigation", driver);
                    }
                } catch (TimeoutException e) {
                    TestReporter.logAction(deviceId, "Warning", "Could not verify lobby after challenges navigation: " + e.getMessage(), driver);
                }
                
            } catch (Exception e) {
                TestReporter.logError(deviceId, "Failed to navigate back to lobby from challenges: " + e.getMessage(), e);
            }
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to navigate through challenges section", e);
        } finally {
            TestReporter.endStep(deviceId, "Navigate Through Challenges Section");
        }
    }
    
    public boolean isLobbyLoaded() {
        TestReporter.startStep(deviceId, "Check Lobby Loaded");
        try {
            Instant startTime = Instant.now();
            TestReporter.logAction(deviceId, "Strategy", "Starting lobby verification with multiple strategies", driver);
            
            // Strategy 1: Check by Hamburger Menu (faster and more reliable)
            try {
                Instant strategy1Start = Instant.now();
                TestReporter.logAction(deviceId, "Strategy 1", "Checking hamburger menu presence", driver);
                
                // Use short wait first for quick check
                try {
                    WebElement hamburger = shortWait.until(ExpectedConditions.presenceOfElementLocated(HAMBURGER_MENU));
                    if (hamburger.isDisplayed()) {
                        Duration strategy1Duration = Duration.between(strategy1Start, Instant.now());
                        TestReporter.logAction(deviceId, "Success", "Found hamburger menu in " + strategy1Duration.toMillis() + "ms", driver);
                        return true;
                    }
                } catch (TimeoutException e) {
                    // If short wait fails, try with normal wait
                    WebElement hamburger = wait.until(ExpectedConditions.presenceOfElementLocated(HAMBURGER_MENU));
                    if (hamburger.isDisplayed()) {
                        Duration strategy1Duration = Duration.between(strategy1Start, Instant.now());
                        TestReporter.logAction(deviceId, "Success", "Found hamburger menu with longer wait in " + strategy1Duration.toMillis() + "ms", driver);
                        return true;
                    }
                }
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Strategy 1", "Failed to find hamburger menu: " + e.getMessage(), driver);
            }
            
            // Strategy 2: Check by ID (with shorter timeout)
            try {
                Instant strategy2Start = Instant.now();
                TestReporter.logAction(deviceId, "Strategy 2", "Checking lobby container by ID", driver);
                WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(LOBBY_CONTAINER));
                if (container.isDisplayed()) {
                    Duration strategy2Duration = Duration.between(strategy2Start, Instant.now());
                    TestReporter.logAction(deviceId, "Success", "Found lobby container by ID in " + strategy2Duration.toMillis() + "ms", driver);
                    return true;
                }
            } catch (Exception e) {
                TestReporter.logAction(deviceId, "Strategy 2", "Failed to find lobby container by ID: " + e.getMessage(), driver);
            }
            
            Duration totalDuration = Duration.between(startTime, Instant.now());
            TestReporter.logError(deviceId, "All lobby verification strategies failed in " + totalDuration.toMillis() + "ms", null);
            return false;
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Unexpected error checking lobby state", e);
            return false;
        } finally {
            TestReporter.endStep(deviceId, "Check Lobby Loaded");
        }
    }
    
    public HamburgerMenuCallbreak openHamburgerMenu() {
        TestReporter.startStep(deviceId, "Open Hamburger Menu");
        Instant startTime = Instant.now();
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for hamburger menu to be clickable", driver);
            
            // Try all possible hamburger menu locators
            WebElement hamburgerMenu = null;
            try {
                hamburgerMenu = shortWait.until(ExpectedConditions.elementToBeClickable(HAMBURGER_MENU));
            } catch (TimeoutException e) {
                try {
                    hamburgerMenu = shortWait.until(ExpectedConditions.elementToBeClickable(HAMBURGER_MENU_SECOND));
                } catch (TimeoutException e2) {
                    hamburgerMenu = shortWait.until(ExpectedConditions.elementToBeClickable(HAMBURGER_MENU_THIRD));
                }
            }
            
            // Click the menu
            Instant clickStart = Instant.now();
            TestReporter.logAction(deviceId, "Click", "Clicking hamburger menu", driver);
            hamburgerMenu.click();
            Duration clickDuration = Duration.between(clickStart, Instant.now());
            TestReporter.logAction(deviceId, "Click", "Menu click completed in " + clickDuration.toMillis() + "ms", driver);
            
            Duration totalDuration = Duration.between(startTime, Instant.now());
            TestReporter.logAction(deviceId, "Success", "Menu opened successfully in " + totalDuration.toMillis() + "ms", driver);
            TestReporter.logPageTransition(deviceId, "CallbreakLobby", "HamburgerMenu");
            return new HamburgerMenuCallbreak(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to open hamburger menu", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Open Hamburger Menu");
        }
    }

    /**
     * Click wallet header plus icon to access add cash functionality
     * @return AddCashPage - The add cash page for entering amount
     */
    public AddCashPage clickWalletHeaderPlusIcon() {
        TestReporter.startStep(deviceId, "Click Wallet Header Plus Icon");
        try {
            TestReporter.logAction(deviceId, "Wait", "Waiting for wallet header plus icon to be clickable", driver);
            WebElement walletPlusIcon = wait.until(ExpectedConditions.elementToBeClickable(WALLET_HEADER_PLUS_ICON));
            
            TestReporter.logAction(deviceId, "Click", "Clicking wallet header plus icon", driver);
            walletPlusIcon.click();
            
            TestReporter.logAction(deviceId, "Success", "Wallet header plus icon clicked successfully", driver);
            
            TestReporter.logPageTransition(deviceId, "CallbreakLobby", "AddCashPage");
            return new AddCashPage(driver);
            
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to click wallet header plus icon", e);
            throw e;
        } finally {
            TestReporter.endStep(deviceId, "Click Wallet Header Plus Icon");
        }
    }
} 