package org.fg.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.appium.java_client.android.AndroidDriver;
import java.util.concurrent.ConcurrentHashMap;
import org.pfg.BaseTest;
import java.util.Set;
import java.time.Instant;
import java.time.Duration;
import java.io.File;

public class Listeners extends BaseTest implements ITestListener {
    private final ExtentReports extent = ExtentReporterNG.getReporterObject();
    private final ConcurrentHashMap<String, ExtentTest> testMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> testStartTimes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> loggedMessages = new ConcurrentHashMap<>();
    private static final long MESSAGE_EXPIRY_MS = 1000; // 1 second expiry for duplicate messages
    
    @Override
    public void onTestStart(ITestResult result) {
        if (result == null || result.getMethod() == null) {
            TestReporter.logError("UNKNOWN", "Invalid test result", 
                new IllegalArgumentException("Test result or method is null"));
            return;
        }
        
        String testName = result.getMethod().getMethodName();
        String deviceId = getDeviceId(result);
        
        // Initialize test in TestReporter
        TestReporter.startTest(deviceId, testName);
        TestReporter.logAction(deviceId, "Test Start", "Starting test: " + testName, getDriverFromResult(result));
        
        // Store test start time
        testStartTimes.put(testName, Instant.now());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        if (result == null) return;
        
        String testName = result.getMethod().getMethodName();
        String deviceId = getDeviceId(result);
        
        // Log test success
        TestReporter.logAction(deviceId, "Test Success", "Test passed: " + testName, getDriverFromResult(result));
        
        // Calculate and log test duration
        Instant startTime = testStartTimes.get(testName);
        if (startTime != null) {
            Duration duration = Duration.between(startTime, Instant.now());
            TestReporter.logAction(deviceId, "Test Duration", 
                String.format("Test completed in %dms", duration.toMillis()), null);
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        if (result == null) return;
        
        String testName = result.getMethod().getMethodName();
        String deviceId = getDeviceId(result);
        Throwable error = result.getThrowable();
        
        // Log test failure
        TestReporter.logError(deviceId, "Test failed: " + testName, error);
        
        // Capture failure screenshot
        try {
            AndroidDriver driver = getDriverFromResult(result);
            if (driver != null) {
                String screenshotPath = AppiumUtils.captureScreenshot(driver, deviceId, "Test_Failure");
                if (screenshotPath != null) {
                    TestReporter.logAction(deviceId, "Screenshot", "Failure screenshot captured", driver);
                }
            }
        } catch (Exception e) {
            TestReporter.logError(deviceId, "Failed to capture failure screenshot", e);
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        if (result == null) return;
        
        String testName = result.getMethod().getMethodName();
        String deviceId = getDeviceId(result);
        
        TestReporter.logAction(deviceId, "Test Skipped", "Test was skipped: " + testName, null);
    }
    
    @Override
    public void onStart(ITestContext context) {
        if (context == null) {
            TestReporter.logError("UNKNOWN", "Invalid test context", 
                new IllegalArgumentException("Test context is null"));
            return;
        }
        
        TestReporter.logAction("SUITE", "Suite Start", 
            "Starting test suite: " + context.getName(), null);
    }
    
    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            // Flush the report
            TestReporter.saveReports();
            
            // Log completion
            String deviceId = getDeviceId(context);
            TestReporter.logAction(deviceId, "SUITE", 
                "Finished test suite: " + context.getName(), null);
            
            // Print report location
            System.out.println("\n===============================================");
            System.out.println("Test Report has been generated at:");
            System.out.println("file://" + new File(ExtentReporterNG.getCurrentReportPath() + "/index.html").getAbsolutePath());
            System.out.println("===============================================\n");
        }
    }
    
    private String getDeviceId(ITestResult result) {
        if (result == null) return "UNKNOWN";
        
        try {
            if (result.getInstance() instanceof BaseTest) {
                return ((BaseTest) result.getInstance()).getDeviceId();
            }
        } catch (Exception e) {
            System.err.println("Failed to get device ID from test result: " + e.getMessage());
        }
        return "UNKNOWN";
    }
    
    private String getDeviceId(ITestContext context) {
        if (context == null) return "UNKNOWN";
        
        try {
            Object[] instances = context.getAllTestMethods()[0].getInstance().getClass().getDeclaredFields();
            for (Object instance : instances) {
                if (instance instanceof BaseTest) {
                    return ((BaseTest) instance).getDeviceId();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get device ID from test context: " + e.getMessage());
        }
        return "UNKNOWN";
    }
    
    private AndroidDriver getDriverFromResult(ITestResult result) {
        if (result == null) return null;
        
        try {
            if (result.getInstance() instanceof BaseTest) {
                return ((BaseTest) result.getInstance()).getDriver();
            }
        } catch (Exception e) {
            System.err.println("Failed to get driver from test result: " + e.getMessage());
        }
        return null;
    }
}
 