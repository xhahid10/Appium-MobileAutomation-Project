package org.fg.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ExtentReporterNG {
    private static ExtentReports extent;
    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String REPORTS_PATH = BASE_PATH + "/reports";
    private static final String SCREENSHOTS_PATH = REPORTS_PATH + "/screenshots";
    private static final String LOGS_PATH = REPORTS_PATH + "/logs";
    private static final String ARCHIVE_PATH = REPORTS_PATH + "/archive";
    private static final String OLD_REPORTS_PATH = REPORTS_PATH + "/old_reports";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private static final SimpleDateFormat DATE_FOLDER_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat HOUR_FOLDER_FORMAT = new SimpleDateFormat("HH");
    private static final long MAX_REPORT_AGE_HOURS = 48; // Keep reports for 48 hours
    private static final long MAX_SCREENSHOT_AGE_DAYS = 7;
    private static String currentReportPath;
    private static boolean directoriesCreated = false;

    static {
        createDirectoryStructure();
    }

    private static void createDirectoryStructure() {
        if (directoriesCreated) {
            return;
        }

        try {
            // Create base reports directory
            Path reportsPath = Paths.get(REPORTS_PATH);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
                System.out.println("Created reports directory: " + reportsPath);
            }

            // Create subdirectories
            Path[] paths = {
                Paths.get(SCREENSHOTS_PATH),
                Paths.get(LOGS_PATH),
                Paths.get(ARCHIVE_PATH),
                Paths.get(OLD_REPORTS_PATH)
            };

            for (Path path : paths) {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                    System.out.println("Created directory: " + path);
                }
            }

            // Set permissions
            for (Path path : paths) {
                File dir = path.toFile();
                dir.setExecutable(true, false);
                dir.setReadable(true, false);
                dir.setWritable(true, false);
            }

            // Move old reports to old_reports directory if they exist
            moveOldReports();

            directoriesCreated = true;
            System.out.println("Report directory structure created successfully");

        } catch (IOException e) {
            System.err.println("Failed to create report directories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void moveOldReports() {
        try {
            // Move old reports to old_reports directory
            Path oldReportsPath = Paths.get(OLD_REPORTS_PATH);
            if (!Files.exists(oldReportsPath)) {
                Files.createDirectories(oldReportsPath);
            }

            // Move old screenshots with device-specific organization
            Path oldScreenshotsPath = Paths.get(SCREENSHOTS_PATH);
            if (Files.exists(oldScreenshotsPath)) {
                Path newScreenshotsPath = oldReportsPath.resolve("screenshots");
                if (!Files.exists(newScreenshotsPath)) {
                    Files.createDirectories(newScreenshotsPath);
                }

                // Group screenshots by device
                Files.walk(oldScreenshotsPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            String fileName = path.getFileName().toString();
                            // Extract device name from filename
                            String deviceName = fileName.split("_")[0];
                            Path devicePath = newScreenshotsPath.resolve(deviceName);
                            if (!Files.exists(devicePath)) {
                                Files.createDirectories(devicePath);
                            }
                            // Copy instead of move to preserve original paths
                            Files.copy(path, devicePath.resolve(fileName),
                                StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.err.println("Failed to copy old screenshot: " + path);
                        }
                    });
            }

            // Move old logs
            Path oldLogsPath = Paths.get(LOGS_PATH);
            if (Files.exists(oldLogsPath)) {
                Path newLogsPath = oldReportsPath.resolve("logs");
                if (!Files.exists(newLogsPath)) {
                    Files.createDirectories(newLogsPath);
                }
                Files.walk(oldLogsPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.copy(path, newLogsPath.resolve(path.getFileName()),
                                StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.err.println("Failed to copy old log: " + path);
                        }
                    });
            }

            System.out.println("Old reports preserved in: " + oldReportsPath);
        } catch (IOException e) {
            System.err.println("Failed to preserve old reports: " + e.getMessage());
        }
    }

    public static ExtentReports getReporterObject() {
        if (extent == null) {
            // Generate timestamped report path with date and hour folders
            String timestamp = DATE_FORMAT.format(new Date());
            String dateFolder = DATE_FOLDER_FORMAT.format(new Date());
            String hourFolder = HOUR_FOLDER_FORMAT.format(new Date());
            currentReportPath = ARCHIVE_PATH + "/" + dateFolder + "/" + hourFolder + "/" + timestamp;
            
            // Create report directory structure
            try {
                Files.createDirectories(Paths.get(currentReportPath));
                // Clean up old reports before creating new one
                cleanupOldReports();
            } catch (IOException e) {
                System.err.println("Failed to create report directory: " + e.getMessage());
            }

            String reportFile = currentReportPath + "/index.html";
            ExtentSparkReporter reporter = new ExtentSparkReporter(reportFile);

            // Configure reporter
            reporter.config().setReportName("Paytm First Games - Test Automation Report");
            reporter.config().setDocumentTitle("Test Results");
            reporter.config().setTheme(Theme.DARK);
            reporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            reporter.config().setCss(".badge-primary { background-color: #7045af; }");
            reporter.config().setJs("document.getElementsByClassName('logo')[0].style.display='none';");
            
            // Configure screenshot handling
            reporter.config().setOfflineMode(true);
            reporter.config().setTimelineEnabled(true);
            reporter.config().setResourceCDN("https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@b00a2d0486596e73dd7326beacf352c639623a0e/");
            
            // Create screenshots directory in report folder
            String screenshotsDir = currentReportPath + "/screenshots";
            new File(screenshotsDir).mkdirs();
            
            // Initialize extent reports
            extent = new ExtentReports();
            extent.attachReporter(reporter);
            
            // Set base path for screenshots to be relative to the report
            extent.setSystemInfo("Screenshots Directory", "screenshots");
            
            // Set view order
            reporter.viewConfigurer()
                .viewOrder()
                .as(new ViewName[] {
                    ViewName.DASHBOARD,
                    ViewName.TEST,
                    ViewName.EXCEPTION
                });

            // Add system info
            extent.setSystemInfo("Project", "Paytm First Games");
            extent.setSystemInfo("Environment", System.getProperty("test.environment", "QA"));
            extent.setSystemInfo("Platform", "Android");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            extent.setSystemInfo("Appium Version", "2.0");
            extent.setSystemInfo("Report Generated", new SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(new Date()));
            extent.setSystemInfo("Working Directory", System.getProperty("user.dir"));

            // Create a symbolic link to the latest report
            try {
                Path latestLink = Paths.get(REPORTS_PATH, "latest");
                Files.deleteIfExists(latestLink);
                Files.createSymbolicLink(latestLink, Paths.get(currentReportPath));
            } catch (IOException e) {
                System.err.println("Failed to create latest report link: " + e.getMessage());
            }

            // Print report location
            System.out.println("\n===============================================");
            System.out.println("Test Report will be generated at:");
            System.out.println("file://" + new File(reportFile).getAbsolutePath());
            System.out.println("Latest report link: file://" + new File(REPORTS_PATH + "/latest/index.html").getAbsolutePath());
            System.out.println("===============================================\n");
        }
        return extent;
    }

    public static String getScreenshotsPath() {
        return SCREENSHOTS_PATH;
    }

    public static String getCurrentReportPath() {
        return currentReportPath;
    }

    private static void cleanupOldReports() {
        try {
            Path archivePath = Paths.get(ARCHIVE_PATH);
            if (!Files.exists(archivePath)) {
                return;
            }

            // Calculate cutoff time (48 hours ago)
            long cutoffTime = System.currentTimeMillis() - (MAX_REPORT_AGE_HOURS * 60 * 60 * 1000);

            // Walk through the archive directory
            Files.walk(archivePath)
                .filter(Files::isDirectory)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (IOException e) {
                        System.err.println("Failed to delete old report: " + path);
                    }
                });
        } catch (IOException e) {
            System.err.println("Failed to cleanup old reports: " + e.getMessage());
        }
    }

    private static void cleanupOldScreenshots() {
        try (Stream<Path> paths = Files.list(Paths.get(SCREENSHOTS_PATH))) {
            paths.filter(path -> {
                try {
                    // Delete screenshots older than MAX_SCREENSHOT_AGE_DAYS
                    return Files.getLastModifiedTime(path).toMillis() <
                           System.currentTimeMillis() - (MAX_SCREENSHOT_AGE_DAYS * 24 * 60 * 60 * 1000);
                } catch (IOException e) {
                    return false;
                }
            }).forEach(path -> {
                try {
                    Files.delete(path);
                    System.out.println("Deleted old screenshot: " + path);
                } catch (IOException e) {
                    System.err.println("Failed to delete screenshot: " + path);
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to cleanup old screenshots: " + e.getMessage());
        }
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("\n===============================================");
            System.out.println("Test Report has been generated at:");
            System.out.println("file://" + new File(currentReportPath + "/index.html").getAbsolutePath());
            System.out.println("Latest report link: file://" + new File(REPORTS_PATH + "/latest/index.html").getAbsolutePath());
            System.out.println("===============================================\n");
        }
    }
} 