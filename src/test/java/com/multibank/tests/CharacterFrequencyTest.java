package com.multibank.tests;

import com.multibank.utils.CharacterFrequency;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * CharacterFrequencyTest
 * ----------------------
 * Unit tests for Task 2: CharacterFrequency utility.
 * No WebDriver needed — pure unit tests.
 */
public class CharacterFrequencyTest {

    @Test(description = "TC-CF-001: Correct frequency count for 'hello world'")
    public void testHelloWorld() {
        String result = CharacterFrequency.count("hello world");
        // Expected: h:1, e:1, l:3, o:2, ' ':1, w:1, r:1, d:1
        Assert.assertTrue(result.contains("h:1"), "h count wrong");
        Assert.assertTrue(result.contains("e:1"), "e count wrong");
        Assert.assertTrue(result.contains("l:3"), "l count wrong");
        Assert.assertTrue(result.contains("o:2"), "o count wrong");
        Assert.assertTrue(result.contains("w:1"), "w count wrong");
        Assert.assertTrue(result.contains("r:1"), "r count wrong");
        Assert.assertTrue(result.contains("d:1"), "d count wrong");
    }

    @Test(description = "TC-CF-002: First appearance order is preserved")
    public void testFirstAppearanceOrder() {
        Map<Character, Integer> map = CharacterFrequency.countAsMap("hello world");
        Object[] keys = map.keySet().toArray();
        Assert.assertEquals(keys[0], 'h', "First char should be h");
        Assert.assertEquals(keys[1], 'e', "Second char should be e");
        Assert.assertEquals(keys[2], 'l', "Third char should be l");
        Assert.assertEquals(keys[3], 'o', "Fourth char should be o");
    }

    @Test(description = "TC-CF-003: Empty string returns empty output")
    public void testEmptyString() {
        Assert.assertEquals(CharacterFrequency.count(""), "");
    }

    @Test(description = "TC-CF-004: Null input returns empty output")
    public void testNullInput() {
        Assert.assertEquals(CharacterFrequency.count(null), "");
    }

    @Test(description = "TC-CF-005: Case-sensitive counting (H != h)")
    public void testCaseSensitivity() {
        Map<Character, Integer> map = CharacterFrequency.countAsMap("HhHh");
        Assert.assertEquals(map.get('H').intValue(), 2, "H count should be 2");
        Assert.assertEquals(map.get('h').intValue(), 2, "h count should be 2");
    }

    @Test(description = "TC-CF-006: Single character string")
    public void testSingleChar() {
        Assert.assertEquals(CharacterFrequency.count("a"), "a:1");
    }

    @Test(description = "TC-CF-007: All identical characters")
    public void testAllIdentical() {
        String result = CharacterFrequency.count("aaaa");
        Assert.assertEquals(result, "a:4");
    }

    @Test(description = "TC-CF-008: Special characters counted correctly")
    public void testSpecialCharacters() {
        Map<Character, Integer> map = CharacterFrequency.countAsMap("!@!@");
        Assert.assertEquals(map.get('!').intValue(), 2);
        Assert.assertEquals(map.get('@').intValue(), 2);
    }

    @Test(description = "TC-CF-009: Whitespace is counted")
    public void testWhitespaceCounted() {
        Map<Character, Integer> map = CharacterFrequency.countAsMap("a b");
        Assert.assertTrue(map.containsKey(' '), "Space should be counted");
        Assert.assertEquals(map.get(' ').intValue(), 1);
    }

    @Test(description = "TC-CF-010: Numeric characters handled correctly")
    public void testNumericCharacters() {
        String result = CharacterFrequency.count("12321");
        Assert.assertTrue(result.contains("1:2"), "1 should appear twice");
        Assert.assertTrue(result.contains("2:2"), "2 should appear twice");
        Assert.assertTrue(result.contains("3:1"), "3 should appear once");
    }
}
