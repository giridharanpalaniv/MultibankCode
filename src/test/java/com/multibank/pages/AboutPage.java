package com.multibank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AboutPage
 * ---------
 * Page Object for the "About Us → Why MultiBank" page.
 * Covers: page components, headings, sections, content text validation.
 */
public class AboutPage extends BasePage {

    // ── Page-level locators ──────────────────────────────────────────────────

    @FindBy(css = "h1, [class*='page-title'], [class*='hero-title'], [class*='main-heading']")
    private WebElement pageMainHeading;

    private final By allHeadingsLocator = By.cssSelector("h1, h2, h3");

    private final By heroSectionLocator = By.cssSelector(
        "[class*='hero'], [class*='banner'], [class*='intro'], [class*='about-hero']");

    private final By featureSectionsLocator = By.cssSelector(
        "[class*='feature'], [class*='benefit'], [class*='why'], " +
        "[class*='advantage'], [class*='reason'], section, article");

    private final By featureCardsLocator = By.cssSelector(
        "[class*='card'], [class*='feature-item'], [class*='benefit-item'], " +
        "[class*='reason-item'], [class*='advantage-item']");

    private final By contentTextLocator = By.cssSelector(
        "p, [class*='description'], [class*='content-text'], " +
        "[class*='body-text'], [class*='paragraph']");

    private final By statisticsLocator = By.cssSelector(
        "[class*='stat'], [class*='number'], [class*='count'], " +
        "[class*='metric'], [class*='figure']");

    private final By ctaButtonsLocator = By.cssSelector(
        "[class*='cta'], [class*='btn-primary'], [class*='get-started'], " +
        "a[href*='register'], a[href*='signup'], button[class*='primary']");

    private final By trustBadgesLocator = By.cssSelector(
        "[class*='trust'], [class*='award'], [class*='badge'], " +
        "[class*='certification'], [class*='license']");

    // ── Constructor ──────────────────────────────────────────────────────────

    public AboutPage() {
        super();
    }

    // ── Page component methods ───────────────────────────────────────────────

    /**
     * Returns the main page heading text.
     */
    public String getMainHeadingText() {
        try {
            return safeGetText(pageMainHeading);
        } catch (Exception e) {
            log.warn("Main heading not found: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Returns all heading texts (h1, h2, h3) on the page.
     */
    public List<String> getAllHeadingTexts() {
        try {
            List<WebElement> headings = waitForAllVisible(allHeadingsLocator);
            return headings.stream()
                           .map(this::safeGetText)
                           .filter(t -> !t.trim().isEmpty())
                           .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Could not retrieve headings: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Returns true if the hero/intro section is visible.
     */
    public boolean isHeroSectionDisplayed() {
        return isDisplayed(heroSectionLocator);
    }

    /**
     * Returns count of feature/benefit/why sections.
     */
    public int getFeatureSectionCount() {
        try {
            return driver.findElements(featureSectionsLocator).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Returns count of feature/benefit cards.
     */
    public int getFeatureCardCount() {
        try {
            return driver.findElements(featureCardsLocator).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Returns true if at least one feature card is visible.
     */
    public boolean areFeatureCardsDisplayed() {
        return isDisplayed(featureCardsLocator);
    }

    /**
     * Returns a list of all non-empty paragraph/content texts.
     */
    public List<String> getContentTexts() {
        try {
            List<WebElement> paras = waitForAllVisible(contentTextLocator);
            return paras.stream()
                        .map(this::safeGetText)
                        .filter(t -> t.length() > 5)
                        .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Could not retrieve content texts: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Returns true if the page has any visible paragraph content.
     */
    public boolean hasContentText() {
        return !getContentTexts().isEmpty();
    }

    /**
     * Returns true if statistics/figures section is present.
     */
    public boolean areStatisticsDisplayed() {
        return isDisplayed(statisticsLocator);
    }

    /**
     * Returns true if CTA (call-to-action) buttons are present.
     */
    public boolean areCtaButtonsPresent() {
        return isDisplayed(ctaButtonsLocator);
    }

    /**
     * Returns true if trust badges/certifications/awards section is present.
     */
    public boolean areTrustBadgesPresent() {
        jsScrollToBottom();
        return isDisplayed(trustBadgesLocator);
    }

    /**
     * Checks whether a specific text fragment appears anywhere on the page.
     */
    public boolean doesPageContainText(String expectedText) {
        String bodyText = driver.findElement(By.tagName("body")).getText();
        boolean found = bodyText.toLowerCase().contains(expectedText.toLowerCase());
        log.info("Text '{}' found on page: {}", expectedText, found);
        return found;
    }

    /**
     * Returns all heading texts that contain the given keyword (case-insensitive).
     */
    public List<String> getHeadingsContaining(String keyword) {
        return getAllHeadingTexts().stream()
                                  .filter(h -> h.toLowerCase().contains(keyword.toLowerCase()))
                                  .collect(Collectors.toList());
    }
}
