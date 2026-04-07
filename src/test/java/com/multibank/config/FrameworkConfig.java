package com.multibank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * FrameworkConfig
 * ---------------
 * Loads framework.properties and exposes typed getters.
 * All values can be overridden by system/env properties at runtime:
 *   mvn test -Dbrowser=firefox -Denv=staging
 */
public class FrameworkConfig {

    private static final Logger log = LoggerFactory.getLogger(FrameworkConfig.class);
    private static final Properties props = new Properties();
    private static FrameworkConfig instance;

    private FrameworkConfig() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("framework.properties")) {
            if (is != null) {
                props.load(is);
                log.info("Loaded framework.properties");
            } else {
                log.warn("framework.properties not found - using defaults");
            }
        } catch (IOException e) {
            log.error("Failed to load framework.properties", e);
        }
    }

    public static synchronized FrameworkConfig getInstance() {
        if (instance == null) instance = new FrameworkConfig();
        return instance;
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private String get(String key, String defaultVal) {
        // System property takes precedence over file property
        String sys = System.getProperty(key);
        if (sys != null && !sys.trim().isEmpty()) return sys.trim();
        String env = System.getenv(key.toUpperCase().replace(".", "_"));
        if (env != null && !env.trim().isEmpty()) return env.trim();
        return props.getProperty(key, defaultVal).trim();
    }

    private int getInt(String key, int def) {
        try { return Integer.parseInt(get(key, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }

    private boolean getBool(String key, boolean def) {
        return Boolean.parseBoolean(get(key, String.valueOf(def)));
    }

    // ── public API ───────────────────────────────────────────────────────────

    public String getBrowser()          { return get("browser", "chrome"); }
    public String getBaseUrl()          { return get("base.url", "https://trade.multibank.io/"); }
    public int    getImplicitWait()     { return getInt("implicit.wait.seconds", 0); }
    public int    getExplicitWait()     { return getInt("explicit.wait.seconds", 15); }
    public int    getPageLoadTimeout()  { return getInt("page.load.timeout", 30); }
    public boolean isHeadless()         { return getBool("headless", false); }
    public boolean takesScreenshotOnFail() { return getBool("screenshot.on.fail", true); }
    public String getReportDir()        { return get("report.dir", "reports"); }
    public String getTestDataDir()      { return get("testdata.dir", "testdata"); }
    public boolean isRemote()           { return getBool("remote.execution", false); }
    public String getRemoteUrl()        { return get("remote.url", "http://localhost:4444/wd/hub"); }
}
