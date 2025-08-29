package org.fg.pageObjects.android;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.fg.utils.TestReporter;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import org.fg.utils.AppiumUtils;

public class HamburgerMenuCallbreak {
	
	private final AndroidDriver driver;
	private final String deviceId;
	private final WebDriverWait wait;
	private final WebDriverWait shortWait;
	private final WebDriverWait longWait;
	private static final int WAIT_TIMEOUT = 15;  // Increased for better reliability
	private static final int SHORT_TIMEOUT = 5;  // Increased for better reliability
	private static final int LONG_TIMEOUT = 30;  // Added for complex operations
	
	// Locators
	private static final By SETTINGS_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Settings']");
	private static final By PROFILE_BUTTON = By.id("com.paytm.paytmplay:id/header_tv_nickname");
	private static final By MY_BALANCE_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='My Balance']");
	private static final By REFER_EARN_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Refer & Earn']");
	private static final By PAYMENT_SETTINGS_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Payment Settings']");
	private static final By INBOX_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Inbox']");
	private static final By FAQ_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='FAQ']");
	private static final By MY_REWARDS_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='My Rewards']");
	private static final By GAME_HISTORY_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Game History']");
	private static final By HOW_TO_PLAY_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='How to play']");
	private static final By RESPONSIBLE_PLAY_BUTTON = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Responsible Play']");

	// Additional locators for comprehensive menu navigation
	private static final By PROFILE_MENU = By.id("com.paytm.paytmplay:id/header_tv_nickname");
	private static final By MY_BALANCE_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='My Balance']");
	private static final By REFER_EARN_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Refer & Earn']");
	private static final By PAYMENT_SETTINGS_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Payment Settings']");
	private static final By INBOX_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Inbox']");
	private static final By FAQ_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='FAQ']");
	private static final By MY_REWARDS_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='My Rewards']");
	private static final By GAME_HISTORY_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Game History']");
	private static final By HOW_TO_PLAY_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='How to play']");
	private static final By RESPONSIBLE_PLAY_MENU = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle' and @text='Responsible Play']");

	// Verification locators
	private static final By PROFILE_VERIFY = By.xpath("//android.view.ViewGroup[@resource-id='com.paytm.paytmplay:id/pip_container']");
	private static final By MY_BALANCE_VERIFY = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/passbook_add_money']");
	private static final By REFER_EARN_VERIFY = By.xpath("//android.widget.TextView[@text='Invite via WhatsApp']");
	private static final By PAYMENT_SETTINGS_VERIFY = By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/tv_title' and @text='Manage Payment Methods']");
	private static final By INBOX_VERIFY = By.id("com.paytm.paytmplay:id/browser_webView_container");
	private static final By MY_REWARDS_VERIFY = By.xpath("//android.widget.TextView[@text='My Bonus']");
	private static final By GAME_HISTORY_VERIFY = By.xpath("//android.widget.FrameLayout[@resource-id='com.paytm.paytmplay:id/cocos_task_center_container']");
	private static final By HOW_TO_PLAY_VERIFY = By.xpath("//android.widget.FrameLayout[@resource-id='com.paytm.paytmplay:id/cocos_task_center_container']");
	private static final By RESPONSIBLE_PLAY_VERIFY = By.xpath("//android.widget.FrameLayout[@resource-id='com.paytm.paytmplay:id/cocos_task_center_container']");

	public HamburgerMenuCallbreak(AndroidDriver driver) {
		this.driver = driver;
		this.deviceId = String.format("%s_%s", 
			driver.getCapabilities().getCapability("deviceName"),
			driver.getCapabilities().getCapability("platformVersion"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
		this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_TIMEOUT));
		this.longWait = new WebDriverWait(driver, Duration.ofSeconds(LONG_TIMEOUT));
		PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(WAIT_TIMEOUT)), this);
		TestReporter.logPageTransition(deviceId, "CallbreakLobby", "HamburgerMenu");
	}

	// Add a new method to open menu when needed
	public void initializeMenu() {
		TestReporter.startStep(deviceId, "Initialize Menu");
		try {
			// Wait for app to be fully loaded
			Thread.sleep(3000);
			
			// Try multiple times to open the menu
			for (int i = 0; i < 3; i++) {
				try {
					TestReporter.logAction(deviceId, "Attempt " + (i + 1), "Trying to find and click hamburger menu", driver);
					
					// Try all hamburger menu locators with longer wait
					WebElement menuButton = null;
					try {
						menuButton = longWait.until(ExpectedConditions.elementToBeClickable(
							CallbreakLobby.HAMBURGER_MENU
						));
					} catch (Exception e) {
						TestReporter.logAction(deviceId, "Warning", "First hamburger menu locator failed, trying second", driver);
						try {
							menuButton = longWait.until(ExpectedConditions.elementToBeClickable(
								CallbreakLobby.HAMBURGER_MENU_SECOND
							));
						} catch (Exception e2) {
							TestReporter.logAction(deviceId, "Warning", "Second hamburger menu locator failed, trying third", driver);
							menuButton = longWait.until(ExpectedConditions.elementToBeClickable(
								CallbreakLobby.HAMBURGER_MENU_THIRD
							));
						}
					}
					
					// Ensure element is visible and clickable
					if (!menuButton.isDisplayed()) {
						TestReporter.logAction(deviceId, "Warning", "Menu button not visible, waiting...", driver);
						Thread.sleep(2000);
						continue;
					}
					
					// Click the menu with retry
					try {
						menuButton.click();
					} catch (Exception e) {
						TestReporter.logAction(deviceId, "Warning", "First click attempt failed, trying JavaScript click", driver);
						driver.executeScript("arguments[0].click();", menuButton);
					}
					
					// Wait for menu animation
					Thread.sleep(3000);
					
					// Verify menu is open
					if (isMenuOpen()) {
						TestReporter.logAction(deviceId, "Success", "Menu initialized successfully", driver);
						return;
					}
					
					TestReporter.logAction(deviceId, "Warning", "Menu not open after click, retrying...", driver);
					Thread.sleep(3000); // Wait before retry
					
				} catch (Exception e) {
					if (i == 2) throw e; // Throw on last attempt
					TestReporter.logAction(deviceId, "Warning", "Attempt " + (i + 1) + " failed: " + e.getMessage(), driver);
					Thread.sleep(3000); // Wait before retry
				}
			}
			
			throw new IllegalStateException("Menu not properly opened after multiple attempts");
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to initialize menu", e);
			throw new IllegalStateException("Failed to initialize menu: " + e.getMessage(), e);
		} finally {
			TestReporter.endStep(deviceId, "Initialize Menu");
		}
	}

	private void waitForPageLoad() {
		TestReporter.startStep(deviceId, "Wait for Page Load");
		try {
			// Try multiple times to find the hamburger menu
			for (int i = 0; i < 3; i++) {
				try {
					// Wait for either hamburger menu button to be present
					longWait.until(ExpectedConditions.or(
						ExpectedConditions.presenceOfElementLocated(CallbreakLobby.HAMBURGER_MENU),
						ExpectedConditions.presenceOfElementLocated(CallbreakLobby.HAMBURGER_MENU_SECOND)
					));
					TestReporter.logAction(deviceId, "Success", "Page loaded successfully", driver);
					// Take screenshot of the loaded page
					try {
						String screenshotPath = AppiumUtils.captureScreenshot(driver, deviceId, "Page_Loaded");
						if (screenshotPath != null) {
							TestReporter.logAction(deviceId, "Screenshot", "Page load screenshot captured", driver);
						}
					} catch (Exception e) {
						TestReporter.logAction(deviceId, "Warning", "Failed to capture page load screenshot: " + e.getMessage(), driver);
					}
					return;
				} catch (Exception e) {
					if (i == 2) throw e; // Throw on last attempt
					TestReporter.logAction(deviceId, "Warning", "Attempt " + (i + 1) + " to find hamburger menu failed, retrying...", driver);
					Thread.sleep(1000); // Wait before retry
				}
			}
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Page load timeout", e);
			throw new IllegalStateException("Page load timeout: " + e.getMessage(), e);
		} finally {
			TestReporter.endStep(deviceId, "Wait for Page Load");
		}
	}

	public boolean isMenuOpen() {
		try {
			// Try multiple menu elements to verify menu is open
			List<By> menuElements = Arrays.asList(
				// Core menu elements that should always be present
				SETTINGS_BUTTON,
				MY_BALANCE_BUTTON,
				// Additional menu elements for redundancy
				REFER_EARN_BUTTON,
				PAYMENT_SETTINGS_BUTTON,
				INBOX_BUTTON,
				FAQ_BUTTON,
				MY_REWARDS_BUTTON,
				GAME_HISTORY_BUTTON,
				HOW_TO_PLAY_BUTTON,
				RESPONSIBLE_PLAY_BUTTON
			);
			
			// Try each element with a short wait
			for (By element : menuElements) {
				try {
					if (shortWait.until(ExpectedConditions.visibilityOfElementLocated(element)).isDisplayed()) {
						return true;
					}
				} catch (Exception e) {
					continue; // Try next element
				}
			}
			
			// If we get here, none of the elements were found
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void verifyMenuOpen() {
		TestReporter.startStep(deviceId, "Verify Menu Open");
		try {
			// Wait for menu animation to complete
			Thread.sleep(500);
			
			// Try multiple times to verify menu is open
			for (int i = 0; i < 3; i++) {
				if (isMenuOpen()) {
					TestReporter.logAction(deviceId, "Success", "Menu is open and verified", driver);
					return;
				}
				Thread.sleep(1000); // Wait before retry
			}
			
			throw new IllegalStateException("Menu not properly opened after multiple verification attempts");
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Menu not properly opened", e);
			throw new IllegalStateException("Menu not properly opened: " + e.getMessage(), e);
		} finally {
			TestReporter.endStep(deviceId, "Verify Menu Open");
		}
	}
	
	public SettingPage openSettings() {
		TestReporter.startStep(deviceId, "Open Settings");
		try {
			// Try short wait first for clickable
			try {
				TestReporter.logAction(deviceId, "Click", "Clicking settings button", driver);
				shortWait.until(ExpectedConditions.elementToBeClickable(SETTINGS_BUTTON)).click();
			} catch (Exception e) {
				// If short wait fails, try normal wait
				wait.until(ExpectedConditions.elementToBeClickable(SETTINGS_BUTTON)).click();
			}
			TestReporter.logAction(deviceId, "Success", "Settings button clicked successfully", driver);
			return new SettingPage(driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open settings", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Settings");
		}
	}

	public void openProfile() {
		TestReporter.startStep(deviceId, "Open Profile");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking profile button", driver);
			driver.findElement(PROFILE_BUTTON).click();
			TestReporter.logAction(deviceId, "Success", "Profile button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open profile", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Profile");
		}
	}

	public MyBalancePage openMyBalance() throws InterruptedException {
		TestReporter.startStep(deviceId, "Open My Balance");
		try {
			// Wait for menu to be fully opened
			Thread.sleep(1000);
			
			// Debug: Capture screenshot and log page source to see what's available
			try {
				String screenshotPath = AppiumUtils.captureScreenshot(driver, deviceId, "HamburgerMenu_Debug");
				TestReporter.logAction(deviceId, "Debug", "Hamburger menu screenshot captured: " + screenshotPath, driver);
				
				// Log all menu elements with the same resource-id to see what's available
				List<WebElement> menuItems = driver.findElements(By.xpath("//android.widget.TextView[@resource-id='com.paytm.paytmplay:id/item_tv_subtitle']"));
				TestReporter.logAction(deviceId, "Debug", "Found " + menuItems.size() + " menu items with resource-id 'item_tv_subtitle'", driver);
				
				for (int i = 0; i < menuItems.size(); i++) {
					try {
						String text = menuItems.get(i).getText();
						TestReporter.logAction(deviceId, "Debug", "Menu item " + (i + 1) + ": '" + text + "'", driver);
					} catch (Exception e) {
						TestReporter.logAction(deviceId, "Debug", "Menu item " + (i + 1) + ": Could not get text", driver);
					}
				}
			} catch (Exception e) {
				TestReporter.logAction(deviceId, "Warning", "Failed to capture debug info: " + e.getMessage(), driver);
			}
			
			// Try multiple strategies to find and click the My Balance button
			Exception lastException = null;
			for (int i = 0; i < 3; i++) {
				try {
					TestReporter.logAction(deviceId, "Attempt " + (i + 1), "Trying to find and click My Balance button", driver);
					
					// Verify session is still active
					try {
						driver.getPageSource();
					} catch (Exception e) {
						TestReporter.logAction(deviceId, "Warning", "Session appears to be lost, attempting to recover", driver);
						// Try to reinitialize the menu
						initializeMenu();
						Thread.sleep(2000);
					}
					
					// Wait for element to be present and visible with shorter timeout
					WebElement myBalanceBtn = wait.until(ExpectedConditions.presenceOfElementLocated(MY_BALANCE_BUTTON));
					if (!myBalanceBtn.isDisplayed()) {
						// If not visible, try scrolling to it
						driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
							"left", 100, "top", 100, "width", 600, "height", 600,
							"direction", "down", "percent", 0.75
						));
						Thread.sleep(500);
					}
					
					// Try to click the button with JavaScript as fallback
					try {
						myBalanceBtn.click();
					} catch (Exception e) {
						TestReporter.logAction(deviceId, "Warning", "Regular click failed, trying JavaScript click", driver);
						driver.executeScript("arguments[0].click();", myBalanceBtn);
					}
					
					TestReporter.logAction(deviceId, "Success", "My balance button clicked successfully", driver);
					
					// Wait for page transition and return MyBalancePage object
					Thread.sleep(3000);
					return new MyBalancePage(driver);
					
				} catch (Exception e) {
					lastException = e;
					TestReporter.logAction(deviceId, "Warning", "Attempt " + (i + 1) + " failed: " + e.getMessage(), driver);
					
					// Try to recover the session
					try {
						driver.getPageSource();
					} catch (Exception sessionEx) {
						TestReporter.logAction(deviceId, "Error", "Session lost during attempt " + (i + 1), driver);
						throw new RuntimeException("Session lost: " + sessionEx.getMessage(), sessionEx);
					}
					
					Thread.sleep(2000); // Increased wait between retries
				}
			}
			
			// If we get here, all attempts failed
			throw new NoSuchElementException("Failed to find and click My Balance button using all available strategies: " + lastException.getMessage());
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open my balance", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open My Balance");
		}
	}

	public void openReferEarn() {
		TestReporter.startStep(deviceId, "Open Refer & Earn");
		try {
			// Verify menu is open
			verifyMenuOpen();
			
			// Try short wait first for clickable
			try {
				TestReporter.logAction(deviceId, "Click", "Clicking Refer & Earn button", driver);
				shortWait.until(ExpectedConditions.elementToBeClickable(REFER_EARN_BUTTON)).click();
			} catch (Exception e) {
				// If short wait fails, try normal wait
				wait.until(ExpectedConditions.elementToBeClickable(REFER_EARN_BUTTON)).click();
			}
			
			// Wait for page load
			waitForPageLoad();
			TestReporter.logAction(deviceId, "Success", "Refer & Earn page opened successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open Refer & Earn", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Refer & Earn");
		}
	}

	public void openPaymentSettings() {
		TestReporter.startStep(deviceId, "Open Payment Settings");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking payment settings button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(PAYMENT_SETTINGS_BUTTON)).click();
			TestReporter.logAction(deviceId, "Success", "Payment settings button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open payment settings", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Payment Settings");
		}
	}

	public void openInbox() {
		TestReporter.startStep(deviceId, "Open Inbox");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking inbox button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(INBOX_BUTTON)).click();
			TestReporter.logAction(deviceId, "Success", "Inbox button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open inbox", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Inbox");
		}
	}

	public void openFAQ() {
		TestReporter.startStep(deviceId, "Open FAQ");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking FAQ button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(FAQ_BUTTON)).click();
			TestReporter.logAction(deviceId, "Success", "FAQ button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open FAQ", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open FAQ");
		}
	}

	public void openMyRewards() {
		TestReporter.startStep(deviceId, "Open My Rewards");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking my rewards button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(MY_REWARDS_BUTTON)).click();
			TestReporter.logAction(deviceId, "Success", "My rewards button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open my rewards", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open My Rewards");
		}
	}

	public void openGameHistory() {
		TestReporter.startStep(deviceId, "Open Game History");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking game history button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(GAME_HISTORY_BUTTON)).click();
			TestReporter.logAction(deviceId, "Success", "Game history button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open game history", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Game History");
		}
	}

	public void openHowToPlay() {
		TestReporter.startStep(deviceId, "Open How to Play");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking how to play button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(HOW_TO_PLAY_BUTTON)).click();
			TestReporter.logAction(deviceId, "Success", "How to play button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open how to play", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open How to Play");
		}
	}

	public void openResponsiblePlay() {
		TestReporter.startStep(deviceId, "Open Responsible Play");
		try {
			TestReporter.logAction(deviceId, "Click", "Clicking responsible play button", driver);
			wait.until(ExpectedConditions.elementToBeClickable(RESPONSIBLE_PLAY_BUTTON)).click();
			TestReporter.logAction(deviceId, "Success", "Responsible play button clicked successfully", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open responsible play", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Responsible Play");
		}
	}

	/**
	 * Scroll to find an element if not immediately visible
	 */
	private void scrollToElement(By elementLocator) throws InterruptedException {
		TestReporter.startStep(deviceId, "Scroll to Element");
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
			if (!element.isDisplayed()) {
				TestReporter.logAction(deviceId, "Scroll", "Element not visible, scrolling down", driver);
				driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
					"left", 100, "top", 100, "width", 600, "height", 600,
					"direction", "down", "percent", 0.75
				));
				Thread.sleep(500);
			}
		} catch (Exception e) {
			TestReporter.logAction(deviceId, "Scroll", "Element not found, scrolling down", driver);
			driver.executeScript("mobile: scrollGesture", ImmutableMap.of(
				"left", 100, "top", 100, "width", 600, "height", 600,
				"direction", "down", "percent", 0.75
			));
			Thread.sleep(500);
		} finally {
			TestReporter.endStep(deviceId, "Scroll to Element");
		}
	}

	/**
	 * Navigate to Profile section
	 */
	public void navigateToProfile() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to Profile");
		try {
			scrollToElement(PROFILE_MENU);
			WebElement profileMenu = wait.until(ExpectedConditions.elementToBeClickable(PROFILE_MENU));
			profileMenu.click();
			
			// Verify Profile page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(PROFILE_VERIFY));
			TestReporter.logAction(deviceId, "Success", "Profile page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to Profile", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to Profile");
		}
	}

	/**
	 * Navigate to My Balance section
	 */
	public void navigateToMyBalance() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to My Balance");
		try {
			scrollToElement(MY_BALANCE_MENU);
			WebElement myBalanceMenu = wait.until(ExpectedConditions.elementToBeClickable(MY_BALANCE_MENU));
			myBalanceMenu.click();
			
			// Verify My Balance page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(MY_BALANCE_VERIFY));
			TestReporter.logAction(deviceId, "Success", "My Balance page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to My Balance", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to My Balance");
		}
	}

	/**
	 * Navigate to Refer & Earn section
	 */
	public void navigateToReferEarn() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to Refer & Earn");
		try {
			scrollToElement(REFER_EARN_MENU);
			WebElement referEarnMenu = wait.until(ExpectedConditions.elementToBeClickable(REFER_EARN_MENU));
			referEarnMenu.click();
			
			// Verify Refer & Earn page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(REFER_EARN_VERIFY));
			TestReporter.logAction(deviceId, "Success", "Refer & Earn page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to Refer & Earn", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to Refer & Earn");
		}
	}

	/**
	 * Navigate to Payment Settings section
	 */
	public void navigateToPaymentSettings() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to Payment Settings");
		try {
			scrollToElement(PAYMENT_SETTINGS_MENU);
			WebElement paymentSettingsMenu = wait.until(ExpectedConditions.elementToBeClickable(PAYMENT_SETTINGS_MENU));
			paymentSettingsMenu.click();
			
			// Verify Payment Settings page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(PAYMENT_SETTINGS_VERIFY));
			TestReporter.logAction(deviceId, "Success", "Payment Settings page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to Payment Settings", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to Payment Settings");
		}
	}

	/**
	 * Navigate to Inbox section
	 */
	public void navigateToInbox() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to Inbox");
		try {
			scrollToElement(INBOX_MENU);
			WebElement inboxMenu = wait.until(ExpectedConditions.elementToBeClickable(INBOX_MENU));
			inboxMenu.click();
			
			// Verify Inbox page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(INBOX_VERIFY));
			TestReporter.logAction(deviceId, "Success", "Inbox page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to Inbox", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to Inbox");
		}
	}

	/**
	 * Navigate to FAQ section
	 */
	public void navigateToFAQ() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to FAQ");
		try {
			scrollToElement(FAQ_MENU);
			WebElement faqMenu = wait.until(ExpectedConditions.elementToBeClickable(FAQ_MENU));
			faqMenu.click();
			
			// Verify FAQ page is loaded (check for any of the expected elements)
			wait.until(ExpectedConditions.or(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='Topics']")),
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='GST']")),
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='Withdraw']"))
			));
			TestReporter.logAction(deviceId, "Success", "FAQ page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to FAQ", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to FAQ");
		}
	}

	/**
	 * Navigate to My Rewards section
	 */
	public void navigateToMyRewards() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to My Rewards");
		try {
			scrollToElement(MY_REWARDS_MENU);
			WebElement myRewardsMenu = wait.until(ExpectedConditions.elementToBeClickable(MY_REWARDS_MENU));
			myRewardsMenu.click();
			
			// Verify My Rewards page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(MY_REWARDS_VERIFY));
			TestReporter.logAction(deviceId, "Success", "My Rewards page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to My Rewards", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to My Rewards");
		}
	}

	/**
	 * Navigate to Game History section
	 */
	public void navigateToGameHistory() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to Game History");
		try {
			scrollToElement(GAME_HISTORY_MENU);
			WebElement gameHistoryMenu = wait.until(ExpectedConditions.elementToBeClickable(GAME_HISTORY_MENU));
			gameHistoryMenu.click();
			
			// Verify Game History page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(GAME_HISTORY_VERIFY));
			TestReporter.logAction(deviceId, "Success", "Game History page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to Game History", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to Game History");
		}
	}

	/**
	 * Navigate to How to Play section
	 */
	public void navigateToHowToPlay() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to How to Play");
		try {
			scrollToElement(HOW_TO_PLAY_MENU);
			WebElement howToPlayMenu = wait.until(ExpectedConditions.elementToBeClickable(HOW_TO_PLAY_MENU));
			howToPlayMenu.click();
			
			// Verify How to Play page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(HOW_TO_PLAY_VERIFY));
			TestReporter.logAction(deviceId, "Success", "How to Play page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to How to Play", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to How to Play");
		}
	}

	/**
	 * Navigate to Responsible Play section
	 */
	public void navigateToResponsiblePlay() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate to Responsible Play");
		try {
			scrollToElement(RESPONSIBLE_PLAY_MENU);
			WebElement responsiblePlayMenu = wait.until(ExpectedConditions.elementToBeClickable(RESPONSIBLE_PLAY_MENU));
			responsiblePlayMenu.click();
			
			// Verify Responsible Play page is loaded
			wait.until(ExpectedConditions.visibilityOfElementLocated(RESPONSIBLE_PLAY_VERIFY));
			TestReporter.logAction(deviceId, "Success", "Responsible Play page loaded successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate to Responsible Play", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate to Responsible Play");
		}
	}

	/**
	 * Navigate back to previous screen
	 */
	public void navigateBack() throws InterruptedException {
		TestReporter.startStep(deviceId, "Navigate Back");
		try {
			driver.navigate().back();
			Thread.sleep(1000); // Wait for navigation to complete
			TestReporter.logAction(deviceId, "Success", "Successfully navigated back", driver);
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to navigate back", e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Navigate Back");
		}
	}
} 