package com.multibank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TradingPage
 * -----------
 * Page Object for the Spot Trading section of MultiBank.
 * Covers: trading categories, trading pairs, pair data structure.
 */
public class TradingPage extends BasePage {

    // ── Spot Trading Container ───────────────────────────────────────────────

    private final By spotTradingContainerLocator = By.cssSelector(
        "[class*='spot'], [class*='trading'], [id*='spot'], [id*='trading'], " +
        "[class*='market'], [class*='trade-section']");

    // ── Category Tabs ────────────────────────────────────────────────────────

    private final By categoryTabsLocator = By.cssSelector(
        "[class*='category'], [class*='tab'], [class*='filter'], " +
        "[role='tab'], [class*='market-tab'], [class*='pair-category']");

    // ── Trading Pair Rows ────────────────────────────────────────────────────

    private final By tradingPairRowsLocator = By.cssSelector(
        "[class*='pair-row'], [class*='trading-pair'], [class*='instrument'], " +
        "[class*='market-row'], [class*='symbol-row'], tbody tr, " +
        "[class*='pair-item'], [class*='asset-row']");

    // ── Pair Name / Symbol ───────────────────────────────────────────────────

    private final By pairNameLocator = By.cssSelector(
        "[class*='pair-name'], [class*='symbol'], [class*='instrument-name'], " +
        "[class*='asset-name'], td:first-child, [class*='pair-title']");

    // ── Pair Price ───────────────────────────────────────────────────────────

    private final By pairPriceLocator = By.cssSelector(
        "[class*='price'], [class*='last-price'], [class*='current-price'], " +
        "[class*='bid'], td:nth-child(2), [class*='rate']");

    // ── Change Percentage ────────────────────────────────────────────────────

    private final By changePercentLocator = By.cssSelector(
        "[class*='change'], [class*='percent'], [class*='variation'], " +
        "[class*='24h'], td:nth-child(3), [class*='price-change']");

    // ── Search / Filter ──────────────────────────────────────────────────────

    @FindBy(css = "input[placeholder*='search'], input[placeholder*='Search'], " +
                  "input[type='search'], [class*='search-input'], [class*='filter-input']")
    private WebElement searchInput;

    // ── Constructor ──────────────────────────────────────────────────────────

    public TradingPage() {
        super();
    }

    // ── Spot Trading methods ─────────────────────────────────────────────────

    /**
     * Returns true if the spot trading section container is visible.
     */
    public boolean isSpotTradingSectionDisplayed() {
        return isDisplayed(spotTradingContainerLocator);
    }

    /**
     * Returns all category tab labels (Crypto, Forex, Stocks, etc.).
     */
    public List<String> getCategoryTabLabels() {
        try {
            List<WebElement> tabs = waitForAllVisible(categoryTabsLocator);
            return tabs.stream()
                       .map(this::safeGetText)
                       .filter(t -> !t.trim().isEmpty())
                       .distinct()
                       .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Could not retrieve category tabs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Clicks the category tab matching the given label (case-insensitive).
     */
    public void selectCategory(String categoryName) {
        List<WebElement> tabs = waitForAllVisible(categoryTabsLocator);
        tabs.stream()
            .filter(t -> safeGetText(t).equalsIgnoreCase(categoryName))
            .findFirst()
            .ifPresent(e -> {
                safeClick(e);
                log.info("Selected category: {}", categoryName);
            });
        // Wait for list to refresh
        wait.until(ExpectedConditions.stalenessOf(
            driver.findElement(tradingPairRowsLocator)));
    }

    /**
     * Returns list of visible trading pair WebElements.
     */
    public List<WebElement> getTradingPairRows() {
        try {
            return waitForAllVisible(tradingPairRowsLocator);
        } catch (Exception e) {
            log.warn("No trading pair rows found: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns count of visible trading pairs.
     */
    public int getTradingPairCount() {
        return getTradingPairRows().size();
    }

    /**
     * Returns all trading pair name strings.
     */
    public List<String> getTradingPairNames() {
        try {
            List<WebElement> nameEls = waitForAllVisible(pairNameLocator);
            return nameEls.stream()
                          .map(this::safeGetText)
                          .filter(t -> !t.trim().isEmpty())
                          .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Could not retrieve pair names: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns true if price data cells are present alongside pair names.
     */
    public boolean arePricesDisplayed() {
        return isDisplayed(pairPriceLocator);
    }

    /**
     * Returns true if change percentage data is displayed.
     */
    public boolean areChangePercentagesDisplayed() {
        return isDisplayed(changePercentLocator);
    }

    /**
     * Returns true if the pair data structure (name + price + change) is complete.
     */
    public boolean isPairDataStructureComplete() {
        return !getTradingPairNames().isEmpty()
            && arePricesDisplayed()
            && areChangePercentagesDisplayed();
    }

    /**
     * Searches for a trading pair by entering text in the search/filter input.
     */
    public void searchForPair(String pairName) {
        try {
            waitForVisible(searchInput);
            searchInput.clear();
            searchInput.sendKeys(pairName);
            log.info("Searched for pair: {}", pairName);
        } catch (Exception e) {
            log.warn("Search input not available: {}", e.getMessage());
        }
    }

    /**
     * Returns true if the specified pair name appears in the current list.
     */
    public boolean isPairVisible(String pairName) {
        return getTradingPairNames().stream()
                                    .anyMatch(n -> n.contains(pairName));
    }
}
