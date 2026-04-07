package com.multibank.tests;

import com.multibank.utils.CharacterFrequency;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * CharacterFrequencyTest
 * ----------------------
 * Unit tests for Task 2: CharacterFrequency utility.
 * Tests character frequency counting for different types of strings.
 */
public class CharacterFrequencyTest {

    @Test(description = "Test frequency count for 'hello world'")
    public void testHelloWorld() {
        String result = CharacterFrequency.count("hello world");
        // Check that each character appears the correct number of times
        Assert.assertTrue(result.contains("h:1"), "h should appear 1 time");
        Assert.assertTrue(result.contains("e:1"), "e should appear 1 time");
        Assert.assertTrue(result.contains("l:3"), "l should appear 3 times");
        Assert.assertTrue(result.contains("o:2"), "o should appear 2 times");
        Assert.assertTrue(result.contains("w:1"), "w should appear 1 time");
        Assert.assertTrue(result.contains("r:1"), "r should appear 1 time");
        Assert.assertTrue(result.contains("d:1"), "d should appear 1 time");
        Assert.assertTrue(result.contains(" :1"), "space should appear 1 time");
    }

    @Test(description = "Test frequency count for repeated characters")
    public void testRepeatedCharacters() {
        String result = CharacterFrequency.count("aabbcc");
        Assert.assertEquals(result, "a:2, b:2, c:2", "Should count each character correctly");
    }

    @Test(description = "Test case sensitivity")
    public void testCaseSensitivity() {
        String result = CharacterFrequency.count("Hello");
        Assert.assertTrue(result.contains("H:1"), "Uppercase H should be counted separately");
        Assert.assertTrue(result.contains("e:1"), "Lowercase e should be counted");
        Assert.assertTrue(result.contains("l:2"), "Lowercase l should appear twice");
        Assert.assertTrue(result.contains("o:1"), "Lowercase o should be counted");
    }

    @Test(description = "Test empty string")
    public void testEmptyString() {
        String result = CharacterFrequency.count("");
        Assert.assertEquals(result, "", "Empty string should return empty result");
    }

    @Test(description = "Test null input")
    public void testNullInput() {
        String result = CharacterFrequency.count(null);
        Assert.assertEquals(result, "", "Null input should return empty result");
    }

    @Test(description = "Test single character")
    public void testSingleCharacter() {
        String result = CharacterFrequency.count("a");
        Assert.assertEquals(result, "a:1", "Single character should be counted once");
    }

    @Test(description = "Test numbers")
    public void testNumbers() {
        String result = CharacterFrequency.count("12321");
        Assert.assertTrue(result.contains("1:2"), "Number 1 should appear twice");
        Assert.assertTrue(result.contains("2:2"), "Number 2 should appear twice");
        Assert.assertTrue(result.contains("3:1"), "Number 3 should appear once");
    }

    @Test(description = "Test special characters")
    public void testSpecialCharacters() {
        String result = CharacterFrequency.count("!@!");
        Assert.assertTrue(result.contains("!:2"), "! should appear twice");
        Assert.assertTrue(result.contains("@:1"), "@ should appear once");
    }

    @Test(description = "Test spaces")
    public void testSpaces() {
        String result = CharacterFrequency.count("a b c");
        Assert.assertTrue(result.contains("a:1"), "a should appear once");
        Assert.assertTrue(result.contains("b:1"), "b should appear once");
        Assert.assertTrue(result.contains("c:1"), "c should appear once");
        Assert.assertTrue(result.contains(" :2"), "space should appear twice");
    }

    @Test(description = "Test order preservation")
    public void testOrderPreservation() {
        Map<Character, Integer> map = CharacterFrequency.countAsMap("abc");
        Object[] keys = map.keySet().toArray();
        Assert.assertEquals(keys[0], 'a', "First character should be 'a'");
        Assert.assertEquals(keys[1], 'b', "Second character should be 'b'");
        Assert.assertEquals(keys[2], 'c', "Third character should be 'c'");
    }

    @Test(description = "Test different string inputs")
    public void testVariousStrings() {
        // Test different types of strings to show it works for any input
        String[] testInputs = {
            "test",
            "programming",
            "Hello World",
            "123",
            "!@#"
        };

        for (String input : testInputs) {
            String result = CharacterFrequency.count(input);
            // Just verify that we get some result and it's not empty for non-empty input
            if (input != null && !input.isEmpty()) {
                Assert.assertFalse(result.isEmpty(), "Should produce output for input: " + input);
                Assert.assertTrue(result.contains(":"), "Result should contain ':' separators");
            } else {
                Assert.assertEquals(result, "", "Empty/null input should give empty result");
            }
        }
    }
}
