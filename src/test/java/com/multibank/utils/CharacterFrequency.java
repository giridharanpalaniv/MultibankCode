package com.multibank.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CharacterFrequency — Task 2
 * ----------------------------
 * Counts character occurrences in a string and outputs them
 * in order of FIRST APPEARANCE.
 *
 * Assumptions:
 *  - Case-SENSITIVE: 'H' and 'h' are treated as different characters
 *  - Whitespace IS counted and included in output (e.g., space character)
 *  - All Unicode characters are supported
 *  - Null input returns empty string
 *  - Empty input returns empty string
 *
 * Algorithm:
 *  - Single pass O(n) using LinkedHashMap which preserves insertion order
 *  - First occurrence of each char inserts it into the map
 *  - Subsequent occurrences increment the existing count
 *
 * Example:
 *  Input:  "hello world"
 *  Output: h:1, e:1, l:3, o:2, ' ':1, w:1, r:1, d:1
 *
 *  (Note: space between 'hello' and 'world' is counted as per assumption above)
 */
public class CharacterFrequency {

    private CharacterFrequency() {}

    /**
     * Counts character frequencies and returns a formatted string.
     *
     * @param input the input string (may be null or empty)
     * @return formatted output "char:count, char:count, ..."
     *         or empty string if input is null/empty
     */
    public static String count(String input) {
        if (input == null || input.isEmpty()) return "";

        // LinkedHashMap preserves insertion (first appearance) order
        Map<Character, Integer> frequencyMap = new LinkedHashMap<>();

        for (char c : input.toCharArray()) {
            frequencyMap.merge(c, 1, Integer::sum);
        }

        // Build output string
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }

        return sb.toString();
    }

    /**
     * Returns the raw frequency map (useful for programmatic access or testing).
     *
     * @param input the input string
     * @return LinkedHashMap of char → count, in first-appearance order
     */
    public static Map<Character, Integer> countAsMap(String input) {
        Map<Character, Integer> map = new LinkedHashMap<>();
        if (input == null || input.isEmpty()) return map;
        for (char c : input.toCharArray()) {
            map.merge(c, 1, Integer::sum);
        }
        return map;
    }

    // ── Demo main ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        String[] testInputs = {
            "hello world",
            "aabbcc",
            "Hello World",       // case-sensitive: H≠h, W≠w
            "  spaces  ",        // leading/trailing spaces
            "12321",             // numeric
            "",                  // empty
            null,                // null
            "AaAaA",            // mixed case
            "!@#$%",            // special characters
        };

        System.out.println("=== Character Frequency Counter ===\n");
        for (String input : testInputs) {
            String display = input == null ? "null" : "\"" + input + "\"";
            String result  = count(input);
            System.out.printf("Input : %-20s%n", display);
            System.out.printf("Output: %s%n%n", result.isEmpty() ? "(empty)" : result);
        }
    }
}
