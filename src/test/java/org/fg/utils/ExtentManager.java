package org.fg.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestResult;
import java.io.File;

public class ExtentManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    private static void createInstance() {
        extent = new ExtentReports();
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Appium Test Report");
        sparkReporter.config().setReportName("Mobile Automation Test Report");
        sparkReporter.config().setTimelineEnabled(true);
        sparkReporter.config().setOfflineMode(true);
        extent.attachReporter(sparkReporter);
    }

    public static void startTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
    }

    public static void endTest() {
        extent.flush();
    }

    public static void addSystemInfo(String key, String value) {
        extent.setSystemInfo(key, value);
    }

    public static void addTestResult(ITestResult result) {
        if (test.get() == null) return;
        
        if (result.getStatus() == ITestResult.FAILURE) {
            test.get().fail("Test Failed: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.get().pass("Test Passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.get().skip("Test Skipped: " + result.getThrowable());
        }
    }
} 