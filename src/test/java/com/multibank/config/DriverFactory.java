package com.multibank.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * DriverFactory
 * -------------
 * Thread-safe WebDriver factory using ThreadLocal storage.
 * Supports Chrome, Firefox, Edge — local and remote (Selenium Grid / BrowserStack).
 *
 * Usage:
 *   DriverFactory.initDriver("chrome");
 *   WebDriver driver = DriverFactory.getDriver();
 *   DriverFactory.quitDriver();
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();
    private static final FrameworkConfig config = FrameworkConfig.getInstance();

    private DriverFactory() {}

    /**
     * Initialises a WebDriver for the given browser on the current thread.
     * If remote execution is enabled, creates a RemoteWebDriver instead.
     */
    public static void initDriver(String browser) {
        WebDriver driver;

        if (config.isRemote()) {
            driver = createRemoteDriver(browser);
        } else {
            driver = createLocalDriver(browser);
        }

        applyTimeouts(driver);
        driver.manage().window().maximize();
        driverThread.set(driver);
        log.info("Driver initialised: {} | thread: {}", browser, Thread.currentThread().getId());
    }

    /** Returns the WebDriver bound to the current thread. */
    public static WebDriver getDriver() {
        WebDriver d = driverThread.get();
        if (d == null) throw new IllegalStateException("Driver not initialised. Call initDriver() first.");
        return d;
    }

    /** Quits and removes the WebDriver for the current thread. */
    public static void quitDriver() {
        WebDriver d = driverThread.get();
        if (d != null) {
            d.quit();
            driverThread.remove();
            log.info("Driver quit: thread {}", Thread.currentThread().getId());
        }
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private static WebDriver createLocalDriver(String browser) {
        switch (browser.toLowerCase().trim()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOpts = new FirefoxOptions();
                if (config.isHeadless()) firefoxOpts.addArguments("--headless");
                return new FirefoxDriver(firefoxOpts);
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOpts = new EdgeOptions();
                if (config.isHeadless()) edgeOpts.addArguments("--headless");
                return new EdgeDriver(edgeOpts);
            default:  // chrome
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOpts = buildChromeOptions();
                return new ChromeDriver(chromeOpts);
        }
    }

    private static WebDriver createRemoteDriver(String browser) {
        try {
            Capabilities caps;
            switch (browser.toLowerCase().trim()) {
                case "firefox":
                    caps = new FirefoxOptions();
                    break;
                case "edge":
                    caps = new EdgeOptions();
                    break;
                default:
                    caps = buildChromeOptions();
                    break;
            }
            URL gridUrl = new URL(config.getRemoteUrl());
            log.info("Connecting to remote grid: {}", gridUrl);
            return new RemoteWebDriver(gridUrl, caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote URL: " + config.getRemoteUrl(), e);
        }
    }

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions opts = new ChromeOptions();
        if (config.isHeadless()) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--window-size=1920,1080",
            "--disable-notifications",
            "--disable-popup-blocking",
            "--disable-extensions"
        );
        return opts;
    }

    private static void applyTimeouts(WebDriver driver) {
        // No implicit wait — using explicit waits only (best practice)
        driver.manage().timeouts()
              .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
    }
}
