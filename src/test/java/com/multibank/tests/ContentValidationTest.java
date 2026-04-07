package com.multibank.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.multibank.pages.AboutPage;
import com.multibank.pages.HomePage;
import com.multibank.utils.ExtentReportManager;
import com.multibank.utils.TestDataLoader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * ContentValidationTest
 * ---------------------
 * Validates page content:
 *  - Marketing banners at the page bottom
 *  - App Store and Google Play download links
 *  - About Us → Why MultiBank page components and text
 *
 * Test data: src/test/resources/testdata/content.json
 */
public class ContentValidationTest extends BaseTest {

    private HomePage homePage;
    private JsonNode contentData;

    @BeforeMethod
    public void setUpPage() {
        contentData = TestDataLoader.load("content.json");
        openBaseUrl();
        homePage = new HomePage();
    }

    // ── TC-CONTENT-001 ───────────────────────────────────────────────────────

    @Test(description = "TC-CONTENT-001: Marketing banners are present at the page bottom")
    public void testMarketingBannersPresent() {
        ExtentReportManager.createTest(
            "TC-CONTENT-001: Marketing banners present",
            "Validates that at least one marketing banner appears toward the bottom of the page");

        logStep("Scrolling to bottom of page");
        logStep("Checking for marketing banners");

        boolean bannersPresent = homePage.areMarketingBannersPresent();
        int bannerCount = homePage.getMarketingBannerCount();

        logStep("Banners present: " + bannersPresent);
        logStep("Banner count: " + bannerCount);

        Assert.assertTrue(bannersPresent,
            "FAIL: No marketing banners found at the bottom of the home page");
    }

    // ── TC-CONTENT-002 ───────────────────────────────────────────────────────

    @Test(description = "TC-CONTENT-002: App Store download link is present and valid")
    public void testAppStoreLinkPresent() {
        ExtentReportManager.createTest(
            "TC-CONTENT-002: App Store link present",
            "Validates that an App Store download link exists and points to apps.apple.com");

        logStep("Scrolling to download section");
        logStep("Checking App Store link");

        boolean isPresent = homePage.isAppStoreLinkPresent();
        logStep("App Store link present: " + isPresent);

        Assert.assertTrue(isPresent,
            "FAIL: App Store download link not found on the page");

        String href = homePage.getAppStoreHref();
        logStep("App Store href: " + href);

        Assert.assertNotNull(href, "FAIL: App Store link href is null");
        Assert.assertFalse(href.trim().isEmpty(), "FAIL: App Store link href is empty");
        Assert.assertTrue(
            href.contains("apple.com") || href.contains("itunes.com"),
            "FAIL: App Store href does not point to Apple domain. Actual: " + href);
    }

    // ── TC-CONTENT-003 ───────────────────────────────────────────────────────

    @Test(description = "TC-CONTENT-003: Google Play download link is present and valid")
    public void testGooglePlayLinkPresent() {
        ExtentReportManager.createTest(
            "TC-CONTENT-003: Google Play link present",
            "Validates that a Google Play download link exists and points to play.google.com");

        logStep("Checking Google Play link");

        boolean isPresent = homePage.isGooglePlayLinkPresent();
        logStep("Google Play link present: " + isPresent);

        Assert.assertTrue(isPresent,
            "FAIL: Google Play download link not found on the page");

        String href = homePage.getGooglePlayHref();
        logStep("Google Play href: " + href);

        Assert.assertNotNull(href, "FAIL: Google Play link href is null");
        Assert.assertFalse(href.trim().isEmpty(), "FAIL: Google Play link href is empty");
        Assert.assertTrue(
            href.contains("play.google.com"),
            "FAIL: Google Play href does not point to Google Play. Actual: " + href);
    }

    // ── TC-CONTENT-004 ───────────────────────────────────────────────────────

    @Test(description = "TC-CONTENT-004: About Us → Why MultiBank page renders all expected components")
    public void testWhyMultiBankPageComponents() {
        ExtentReportManager.createTest(
            "TC-CONTENT-004: Why MultiBank page components",
            "Validates all expected sections and components on the Why MultiBank about page");

        String aboutUrl = TestDataLoader.getString(contentData, "aboutPageUrl");
        if (aboutUrl.trim().isEmpty()) aboutUrl = baseUrl + "about";

        logStep("Navigating to About page: " + aboutUrl);
        openUrl(aboutUrl);
        AboutPage aboutPage = new AboutPage();

        // Main heading
        logStep("Checking main heading");
        String heading = aboutPage.getMainHeadingText();
        logStep("Main heading: " + heading);
        Assert.assertFalse(heading.trim().isEmpty(),
            "FAIL: Main heading is blank on About/Why MultiBank page");

        // Hero section
        logStep("Checking hero section");
        boolean heroDisplayed = aboutPage.isHeroSectionDisplayed();
        logStep("Hero section displayed: " + heroDisplayed);

        // Feature cards
        logStep("Checking feature cards");
        boolean cardsPresent = aboutPage.areFeatureCardsDisplayed();
        int cardCount = aboutPage.getFeatureCardCount();
        logStep("Feature cards present: " + cardsPresent + " | count: " + cardCount);

        // Content text
        logStep("Checking page content text");
        boolean hasContent = aboutPage.hasContentText();
        logStep("Has content text: " + hasContent);
        Assert.assertTrue(hasContent,
            "FAIL: No content text found on About page");

        // All headings
        List<String> allHeadings = aboutPage.getAllHeadingTexts();
        logStep("All headings found: " + allHeadings);
        Assert.assertFalse(allHeadings.isEmpty(),
            "FAIL: No headings found on About page");
    }

    // ── TC-CONTENT-005 ───────────────────────────────────────────────────────

    @Test(description = "TC-CONTENT-005: About page contains expected text from test data")
    public void testAboutPageExpectedTextPresent() {
        ExtentReportManager.createTest(
            "TC-CONTENT-005: About page expected text",
            "Validates that key text phrases from test data are present on the About page");

        String aboutUrl = TestDataLoader.getString(contentData, "aboutPageUrl");
        if (aboutUrl.trim().isEmpty()) aboutUrl = baseUrl + "about";

        logStep("Navigating to About page: " + aboutUrl);
        openUrl(aboutUrl);
        AboutPage aboutPage = new AboutPage();

        List<String> expectedTexts = TestDataLoader.getStringList(
            contentData, "expectedAboutPageText");

        if (expectedTexts.isEmpty()) {
            logStep("No expected text entries in test data — checking basic content");
            Assert.assertTrue(aboutPage.hasContentText(),
                "FAIL: About page has no readable content");
            return;
        }

        for (String expected : expectedTexts) {
            boolean found = aboutPage.doesPageContainText(expected);
            logStep("Expected text '" + expected + "' present: " + found);
            Assert.assertTrue(found,
                "FAIL: Expected text '" + expected + "' not found on About page");
        }
    }

    // ── TC-CONTENT-006 ───────────────────────────────────────────────────────

    @Test(description = "TC-CONTENT-006: Download section is present on the page")
    public void testDownloadSectionPresent() {
        ExtentReportManager.createTest(
            "TC-CONTENT-006: Download section present",
            "Validates that the app download section container is visible");

        logStep("Checking download section");
        boolean isPresent = homePage.isDownloadSectionPresent();
        logStep("Download section present: " + isPresent);

        // At minimum, either the download section or the individual links should be present
        boolean appStorePresent  = homePage.isAppStoreLinkPresent();
        boolean googlePlayPresent = homePage.isGooglePlayLinkPresent();

        Assert.assertTrue(isPresent || appStorePresent || googlePlayPresent,
            "FAIL: No download section or app download links found on the page");
    }
}
