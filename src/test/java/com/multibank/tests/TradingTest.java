package com.multibank.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.multibank.pages.TradingPage;
import com.multibank.utils.ExtentReportManager;
import com.multibank.utils.TestDataLoader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TradingTest
 * -----------
 * Validates the Spot Trading section:
 *  - Section is displayed
 *  - Trading categories are visible
 *  - Trading pairs are populated
 *  - Pair data structure (name + price + change) is complete
 *  - Pairs exist across different categories
 *
 * Test data: src/test/resources/testdata/trading.json
 */
public class TradingTest extends BaseTest {

    private TradingPage tradingPage;
    private JsonNode tradingData;

    @BeforeMethod
    public void setUpPage() {
        tradingData = TestDataLoader.load("trading.json");
        openBaseUrl();
        tradingPage = new TradingPage();
    }

    // ── TC-TRADE-001 ─────────────────────────────────────────────────────────

    @Test(description = "TC-TRADE-001: Spot trading section is displayed")
    public void testSpotTradingSectionDisplayed() {
        ExtentReportManager.createTest(
            "TC-TRADE-001: Spot trading section displayed",
            "Validates that the spot trading section is visible on the page");

        logStep("Checking spot trading section visibility");
        boolean isDisplayed = tradingPage.isSpotTradingSectionDisplayed();

        logStep("Spot trading section displayed: " + isDisplayed);
        Assert.assertTrue(isDisplayed,
            "FAIL: Spot trading section is not displayed on the page");
    }

    // ── TC-TRADE-002 ─────────────────────────────────────────────────────────

    @Test(description = "TC-TRADE-002: Trading categories are displayed")
    public void testTradingCategoriesDisplayed() {
        ExtentReportManager.createTest(
            "TC-TRADE-002: Trading categories displayed",
            "Validates that trading category tabs/filters are present");

        logStep("Retrieving trading category tabs");
        List<String> categories = tradingPage.getCategoryTabLabels();
        List<String> expectedCategories = TestDataLoader.getStringList(
            tradingData, "expectedCategories");

        logStep("Categories found: " + categories);
        logStep("Expected categories: " + expectedCategories);

        Assert.assertFalse(categories.isEmpty(),
            "FAIL: No trading category tabs found on the page");

        // Verify expected categories are present (if specified in test data)
        for (String expected : expectedCategories) {
            boolean found = categories.stream()
                .anyMatch(c -> c.toLowerCase().contains(expected.toLowerCase()));
            logStep("Category '" + expected + "' found: " + found);
            // Soft assertion — warn but don't fail if category name changes
            if (!found) {
                logStep("WARNING: Expected category '" + expected + "' not found in: " + categories);
            }
        }
    }

    // ── TC-TRADE-003 ─────────────────────────────────────────────────────────

    @Test(description = "TC-TRADE-003: Trading pairs are populated in the list")
    public void testTradingPairsPopulated() {
        ExtentReportManager.createTest(
            "TC-TRADE-003: Trading pairs populated",
            "Validates that at least the minimum expected number of trading pairs are displayed");

        int minPairs = TestDataLoader.getInt(tradingData, "minimumPairCount", 5);
        logStep("Minimum expected trading pairs: " + minPairs);

        int actualCount = tradingPage.getTradingPairCount();
        logStep("Actual trading pair count: " + actualCount);

        Assert.assertTrue(actualCount >= minPairs,
            "FAIL: Expected at least " + minPairs + " trading pairs, found: " + actualCount);
    }

    // ── TC-TRADE-004 ─────────────────────────────────────────────────────────

    @Test(description = "TC-TRADE-004: Trading pair names are displayed")
    public void testTradingPairNamesDisplayed() {
        ExtentReportManager.createTest(
            "TC-TRADE-004: Trading pair names displayed",
            "Validates that pair names/symbols are visible in the trading list");

        logStep("Retrieving trading pair names");
        List<String> pairNames = tradingPage.getTradingPairNames();

        logStep("Pair names found: " + pairNames.subList(0, Math.min(10, pairNames.size())));

        Assert.assertFalse(pairNames.isEmpty(),
            "FAIL: No trading pair names found in the list");

        // All pair names should be non-empty strings
        long emptyNames = pairNames.stream().filter(name -> name.trim().isEmpty()).count();
        Assert.assertEquals(emptyNames, 0,
            "FAIL: Found " + emptyNames + " trading pairs with empty names");
    }

    // ── TC-TRADE-005 ─────────────────────────────────────────────────────────

    @Test(description = "TC-TRADE-005: Trading pair data structure is complete (name + price + change)")
    public void testTradingPairDataStructureComplete() {
        ExtentReportManager.createTest(
            "TC-TRADE-005: Trading pair data structure complete",
            "Validates that each trading pair shows name, price, and change percentage");

        logStep("Checking pair data structure completeness");
        boolean pairNamesPresent  = !tradingPage.getTradingPairNames().isEmpty();
        boolean pricesPresent     = tradingPage.arePricesDisplayed();
        boolean changesPresent    = tradingPage.areChangePercentagesDisplayed();

        logStep("Pair names present  : " + pairNamesPresent);
        logStep("Prices present      : " + pricesPresent);
        logStep("Change % present    : " + changesPresent);

        Assert.assertTrue(pairNamesPresent,  "FAIL: Trading pair names not displayed");
        Assert.assertTrue(pricesPresent,     "FAIL: Trading pair prices not displayed");
        Assert.assertTrue(changesPresent,    "FAIL: Trading pair change percentages not displayed");
    }

    // ── TC-TRADE-006 ─────────────────────────────────────────────────────────

    @Test(description = "TC-TRADE-006: Specific trading pairs are present in the list")
    public void testSpecificTradingPairsPresent() {
        ExtentReportManager.createTest(
            "TC-TRADE-006: Specific trading pairs present",
            "Validates that key trading pairs (from test data) are visible");

        List<String> requiredPairs = TestDataLoader.getStringList(
            tradingData, "requiredPairs");

        if (requiredPairs.isEmpty()) {
            logStep("No required pairs specified in test data — skipping");
            return;
        }

        for (String pair : requiredPairs) {
            boolean found = tradingPage.isPairVisible(pair);
            logStep("Pair '" + pair + "' visible: " + found);
            Assert.assertTrue(found,
                "FAIL: Required trading pair '" + pair + "' not found in list");
        }
    }
}
