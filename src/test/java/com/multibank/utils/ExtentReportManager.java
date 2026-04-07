package com.multibank.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.multibank.config.FrameworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReportManager
 * -------------------
 * Thread-safe HTML report manager using ExtentReports 5 + SparkReporter.
 * Generates a timestamped HTML report in the configured reports directory.
 *
 * Usage (in BaseTest listener):
 *   ExtentReportManager.init();
 *   ExtentTest test = ExtentReportManager.createTest("Test Name");
 *   test.pass("Step passed");
 *   test.fail("Step failed");
 *   ExtentReportManager.flush();
 */
public class ExtentReportManager {

    private static final Logger log = LoggerFactory.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    private ExtentReportManager() {}

    /**
     * Initialises ExtentReports with a timestamped HTML output file.
     * Call once before all tests (e.g., in @BeforeSuite).
     */
    public static synchronized void init() {
        if (extent != null) return;

        FrameworkConfig config = FrameworkConfig.getInstance();
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = config.getReportDir() + "/MultiBank_TestReport_" + timestamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("MultiBank QA Automation Report");
        spark.config().setReportName("MultiBank Trading Platform — Test Execution Report");
        spark.config().setEncoding("UTF-8");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Application", "MultiBank Trading Platform");
        extent.setSystemInfo("Base URL",    config.getBaseUrl());
        extent.setSystemInfo("Browser",     config.getBrowser());
        extent.setSystemInfo("Environment", System.getProperty("env", "production"));
        extent.setSystemInfo("Executed By", System.getProperty("user.name", "CI"));

        log.info("ExtentReports initialised → {}", reportPath);
    }

    /**
     * Creates a new test entry and binds it to the current thread.
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
        return test;
    }

    /**
     * Creates a test entry with a description.
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        testThread.set(test);
        return test;
    }

    /**
     * Returns the ExtentTest for the current thread.
     */
    public static ExtentTest getTest() {
        return testThread.get();
    }

    /**
     * Removes the ExtentTest binding for the current thread.
     */
    public static void removeTest() {
        testThread.remove();
    }

    /**
     * Flushes all data to the HTML report file.
     * Call once after all tests (e.g., in @AfterSuite).
     */
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed successfully");
        }
    }
}
