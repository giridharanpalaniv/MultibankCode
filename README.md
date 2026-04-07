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

### 7. Run Task 2 demo (Character Frequency - Fastest)
```bash
java -cp target/test-classes com.multibank.utils.CharacterFrequency
```

### 8. Run Task 2 standalone tests (All 10 tests)
```bash
java -cp target/test-classes com.multibank.tests.SimpleCharacterFrequencyTest
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

### Run the demo (Direct Java - Recommended)
```bash
java -cp target/test-classes com.multibank.utils.CharacterFrequency
```

**Output:**
```
=== Character Frequency Counter ===

Input : "hello world"       
Output: h:1, e:1, l:3, o:2,  :1, w:1, r:1, d:1

Input : "aabbcc"            
Output: a:2, b:2, c:2

Input : "Hello World"       
Output: H:1, e:1, l:3, o:2,  :1, W:1, r:1, d:1
```

### Run the standalone tests
```bash
java -cp target/test-classes com.multibank.tests.SimpleCharacterFrequencyTest
```

### Run unit tests with TestNG (Alternative)
```bash
mvn test -DsuiteXmlFile=testng-unit.xml
```

### Run the demo with Maven (Alternative)
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


