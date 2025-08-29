package org.fg.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Instant;
import java.time.Duration;
import io.appium.java_client.android.AndroidDriver;
import org.fg.utils.AppiumUtils;
import java.util.concurrent.ConcurrentHashMap;

public class TestReporter {
    private static final ExtentReports extent = ExtentReporterNG.getReporterObject();
    private static final ConcurrentHashMap<String, Instant> actionStartTimes = new ConcurrentHashMap<>();
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    private static ExtentTest currentTest = null;

    private static String formatDeviceId(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return "UNKNOWN";
        }
        
        // Handle LambdaTest session IDs
        if (deviceId.startsWith("RMAA-AND-") || deviceId.matches("^[A-Z0-9]{10,}$")) {
            String deviceName = System.getProperty("deviceName");
            String platformVersion = System.getProperty("platformVersion");
            String sessionId = System.getProperty("sessionId");
            
            if (deviceName != null && platformVersion != null) {
                return String.format("LambdaTest [%s] - %s (Android %s)", 
                    sessionId != null ? sessionId : "Unknown Session",
                    deviceName,
                    platformVersion);
            }
            return "LambdaTest Device";
        }
        
        return deviceId;
    }

    private static void logToConsole(String deviceId, String type, String message) {
        String timestamp = TIME_FORMAT.format(new Date());
        String formattedDeviceId = formatDeviceId(deviceId);
        String sessionId = System.getProperty("sessionId");
        
        // Format for LambdaTest
        if (sessionId != null) {
            System.out.printf("[%s] [LambdaTest Session: %s] [%s] [%s] %s%n", 
                timestamp, sessionId, formattedDeviceId, type, message);
        } else {
            System.out.printf("[%s] [%s] [%s] %s%n", 
                timestamp, formattedDeviceId, type, message);
        }
    }

    public static void startTest(String deviceId, String testName) {
        String sessionId = System.getProperty("sessionId");
        if (sessionId != null) {
            logToConsole(deviceId, "LAMBDATEST", "Session Started: " + sessionId);
        }
        logToConsole(deviceId, "TEST", "Starting test: " + testName);
        currentTest = null;
    }

    public static void logStep(String deviceId, String stepName, String description) {
        logToConsole(deviceId, "STEP", stepName + ": " + description);
    }

    public static void startStep(String deviceId, String stepName) {
        actionStartTimes.put(deviceId, Instant.now());
        logToConsole(deviceId, "STEP", "Starting: " + stepName);
    }

    public static void endStep(String deviceId, String stepName) {
        Instant startTime = actionStartTimes.get(deviceId);
        String duration = getDuration(startTime);
        logToConsole(deviceId, "STEP", "Completed: " + stepName + " (Duration: " + duration + ")");
    }

    private static String getDuration(Instant startTime) {
        if (startTime == null) return "0ms";
        Duration duration = Duration.between(startTime, Instant.now());
        long millis = duration.toMillis();
        if (millis < 1000) {
            return millis + "ms";
        } else {
            return String.format("%.2fs", millis / 1000.0);
        }
    }

    public static void logAction(String deviceId, String action, String details, AndroidDriver driver) {
        // Log to console with LambdaTest session info
        logToConsole(deviceId, action, details);

        // Log to extent report
        if (currentTest == null) {
            String formattedDeviceId = formatDeviceId(deviceId);
            String sessionId = System.getProperty("sessionId");
            String testName = sessionId != null ? 
                "LambdaTest Session: " + sessionId : 
                "Menu Navigation Test";
            currentTest = extent.createTest(formattedDeviceId + " - " + testName);
            currentTest.assignCategory("Android");
        }

        ExtentTest actionNode = currentTest.createNode(action);
        actionNode.log(Status.INFO, MarkupHelper.createLabel(details, ExtentColor.BLUE));

        // Capture screenshots for LambdaTest
        if (driver != null && (action.equals("Success") || action.equals("Error") || 
            action.equals("Screenshot") || action.equals("Click"))) {
            try {
                String screenshotPath = AppiumUtils.captureScreenshot(driver, deviceId, action);
                if (screenshotPath != null) {
                    actionNode.addScreenCaptureFromPath(screenshotPath);
                }
            } catch (Exception e) {
                actionNode.log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    private static boolean shouldCaptureScreenshot(String action, String details) {
        if (action.equals("Verify")) {
            return true;
        }
        
        if (action.equals("Success") || action.equals("Error")) {
            return true;
        }
        
        String lowerDetails = details.toLowerCase();
        return lowerDetails.contains("login") ||
               lowerDetails.contains("logout") ||
               lowerDetails.contains("error") ||
               lowerDetails.contains("failed") ||
               lowerDetails.contains("exception") ||
               lowerDetails.contains("navigation") ||
               lowerDetails.contains("menu");
    }

    private static boolean isSignificantAction(String actionType) {
        return actionType.equals("Click") || 
               actionType.equals("Input") || 
               actionType.equals("Verify") || 
               actionType.equals("Navigation") ||
               actionType.equals("Test Start") ||
               actionType.equals("Test Success") ||
               actionType.equals("Test Failure");
    }

    public static void logError(String deviceId, String message, Throwable error) {
        String sessionId = System.getProperty("sessionId");
        if (sessionId != null) {
            logToConsole(deviceId, "LAMBDATEST", "Error in Session: " + sessionId);
        }
        logToConsole(deviceId, "ERROR", message + (error != null ? ": " + error.getMessage() : ""));

        if (currentTest != null) {
            currentTest.log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
            if (error != null) {
                currentTest.log(Status.FAIL, error);
            }
        }
    }

    public static void logTestResult(String deviceId, String testName, boolean passed, String message) {
        String sessionId = System.getProperty("sessionId");
        if (sessionId != null) {
            logToConsole(deviceId, "LAMBDATEST", 
                String.format("Session %s: %s", sessionId, passed ? "PASSED" : "FAILED"));
        }
        logToConsole(deviceId, "RESULT", testName + ": " + (passed ? "PASSED" : "FAILED") + " - " + message);
    }

    public static void logPageTransition(String deviceId, String fromPage, String toPage) {
        logToConsole(deviceId, "NAVIGATION", String.format("Page Transition: %s -> %s", fromPage, toPage));
    }

    private static ExtentTest ensureDeviceReport(String deviceId) {
        if (currentTest == null) {
            String formattedDeviceId = formatDeviceId(deviceId);
            currentTest = extent.createTest(formattedDeviceId + " - Login to Logout Flow");
            currentTest.assignCategory("Android");
        }
        return currentTest;
    }

    public static void saveReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void clearReports() {
        actionStartTimes.clear();
        currentTest = null;
    }
} 