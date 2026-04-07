package com.multibank.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TestDataLoader
 * --------------
 * Reads JSON test data files from src/test/resources/testdata/.
 * No hard-coded assertion values in test classes — all from JSON.
 *
 * Usage:
 *   JsonNode data = TestDataLoader.load("navigation.json");
 *   List<String> navItems = TestDataLoader.getStringList(data, "expectedNavItems");
 */
public class TestDataLoader {

    private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String DATA_PATH = "testdata/";

    private TestDataLoader() {}

    /**
     * Loads a JSON file from the testdata directory and returns its root JsonNode.
     */
    public static JsonNode load(String fileName) {
        String path = DATA_PATH + fileName;
        try (InputStream is = TestDataLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Test data file not found: " + path);
            JsonNode node = mapper.readTree(is);
            log.debug("Loaded test data: {}", path);
            return node;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data: " + path, e);
        }
    }

    /**
     * Extracts a list of strings from a JSON array node at the given key.
     */
    public static List<String> getStringList(JsonNode root, String key) {
        List<String> result = new ArrayList<>();
        JsonNode node = root.get(key);
        if (node != null && node.isArray()) {
            node.forEach(n -> result.add(n.asText()));
        }
        return result;
    }

    /**
     * Returns a string value from the root node by key.
     */
    public static String getString(JsonNode root, String key) {
        JsonNode node = root.get(key);
        return (node != null) ? node.asText() : "";
    }

    /**
     * Returns an integer value from the root node by key.
     */
    public static int getInt(JsonNode root, String key, int defaultVal) {
        JsonNode node = root.get(key);
        return (node != null) ? node.asInt(defaultVal) : defaultVal;
    }

    /**
     * Returns a boolean value from the root node by key.
     */
    public static boolean getBoolean(JsonNode root, String key, boolean defaultVal) {
        JsonNode node = root.get(key);
        return (node != null) ? node.asBoolean(defaultVal) : defaultVal;
    }
}
