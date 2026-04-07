package com.multibank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HomePage
 * --------
 * Page Object for https://trade.multibank.io/
 * Covers: navigation bar, marketing banners, app download section.
 */
public class HomePage extends BasePage {

    // ── Top Navigation ───────────────────────────────────────────────────────

    @FindBy(css = "nav, header nav, .nav, .navbar, [class*='nav'], [class*='header']")
    private WebElement topNavContainer;

    private final By navItemsLocator = By.cssSelector(
        "nav a, header nav a, .nav-item, .navbar a, [class*='nav'] a");

    private final By navMenuLocator  = By.cssSelector(
        "nav, header nav, [class*='navigation'], [class*='navbar'], [class*='header-nav']");

    // ── Marketing Banners ────────────────────────────────────────────────────

    private final By marketingBannerLocator = By.cssSelector(
        "[class*='banner'], [class*='promo'], [class*='marketing'], " +
        "[class*='hero'], [class*='advertisement'], footer [class*='banner']");

    private final By footerBannerLocator = By.cssSelector(
        "footer [class*='banner'], footer [class*='promo'], " +
        "[class*='footer'] [class*='banner'], [class*='bottom-banner']");

    // ── Download Section ─────────────────────────────────────────────────────

    private final By appStoreLinkLocator = By.cssSelector(
        "a[href*='apps.apple.com'], a[href*='itunes.apple.com'], " +
        "[class*='app-store'] a, [aria-label*='App Store'], img[alt*='App Store']");

    private final By googlePlayLinkLocator = By.cssSelector(
        "a[href*='play.google.com'], [class*='google-play'] a, " +
        "[aria-label*='Google Play'], img[alt*='Google Play']");

    private final By downloadSectionLocator = By.cssSelector(
        "[class*='download'], [class*='app-download'], [id*='download']");

    // ── Constructor ──────────────────────────────────────────────────────────

    public HomePage() {
        super();
    }

    // ── Navigation methods ───────────────────────────────────────────────────

    /**
     * Returns true if the top navigation container is visible on the page.
     */
    public boolean isNavMenuDisplayed() {
        return isDisplayed(navMenuLocator);
    }

    /**
     * Returns a list of all nav link texts that are visible.
     */
    public List<String> getNavigationItemTexts() {
        List<WebElement> items = waitForAllVisible(navItemsLocator);
        return items.stream()
                    .map(e -> safeGetText(e))
                    .filter(t -> !t.trim().isEmpty())
                    .collect(Collectors.toList());
    }

    /**
     * Returns a list of all nav link hrefs.
     */
    public List<String> getNavigationItemHrefs() {
        List<WebElement> items = waitForAllVisible(navItemsLocator);
        return items.stream()
                    .map(e -> safeGetAttribute(e, "href"))
                    .filter(h -> h != null && !h.trim().isEmpty())
                    .collect(Collectors.toList());
    }

    /**
     * Clicks the nav item that contains the given text (case-insensitive).
     */
    public void clickNavItem(String linkText) {
        List<WebElement> items = waitForAllVisible(navItemsLocator);
        items.stream()
             .filter(e -> safeGetText(e).equalsIgnoreCase(linkText))
             .findFirst()
             .ifPresent(this::safeClick);
        log.info("Clicked nav item: {}", linkText);
    }

    /**
     * Checks whether a nav item with the given text exists (case-insensitive).
     */
    public boolean isNavItemPresent(String text) {
        return getNavigationItemTexts().stream()
                                      .anyMatch(t -> t.equalsIgnoreCase(text));
    }

    // ── Marketing Banner methods ─────────────────────────────────────────────

    /**
     * Returns true if at least one marketing banner is present on the page.
     */
    public boolean areMarketingBannersPresent() {
        jsScrollToBottom();
        return isDisplayed(marketingBannerLocator) || isDisplayed(footerBannerLocator);
    }

    /**
     * Returns the count of marketing banners found.
     */
    public int getMarketingBannerCount() {
        jsScrollToBottom();
        try {
            return driver.findElements(marketingBannerLocator).size()
                 + driver.findElements(footerBannerLocator).size();
        } catch (Exception e) {
            return 0;
        }
    }

    // ── Download Section methods ─────────────────────────────────────────────

    /**
     * Scrolls to download section and returns true if App Store link is present.
     */
    public boolean isAppStoreLinkPresent() {
        jsScrollToBottom();
        return isDisplayed(appStoreLinkLocator);
    }

    /**
     * Scrolls to download section and returns true if Google Play link is present.
     */
    public boolean isGooglePlayLinkPresent() {
        jsScrollToBottom();
        return isDisplayed(googlePlayLinkLocator);
    }

    /**
     * Returns the href of the App Store link.
     */
    public String getAppStoreHref() {
        WebElement el = waitForVisible(appStoreLinkLocator);
        return safeGetAttribute(el, "href");
    }

    /**
     * Returns the href of the Google Play link.
     */
    public String getGooglePlayHref() {
        WebElement el = waitForVisible(googlePlayLinkLocator);
        return safeGetAttribute(el, "href");
    }

    /**
     * Returns true if the download section container is present.
     */
    public boolean isDownloadSectionPresent() {
        jsScrollToBottom();
        return isDisplayed(downloadSectionLocator);
    }
}
