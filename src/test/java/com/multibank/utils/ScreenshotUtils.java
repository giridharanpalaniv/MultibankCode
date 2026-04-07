package com.multibank.utils;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.multibank.config.DriverFactory;
import com.multibank.config.FrameworkConfig;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * ScreenshotUtils
 * ---------------
 * Captures screenshots and attaches them to ExtentReports.
 * Screenshots are saved to reports/screenshots/ with timestamps.
 */
public class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR =
        FrameworkConfig.getInstance().getReportDir() + "/screenshots/";

    private ScreenshotUtils() {}

    /**
     * Captures a screenshot and saves it to the screenshots directory.
     * Returns the file path (used for attaching to report).
     */
    public static String captureAndSave(String testName) {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
            WebDriver driver = DriverFactory.getDriver();
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String fileName = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";
            Files.copy(src.toPath(), Path.of(fileName));
            log.info("Screenshot saved: {}", fileName);
            return fileName;
        } catch (IOException e) {
            log.error("Failed to save screenshot", e);
            return "";
        }
    }

    /**
     * Captures a screenshot and returns it as a Base64 string.
     * This format is directly embeddable in ExtentReports.
     */
    public static String captureAsBase64() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture base64 screenshot", e);
            return "";
        }
    }

    /**
     * Attaches a screenshot to the current ExtentTest.
     * Call this on test failure from the TestNG listener.
     */
    public static void attachToReport(String testName) {
        try {
            String base64 = captureAsBase64();
            if (!base64.isEmpty() && ExtentReportManager.getTest() != null) {
                ExtentReportManager.getTest()
                    .fail("Test failed — screenshot captured",
                          MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            }
            // Also save to disk for CI artifacts
            captureAndSave(testName);
        } catch (Exception e) {
            log.error("Failed to attach screenshot to report", e);
        }
    }
}
