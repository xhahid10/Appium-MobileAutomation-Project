package org.fg.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Duration;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.android.AndroidDriver;
import org.fg.utils.TestReporter;

public class AppiumUtils {
	
	//common android and ios function for actions. This class can be inherit to other android and ios action class .
	
	
	//we can use javascript executor for going to any page by using activity name

	
	private static final int DEFAULT_WAIT_SECONDS = 30;
	
	public static void waitForElementToBeClickable(WebElement element, AndroidDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_SECONDS));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}
	
	public static void waitForElementToBeVisible(WebElement element, AndroidDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_SECONDS));
		wait.until(ExpectedConditions.visibilityOf(element));
	}
	
	public static void waitForElementToBePresent(WebElement element, AndroidDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_SECONDS));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Open deep link in the app
	 * @param driver - Android driver instance
	 * @param deepLink - Deep link URL to open
	 * @param deviceId - Device identifier for logging
	 */
	public static void openDeepLink(AndroidDriver driver, String deepLink, String deviceId) {
		TestReporter.startStep(deviceId, "Open Deep Link");
		try {
			TestReporter.logAction(deviceId, "Info", "Opening deep link: " + deepLink, driver);
			
			// Open deep link using Appium's deep link capability
			driver.get(deepLink);
			
			TestReporter.logAction(deviceId, "Success", "Deep link opened successfully", driver);
			
		} catch (Exception e) {
			TestReporter.logError(deviceId, "Failed to open deep link: " + e.getMessage(), e);
			throw e;
		} finally {
			TestReporter.endStep(deviceId, "Open Deep Link");
		}
	}

	/**
	 * Wait for app to load after deep link navigation
	 * @param driver - Android driver instance
	 * @param deviceId - Device identifier for logging
	 * @param timeoutSeconds - Timeout in seconds
	 */
	public static void waitForAppLoadAfterDeepLink(AndroidDriver driver, String deviceId, int timeoutSeconds) {
		TestReporter.startStep(deviceId, "Wait for App Load After Deep Link");
		try {
			TestReporter.logAction(deviceId, "Wait", "Waiting for app to load after deep link navigation (" + timeoutSeconds + "s)", driver);
			
			// Wait for the app to stabilize after deep link navigation
			Thread.sleep(timeoutSeconds * 1000);
			
			TestReporter.logAction(deviceId, "Success", "App load wait completed", driver);
			
		} catch (InterruptedException e) {
			TestReporter.logError(deviceId, "Wait interrupted: " + e.getMessage(), e);
			Thread.currentThread().interrupt();
		} finally {
			TestReporter.endStep(deviceId, "Wait for App Load After Deep Link");
		}
	}

	public String getScreenshot(String testCaseName, AndroidDriver driver) throws IOException {
		// Get current timestamp
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		
		// Capture screenshot as a file
		File source = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		// Create target file path
		String destinationFile = System.getProperty("user.dir") + "/reports/" + testCaseName + "_" + timestamp + ".png";
		
		// Copy screenshot to target location
		FileUtils.copyFile(source, new File(destinationFile));
		
		return destinationFile;
	}

	public static String captureScreenshot(WebDriver driver, String deviceId, String testName) {
		try {
			// Get the current report path from ExtentReporterNG
			String currentReportPath = ExtentReporterNG.getCurrentReportPath();
			String screenshotsDir = currentReportPath + "/screenshots";
			
			// Create directory if it doesn't exist
			File dir = new File(screenshotsDir);
			if (!dir.exists()) {
				boolean created = dir.mkdirs();
				if (!created) {
					System.err.println("Failed to create screenshots directory: " + screenshotsDir);
					return null;
				}
			}

			// Generate timestamp
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			
			// Create screenshot file name with shorter format
			String fileName = String.format("%s_%s.png", 
				testName.replaceAll("[^a-zA-Z0-9.-]", "_"),
				timestamp);
			
			// Take screenshot directly to target file
			File targetFile = new File(screenshotsDir + File.separator + fileName);
			((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).renameTo(targetFile);
			
			// Verify the file was created successfully
			if (!targetFile.exists() || targetFile.length() == 0) {
				System.err.println("Failed to save screenshot: " + targetFile.getAbsolutePath());
				return null;
			}
			
			// Return relative path for Extent Report
			return "./screenshots/" + fileName;
		} catch (Exception e) {
			System.err.println("Failed to capture screenshot: " + e.getMessage());
			return null;
		}
	}
}
