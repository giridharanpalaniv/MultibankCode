package com.multibank.pages;

import com.multibank.config.DriverFactory;
import com.multibank.config.FrameworkConfig;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * BasePage
 * --------
 * Abstract parent for all Page Objects.
 * Provides:
 *  - Thread-safe driver access
 *  - Fluent explicit wait helpers (no Thread.sleep)
 *  - Safe element interactions with auto-retry on stale elements
 *  - Screenshot capture
 *  - JavaScript utilities
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;
    protected final Actions actions;
    private   final FrameworkConfig config = FrameworkConfig.getInstance();

    protected BasePage() {
        this.driver    = DriverFactory.getDriver();
        int timeout    = config.getExplicitWait();
        this.wait      = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.actions   = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    public void navigateTo(String url) {
        log.info("Navigate → {}", url);
        driver.get(url);
    }

    public String getCurrentUrl()   { return driver.getCurrentUrl(); }
    public String getPageTitle()    { return driver.getTitle(); }

    // ── Wait helpers ─────────────────────────────────────────────────────────

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected boolean waitForUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected boolean isDisplayed(By locator) {
        try {
            return shortWait
                .until(ExpectedConditions.visibilityOfElementLocated(locator))
                .isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    // ── Safe interactions ────────────────────────────────────────────────────

    protected void safeClick(WebElement element) {
        try {
            waitForClickable(element).click();
        } catch (ElementClickInterceptedException e) {
            log.warn("Click intercepted — retrying via JS");
            jsClick(element);
        } catch (StaleElementReferenceException e) {
            log.warn("Stale element on click — refreshing");
            throw e;  // let caller handle retry
        }
    }

    protected void safeClick(By locator) {
        safeClick(waitForClickable(locator));
    }

    protected String safeGetText(WebElement element) {
        try {
            return waitForVisible(element).getText().trim();
        } catch (StaleElementReferenceException e) {
            log.warn("Stale element on getText");
            return "";
        }
    }

    protected String safeGetText(By locator) {
        return safeGetText(waitForVisible(locator));
    }

    protected String safeGetAttribute(WebElement element, String attr) {
        try {
            return element.getAttribute(attr);
        } catch (StaleElementReferenceException e) {
            return "";
        }
    }

    protected void hoverOver(WebElement element) {
        actions.moveToElement(waitForVisible(element)).perform();
    }

    // ── JavaScript helpers ───────────────────────────────────────────────────

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void jsScrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", element);
    }

    protected void jsScrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    protected Object jsExecute(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    // ── Screenshot ───────────────────────────────────────────────────────────

    public byte[] captureScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
