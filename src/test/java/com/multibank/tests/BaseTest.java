package com.multibank.tests;

import com.aventstack.extentreports.Status;
import com.multibank.config.DriverFactory;
import com.multibank.config.FrameworkConfig;
import com.multibank.utils.ExtentReportManager;
import com.multibank.utils.ScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * BaseTest
 * --------
 * All test classes extend this.
 * Manages:
 *  - Report initialisation / flush (@BeforeSuite / @AfterSuite)
 *  - Driver lifecycle per test method (@BeforeMethod / @AfterMethod)
 *  - Automatic screenshot + report entry on failure
 *  - Browser parameter injection from testng.xml or system property
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final FrameworkConfig config = FrameworkConfig.getInstance();
    protected final String baseUrl = FrameworkConfig.getInstance().getBaseUrl();

    // ── Suite level ──────────────────────────────────────────────────────────

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() throws Exception {
        // Create reports directory
        Files.createDirectories(Paths.get(config.getReportDir()));
        ExtentReportManager.init();
        log.info("=== TEST SUITE STARTED ===");
        log.info("Base URL  : {}", config.getBaseUrl());
        log.info("Browser   : {}", config.getBrowser());
        log.info("Headless  : {}", config.isHeadless());
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        ExtentReportManager.flush();
        log.info("=== TEST SUITE COMPLETED ===");
    }

    // ── Test level ───────────────────────────────────────────────────────────

    /**
     * Receives browser from testng.xml <parameter> or falls back to config.
     * @param browser optional browser override from XML
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void testSetup(@Optional String browser) {
        String b = (browser != null && !browser.trim().isEmpty())
                   ? browser : config.getBrowser();
        DriverFactory.initDriver(b);
        log.info("Driver initialised for: {}", b);
    }

    @AfterMethod(alwaysRun = true)
    public void testTeardown(ITestResult result) {
        // Update Extent report based on result
        if (ExtentReportManager.getTest() != null) {
            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    ExtentReportManager.getTest().log(Status.PASS, "Test PASSED");
                    break;
                case ITestResult.FAILURE:
                    ScreenshotUtils.attachToReport(result.getName());
                    ExtentReportManager.getTest().log(Status.FAIL,
                        "Test FAILED: " + result.getThrowable().getMessage());
                    break;
                case ITestResult.SKIP:
                    ExtentReportManager.getTest().log(Status.SKIP, "Test SKIPPED");
                    break;
            }
            ExtentReportManager.removeTest();
        }

        DriverFactory.quitDriver();
        log.info("Driver quit after: {}", result.getName());
    }

    // ── Utility for subclasses ───────────────────────────────────────────────

    /** Opens the base URL and returns for fluent chaining. */
    protected void openBaseUrl() {
        DriverFactory.getDriver().get(baseUrl);
        log.info("Opened: {}", baseUrl);
    }

    /** Opens any URL. */
    protected void openUrl(String url) {
        DriverFactory.getDriver().get(url);
        log.info("Opened: {}", url);
    }

    /** Logs a step to the Extent report. */
    protected void logStep(String message) {
        log.info(message);
        if (ExtentReportManager.getTest() != null)
            ExtentReportManager.getTest().log(Status.INFO, message);
    }
}
