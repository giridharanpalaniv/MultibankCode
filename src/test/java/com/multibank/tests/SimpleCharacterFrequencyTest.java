package com.multibank.tests;

import com.multibank.utils.CharacterFrequency;

/**
 * SimpleCharacterFrequencyTest
 *
 * A simple standalone test runner that doesn't require TestNG.
 * Tests the CharacterFrequency utility with various string inputs.
 */
public class SimpleCharacterFrequencyTest {

    public static void main(String[] args) {
        System.out.println("=== Testing CharacterFrequency Utility ===\n");

        int testsPassed = 0;
        int testsFailed = 0;

        // Test 1: Hello World
        System.out.println("Test 1: 'hello world'");
        String result1 = CharacterFrequency.count("hello world");
        if (result1.contains("h:1") && result1.contains("l:3") && result1.contains("o:2")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result1);
            testsFailed++;
        }

        // Test 2: Repeated Characters
        System.out.println("\nTest 2: 'aabbcc'");
        String result2 = CharacterFrequency.count("aabbcc");
        if (result2.equals("a:2, b:2, c:2")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result2);
            testsFailed++;
        }

        // Test 3: Case Sensitivity
        System.out.println("\nTest 3: 'Hello'");
        String result3 = CharacterFrequency.count("Hello");
        if (result3.contains("H:1") && result3.contains("e:1") && result3.contains("l:2")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result3);
            testsFailed++;
        }

        // Test 4: Empty String
        System.out.println("\nTest 4: Empty String");
        String result4 = CharacterFrequency.count("");
        if (result4.isEmpty()) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result4);
            testsFailed++;
        }

        // Test 5: Null Input
        System.out.println("\nTest 5: Null Input");
        String result5 = CharacterFrequency.count(null);
        if (result5.isEmpty()) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result5);
            testsFailed++;
        }

        // Test 6: Single Character
        System.out.println("\nTest 6: Single Character 'a'");
        String result6 = CharacterFrequency.count("a");
        if (result6.equals("a:1")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result6);
            testsFailed++;
        }

        // Test 7: Numbers
        System.out.println("\nTest 7: Numbers '12321'");
        String result7 = CharacterFrequency.count("12321");
        if (result7.contains("1:2") && result7.contains("2:2") && result7.contains("3:1")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result7);
            testsFailed++;
        }

        // Test 8: Special Characters
        System.out.println("\nTest 8: Special Characters '!@!'");
        String result8 = CharacterFrequency.count("!@!");
        if (result8.contains("!:2") && result8.contains("@:1")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result8);
            testsFailed++;
        }

        // Test 9: Spaces
        System.out.println("\nTest 9: Spaces 'a b c'");
        String result9 = CharacterFrequency.count("a b c");
        if (result9.contains("a:1") && result9.contains("b:1") && result9.contains("c:1") && result9.contains(" :2")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result9);
            testsFailed++;
        }

        // Test 10: Order Preservation
        System.out.println("\nTest 10: Order Preservation 'abc'");
        String result10 = CharacterFrequency.count("abc");
        if (result10.equals("a:1, b:1, c:1")) {
            System.out.println("✓ PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ FAILED - Got: " + result10);
            testsFailed++;
        }

        // Summary
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Total Tests:  " + (testsPassed + testsFailed));
        System.out.println("=".repeat(50));

        if (testsFailed == 0) {
            System.out.println("\n✓ All tests PASSED!");
            System.exit(0);
        } else {
            System.out.println("\n✗ Some tests FAILED!");
            System.exit(1);
        }
    }
}

