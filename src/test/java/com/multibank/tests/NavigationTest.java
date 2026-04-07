package com.multibank.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.multibank.pages.HomePage;
import com.multibank.utils.ExtentReportManager;
import com.multibank.utils.TestDataLoader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * NavigationTest
 * --------------
 * Validates top navigation menu:
 *  - Nav container is visible
 *  - Expected nav items are present
 *  - Nav links have valid hrefs
 *  - Nav items are clickable and navigate correctly
 *
 * Test data: src/test/resources/testdata/navigation.json
 */
public class  NavigationTest extends BaseTest {

    private HomePage homePage;
    private JsonNode navData;

    @BeforeMethod
    public void setUpPage() {
        navData = TestDataLoader.load("navigation.json");
        openBaseUrl();
        homePage = new HomePage();
    }

    // ── TC-NAV-001 ───────────────────────────────────────────────────────────

    @Test(description = "TC-NAV-001: Top navigation menu is displayed")
    public void testNavigationMenuIsDisplayed() {
        ExtentReportManager.createTest(
            "TC-NAV-001: Navigation menu displayed",
            "Validates the top navigation container is visible on the home page");

        logStep("Opening MultiBank home page");
        logStep("Checking navigation menu visibility");

        boolean isDisplayed = homePage.isNavMenuDisplayed();

        logStep("Navigation menu displayed: " + isDisplayed);
        Assert.assertTrue(isDisplayed,
            "FAIL: Top navigation menu is not displayed on the home page");
    }

    // ── TC-NAV-002 ───────────────────────────────────────────────────────────

    @Test(description = "TC-NAV-002: Navigation items are visible with expected labels")
    public void testNavigationItemsPresent() {
        ExtentReportManager.createTest(
            "TC-NAV-002: Navigation items present",
            "Validates that expected navigation items are present in the nav menu");

        logStep("Retrieving navigation item texts");
        List<String> actualItems = homePage.getNavigationItemTexts();
        List<String> expectedItems = TestDataLoader.getStringList(navData, "expectedNavItems");

        logStep("Actual nav items found: " + actualItems);
        logStep("Expected nav items: " + expectedItems);

        Assert.assertFalse(actualItems.isEmpty(),
            "FAIL: No navigation items found in the nav menu");

        // Validate each expected item is present (partial/case-insensitive match)
        for (String expected : expectedItems) {
            boolean found = actualItems.stream()
                .anyMatch(actual -> actual.toLowerCase().contains(expected.toLowerCase()));
            logStep("Nav item '" + expected + "' present: " + found);
            Assert.assertTrue(found,
                "FAIL: Expected nav item '" + expected + "' not found. Found: " + actualItems);
        }
    }

    // ── TC-NAV-003 ───────────────────────────────────────────────────────────

    @Test(description = "TC-NAV-003: All navigation links have valid non-empty hrefs")
    public void testNavigationLinksHaveValidHrefs() {
        ExtentReportManager.createTest(
            "TC-NAV-003: Navigation links have valid hrefs",
            "Validates that all navigation anchor tags have non-null, non-empty href values");

        logStep("Retrieving navigation link hrefs");
        List<String> hrefs = homePage.getNavigationItemHrefs();

        logStep("Total nav links with hrefs: " + hrefs.size());
        Assert.assertFalse(hrefs.isEmpty(),
            "FAIL: No navigation link hrefs found");

        long invalidHrefs = hrefs.stream()
            .filter(h -> h == null || h.trim().isEmpty() || h.equals("#"))
            .count();

        logStep("Invalid hrefs count: " + invalidHrefs);
        Assert.assertEquals(invalidHrefs, 0,
            "FAIL: Found " + invalidHrefs + " navigation links with invalid/empty hrefs");
    }

    // ── TC-NAV-004 ───────────────────────────────────────────────────────────

    @Test(description = "TC-NAV-004: Navigation items count meets minimum threshold")
    public void testNavigationItemCountMinimum() {
        ExtentReportManager.createTest(
            "TC-NAV-004: Navigation items count",
            "Validates that the navigation has at least the minimum expected number of items");

        int minExpected = TestDataLoader.getInt(navData, "minimumNavItemCount", 3);
        List<String> items = homePage.getNavigationItemTexts();

        logStep("Minimum expected nav items: " + minExpected);
        logStep("Actual nav items count: " + items.size());
        logStep("Items found: " + items);

        Assert.assertTrue(items.size() >= minExpected,
            "FAIL: Expected at least " + minExpected + " nav items, found: " + items.size());
    }
}
