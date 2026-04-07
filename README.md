# MultiBank QA Automation Framework

Production-grade Selenium Java automation framework for [trade.multibank.io](https://trade.multibank.io/)  
**Task 1** — Web UI Automation | **Task 2** — String Character Frequency

---

## Project Structure

```
multibank-qa/
├── src/test/java/com/multibank/
│   ├── config/
│   │   ├── DriverFactory.java        # Thread-safe WebDriver factory
│   │   └── FrameworkConfig.java      # Central config reader
│   ├── pages/
│   │   ├── BasePage.java             # Parent POM — wait helpers, JS utils
│   │   ├── HomePage.java             # Nav, banners, download links
│   │   ├── TradingPage.java          # Spot trading section
│   │   └── AboutPage.java            # About / Why MultiBank
│   ├── tests/
│   │   ├── BaseTest.java             # TestNG lifecycle, report hooks
│   │   ├── NavigationTest.java       # 4 navigation test cases
│   │   ├── TradingTest.java          # 6 trading test cases
│   │   ├── ContentValidationTest.java# 6 content test cases
│   │   └── CharacterFrequencyTest.java # 10 unit tests (Task 2)
│   └── utils/
│       ├── CharacterFrequency.java   # Task 2 implementation
│       ├── ExtentReportManager.java  # HTML report manager
│       ├── ScreenshotUtils.java      # Screenshot capture
│       └── TestDataLoader.java       # JSON data reader
├── src/test/resources/
│   ├── framework.properties          # All config values
│   ├── logback.xml                   # Logging config
│   └── testdata/
│       ├── navigation.json           # Nav expected values
│       ├── trading.json              # Trading pair data
│       └── content.json              # About page / banner data
├── testng.xml                        # Default suite (Chrome)
├── testng-crossbrowser.xml           # Chrome + Firefox + Edge
├── testng-unit.xml                   # Unit tests only
├── .github/workflows/qa-automation.yml  # CI/CD pipeline
└── pom.xml
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 8+ |
| Maven | 3.8+ |
| Chrome / Firefox / Edge | Latest |

> **WebDriverManager** automatically downloads the correct driver binary — no manual setup needed.

---

## Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/multibank-qa.git
cd multibank-qa
```

### 2. Run all tests (Chrome, headed)
```bash
mvn test
```

### 3. Run headless (CI mode)
```bash
mvn test -Dheadless=true
```

### 4. Run on Firefox
```bash
mvn test -Dbrowser=firefox
```

### 5. Cross-browser (Chrome + Firefox + Edge in parallel)
```bash
mvn test -DsuiteXmlFile=testng-crossbrowser.xml
```

### 6. Unit tests only (Task 2 — no browser needed)
```bash
mvn test -DsuiteXmlFile=testng-unit.xml
```

---

## Configuration

All values in `src/test/resources/framework.properties`.  
Any value can be overridden at runtime:

```bash
mvn test -Dbrowser=firefox -Dheadless=true -Dexplicit.wait.seconds=20
```

| Property | Default | Description |
|----------|---------|-------------|
| `browser` | `chrome` | chrome / firefox / edge |
| `base.url` | `https://trade.multibank.io/` | Target URL |
| `headless` | `false` | Headless mode |
| `explicit.wait.seconds` | `15` | WebDriverWait timeout |
| `screenshot.on.fail` | `true` | Auto-screenshot on failure |
| `remote.execution` | `false` | Use Selenium Grid |
| `remote.url` | `localhost:4444` | Grid hub URL |

---

## Test Cases

### Navigation Tests (NavigationTest.java)
| ID | Description |
|----|-------------|
| TC-NAV-001 | Top navigation menu is displayed |
| TC-NAV-002 | Expected navigation items are present |
| TC-NAV-003 | All nav links have valid hrefs |
| TC-NAV-004 | Navigation items count meets minimum |

### Trading Tests (TradingTest.java)
| ID | Description |
|----|-------------|
| TC-TRADE-001 | Spot trading section is displayed |
| TC-TRADE-002 | Trading categories are displayed |
| TC-TRADE-003 | Trading pairs are populated |
| TC-TRADE-004 | Trading pair names are displayed |
| TC-TRADE-005 | Pair data structure complete (name + price + change) |
| TC-TRADE-006 | Specific required pairs are visible |

### Content Validation Tests (ContentValidationTest.java)
| ID | Description |
|----|-------------|
| TC-CONTENT-001 | Marketing banners present at page bottom |
| TC-CONTENT-002 | App Store link present and points to apple.com |
| TC-CONTENT-003 | Google Play link present and points to play.google.com |
| TC-CONTENT-004 | About/Why MultiBank page renders all components |
| TC-CONTENT-005 | About page contains expected text |
| TC-CONTENT-006 | Download section is present |

### Character Frequency Unit Tests (CharacterFrequencyTest.java)
| ID | Description |
|----|-------------|
| TC-CF-001 | Correct count for "hello world" |
| TC-CF-002 | First appearance order preserved |
| TC-CF-003 | Empty string → empty output |
| TC-CF-004 | Null input → empty output |
| TC-CF-005 | Case-sensitive counting (H ≠ h) |
| TC-CF-006 | Single character |
| TC-CF-007 | All identical characters |
| TC-CF-008 | Special characters |
| TC-CF-009 | Whitespace counted |
| TC-CF-010 | Numeric characters |

---

## Test Reports

After execution, HTML report at:
```
reports/MultiBank_TestReport_YYYYMMDD_HHmmss.html
```
Screenshots on failure:
```
reports/screenshots/
```
Log file:
```
reports/automation.log
```

---

## Task 2 — Character Frequency

### Usage
```java
String result = CharacterFrequency.count("hello world");
// Output: h:1, e:1, l:3, o:2, ' ':1, w:1, r:1, d:1
```

### Run the demo
```bash
mvn compile -q && mvn exec:java \
  -Dexec.mainClass="com.multibank.utils.CharacterFrequency" \
  -Dexec.classpathScope=test
```

### Algorithm & Assumptions
- **O(n)** single-pass using `LinkedHashMap` (preserves insertion order)
- **Case-sensitive**: `H` ≠ `h`
- **Whitespace counted**: space, tab, newline are included
- **Special characters**: all Unicode supported
- **Null/empty input**: returns empty string safely

---

## Architecture Decisions

### Why Page Object Model?
Each page class owns its locators and interaction methods. Tests stay readable and locator changes require editing only one file.

### Why explicit waits only?
Implicit waits interact unpredictably with explicit waits. Every interaction in `BasePage` uses `WebDriverWait` with `ExpectedConditions` — no `Thread.sleep` anywhere.

### Why ThreadLocal for WebDriver?
Enables safe parallel execution. Each test thread owns its own driver instance with no shared state.

### Why external JSON test data?
All assertion values live in `testdata/*.json` — tests never contain hard-coded strings. Update data files when the UI changes without touching test logic.

### Why WebDriverManager?
Eliminates the brittle manual driver version matching. Automatically downloads the correct chromedriver/geckodriver/edgedriver binary.

---

## Extending the Framework

### Add a new page
1. Create `src/test/java/com/multibank/pages/NewPage.java`
2. Extend `BasePage`
3. Define locators as `private final By` fields
4. Add interaction methods using `BasePage` wait helpers

### Add a new test
1. Create `src/test/java/com/multibank/tests/NewTest.java`
2. Extend `BaseTest`
3. Add `@BeforeMethod` to instantiate the page
4. Write `@Test` methods — each independent

### Add test data
1. Create `src/test/resources/testdata/newdata.json`
2. Load with `TestDataLoader.load("newdata.json")`

---

## Requirements Verification Checklist

### Task 1: Web UI Automation Framework

#### ✅ Architecture & Design
- [x] **Clean architecture** - Page Object Model separates concerns (pages/, tests/, utils/, config/)
- [x] **Separation of concerns** - Each class has single responsibility
- [x] **Reusability** - BasePage provides common wait/interaction methods for all pages
- [x] **Extensibility** - Easy to add new pages and tests without modifying existing code

#### ✅ Robust Test Design
- [x] **Proper wait strategies** - Explicit waits via `WebDriverWait` in BasePage (no `Thread.sleep`)
- [x] **Handles real-world flakiness** - Retry logic in `safeClick()`, stale element recovery
- [x] **Timing isolation** - WebDriver timeouts configurable, separate long/short waits

#### ✅ Professional Code Quality
- [x] **Clear structure** - Well-organized package hierarchy
- [x] **Documentation** - Javadoc comments on all classes and public methods
- [x] **Logging** - SLF4J with Logback for detailed execution trace
- [x] **Error handling** - Graceful exceptions, meaningful error messages

#### ✅ Data-Driven Approach
- [x] **No hard-coded values** - All assertions reference test data JSON files
- [x] **External test data** - `navigation.json`, `trading.json`, `content.json` in `testdata/`
- [x] **TestDataLoader utility** - Centralized JSON parsing and data access
- [x] **Configuration flexibility** - Runtime property overrides supported

#### ✅ Cross-Browser Compatibility
- [x] **Chrome** - Default browser
- [x] **Firefox** - Supported via -Dbrowser=firefox
- [x] **Edge** - Supported via -Dbrowser=edge
- [x] **Thread-safe isolation** - ThreadLocal driver management in DriverFactory
- [x] **WebDriverManager** - Auto-downloads correct driver binaries

#### ✅ Core Test Scenarios

**Navigation & Layout (NavigationTest.java)**
- [x] TC-NAV-001: Top navigation menu displays correctly
- [x] TC-NAV-002: Navigation items are visible with expected labels
- [x] TC-NAV-003: All nav links have valid non-empty hrefs
- [x] TC-NAV-004: Navigation items count meets minimum threshold
- [x] Data-driven from `navigation.json`

**Trading Functionality (TradingTest.java)**
- [x] TC-TRADE-001: Spot trading section is displayed
- [x] TC-TRADE-002: Trading categories are displayed
- [x] TC-TRADE-003: Trading pairs are populated (≥ minimum count)
- [x] TC-TRADE-004: Trading pair names are displayed
- [x] TC-TRADE-005: Pair data structure complete (name + price + change)
- [x] TC-TRADE-006: Specific required pairs are visible
- [x] Data-driven from `trading.json`

**Content Validation (ContentValidationTest.java)**
- [x] TC-CONTENT-001: Marketing banners appear at page bottom
- [x] TC-CONTENT-002: App Store link present and points to apple.com
- [x] TC-CONTENT-003: Google Play link present and points to play.google.com
- [x] TC-CONTENT-004: About/Why MultiBank page renders all components
- [x] TC-CONTENT-005: About page contains expected text
- [x] TC-CONTENT-006: Download section is present
- [x] Data-driven from `content.json`

#### ✅ Technical Requirements

**Must Have**
- [x] **Selenium 4.18.1** - Modern automation tool with explicit wait support
- [x] **Page Object Model** - Implemented across all page classes
- [x] **External test data** - JSON files in testdata/ with no hard-coded values
- [x] **Cross-browser execution** - Chrome/Firefox/Edge via runtime property
- [x] **Proper wait strategies** - `WebDriverWait` with `ExpectedConditions`, no fixed sleeps
- [x] **Test reporting** - ExtentReports HTML reports with screenshots on failure
- [x] **Build automation** - Maven with pom.xml (Java 8 compatible)

**Quality Measures**
- [x] **Independent tests** - No test depends on another's state; `@BeforeMethod` isolation
- [x] **Deterministic execution** - Consistent results via explicit waits and data-driven tests
- [x] **Maintainable code** - Clean separation, single responsibility, minimal duplication
- [x] **Debuggable failures** - Detailed logs, auto-screenshots, descriptive assertions
- [x] **CI/CD ready** - GitHub Actions pipeline (`.github/workflows/qa-automation.yml`)

#### ✅ Bonus Features
- [x] **CI/CD pipeline** - GitHub Actions with matrix execution (Chrome + Firefox)
- [x] **Parallel execution** - TestNG parallel attribute in suite XMLs
- [x] **Advanced reporting** - ExtentReports with screenshots, logs, and timings
- [x] **Headless mode** - Configurable for CI environments
- [x] **Maven wrapper** - Consistent builds across environments

---

### Task 2: String Character Frequency

#### ✅ Core Requirements
- [x] **Counts character occurrences** - `count()` method tallies each char
- [x] **Output in first-appearance order** - `LinkedHashMap` preserves insertion order
- [x] **Example validates** - `"hello world"` → `h:1, e:1, l:3, o:2, ' ':1, w:1, r:1, d:1` ✓

#### ✅ Edge Cases
- [x] **Empty string** - Returns empty string safely
- [x] **Null input** - Returns empty string safely (no NPE)
- [x] **Single character** - `"a"` → `"a:1"`
- [x] **All identical chars** - `"aaaa"` → `"a:4"`
- [x] **Special characters** - Supported: `"!@#$%"`
- [x] **Whitespace** - Spaces, tabs, newlines are counted as per assumption
- [x] **Numeric characters** - Digits treated as regular characters
- [x] **Mixed case** - Case-sensitive: `"H"` ≠ `"h"`

#### ✅ Code Quality
- [x] **Readable** - Clear variable names, simple single-pass algorithm
- [x] **Efficient** - O(n) time complexity, one pass through input
- [x] **Well-documented** - Javadoc with algorithm explanation and assumptions
- [x] **Functional methods** - `count()` returns formatted string, `countAsMap()` returns raw map
- [x] **Demo main()** - Shows multiple test cases

#### ✅ Assumptions (Documented)
- [x] **Case-sensitive** - 'H' and 'h' are different characters
- [x] **Whitespace counted** - Space, tab, newline included in output
- [x] **All Unicode supported** - No restrictions on character range
- [x] **Null/empty safe** - Returns empty string rather than throwing exception

#### ✅ Unit Tests (CharacterFrequencyTest.java)
- [x] TC-CF-001: Correct count for "hello world"
- [x] TC-CF-002: First appearance order preserved
- [x] TC-CF-003: Empty string → empty output
- [x] TC-CF-004: Null input → empty output
- [x] TC-CF-005: Case-sensitive counting (H ≠ h)
- [x] TC-CF-006: Single character
- [x] TC-CF-007: All identical characters
- [x] TC-CF-008: Special characters
- [x] TC-CF-009: Whitespace counted
- [x] TC-CF-010: Numeric characters

---

## Verification Summary

| Requirement Category | Status | Notes |
|----------------------|--------|-------|
| **Architecture** | ✅ Complete | POM, thread-safety, extensible design |
| **Test Coverage** | ✅ Complete | 16 UI tests + 10 unit tests = 26 total |
| **Data-Driven** | ✅ Complete | All assertions from JSON test data |
| **Cross-Browser** | ✅ Complete | Chrome, Firefox, Edge with ThreadLocal |
| **Waits/Timing** | ✅ Complete | Explicit waits only, no fixed sleeps |
| **Reporting** | ✅ Complete | ExtentReports HTML + auto-screenshots |
| **Documentation** | ✅ Complete | README + Javadoc + inline comments |
| **Character Frequency** | ✅ Complete | All edge cases + 10 unit tests |
| **CI/CD** | ✅ Complete | GitHub Actions pipeline configured |
| **Build** | ✅ Complete | Maven with Java 8 compatibility |

---

## Quick Verification (Local)

To verify all requirements are correctly implemented:

```bash
# Run all UI tests (Chrome)
mvn test -DsuiteXmlFile=testng.xml

# Run all character frequency unit tests (Task 2)
mvn test -DsuiteXmlFile=testng-unit.xml

# Cross-browser tests (Chrome + Firefox)
mvn test -DsuiteXmlFile=testng-crossbrowser.xml

# Headless mode (CI)
mvn test -Dheadless=true

# Run Character Frequency demo
mvn exec:java -Dexec.mainClass="com.multibank.utils.CharacterFrequency" -Dexec.classpathScope=test
```

---

