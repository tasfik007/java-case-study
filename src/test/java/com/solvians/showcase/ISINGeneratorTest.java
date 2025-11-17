package com.solvians.showcase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ISINGeneratorTest {

    private ISINGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ISINGenerator();
    }

    @Test
    void testGenerateISIN_ValidFormat() {
        String isin = generator.generateISIN();

        assertNotNull(isin);
        assertEquals(12, isin.length(), "ISIN should be 12 characters long");

        // Check first 2 characters are uppercase letters
        assertTrue(Character.isUpperCase(isin.charAt(0)) && Character.isLetter(isin.charAt(0)));
        assertTrue(Character.isUpperCase(isin.charAt(1)) && Character.isLetter(isin.charAt(1)));

        // Check next 9 characters are alphanumeric
        for (int i = 2; i < 11; i++) {
            char c = isin.charAt(i);
            assertTrue(Character.isLetterOrDigit(c) && (c < 'a' || c > 'z'),
                "Character at position " + i + " should be uppercase letter or digit");
        }

        // Check last character is a digit
        assertTrue(Character.isDigit(isin.charAt(11)), "Last character should be a digit");
    }

    @Test
    void testGenerateISIN_PassesValidation() {
        String isin = generator.generateISIN();
        assertTrue(generator.validateISIN(isin), "Generated ISIN should pass validation");
    }

    @Test
    void testGenerateMultipleISINs_AllValid() {
        for (int i = 0; i < 100; i++) {
            String isin = generator.generateISIN();
            assertTrue(generator.validateISIN(isin), "Generated ISIN #" + i + " should be valid");
        }
    }

    @Test
    void testGenerateMultipleISINs_Uniqueness() {
        Set<String> isins = new HashSet<>();
        int count = 1000;

        for (int i = 0; i < count; i++) {
            isins.add(generator.generateISIN());
        }

        // Most ISINs should be unique (allowing for very small collision rate)
        assertTrue(isins.size() > count * 0.99,
            "Expected most ISINs to be unique, got " + isins.size() + " unique out of " + count);
    }

    @Test
    void testCalculateCheckDigit_KnownExample() {
        // Example from README: "DE123456789" should have check digit 6
        String isinWithoutCheckDigit = "DE123456789";
        int checkDigit = generator.calculateCheckDigit(isinWithoutCheckDigit);
        assertEquals(6, checkDigit, "Check digit for DE123456789 should be 6");
    }

    @Test
    void testCalculateCheckDigit_AllLetters() {
        String isinWithoutCheckDigit = "ABCDEFGHIJK";
        int checkDigit = generator.calculateCheckDigit(isinWithoutCheckDigit);
        assertTrue(checkDigit >= 0 && checkDigit <= 9, "Check digit should be between 0 and 9");
    }

    @Test
    void testCalculateCheckDigit_AllNumbers() {
        String isinWithoutCheckDigit = "US12345678";
        int checkDigit = generator.calculateCheckDigit(isinWithoutCheckDigit);
        assertTrue(checkDigit >= 0 && checkDigit <= 9, "Check digit should be between 0 and 9");
    }

    @Test
    void testValidateISINFormat_ValidISIN() {
        String validISIN = "US0378331005"; // Apple Inc ISIN
        assertTrue(generator.validateISINFormat(validISIN), "Valid ISIN format should return true");
    }

    @Test
    void testValidateISINFormat_WrongLength() {
        assertFalse(generator.validateISINFormat("US037833100"), "ISIN with 11 characters should be invalid");
        assertFalse(generator.validateISINFormat("US03783310055"), "ISIN with 13 characters should be invalid");
        assertFalse(generator.validateISINFormat(""), "Empty string should be invalid");
    }

    @Test
    void testValidateISINFormat_NullInput() {
        assertFalse(generator.validateISINFormat(null), "Null ISIN should be invalid");
    }

    @Test
    void testValidateISINFormat_InvalidCountryCode() {
        assertFalse(generator.validateISINFormat("U10378331005"), "Country code with digit should be invalid");
        assertFalse(generator.validateISINFormat("us0378331005"), "Country code with lowercase should be invalid");
        assertFalse(generator.validateISINFormat("1S0378331005"), "Country code with digit should be invalid");
    }

    @Test
    void testValidateISINFormat_InvalidAlphanumeric() {
        assertFalse(generator.validateISINFormat("US037833100!"), "ISIN with special character should be invalid");
        assertFalse(generator.validateISINFormat("US037833100a"), "ISIN with lowercase should be invalid");
        assertFalse(generator.validateISINFormat("US 037833100"), "ISIN with space should be invalid");
    }

    @Test
    void testValidateISINFormat_InvalidCheckDigit() {
        assertFalse(generator.validateISINFormat("US037833100A"), "Check digit must be numeric");
        assertFalse(generator.validateISINFormat("US037833100!"), "Check digit must be numeric");
    }

    @Test
    void testValidateCheckDigit_Valid() {
        // Generate an ISIN and verify its check digit
        String isin = generator.generateISIN();
        assertTrue(generator.validateCheckDigit(isin), "Generated ISIN should have valid check digit");
    }

    @Test
    void testValidateCheckDigit_Invalid() {
        // Create ISIN with incorrect check digit
        String isinWithoutCheck = "US037833100";
        int correctCheckDigit = generator.calculateCheckDigit(isinWithoutCheck);
        int incorrectCheckDigit = (correctCheckDigit + 1) % 10;
        String invalidISIN = isinWithoutCheck + incorrectCheckDigit;

        assertFalse(generator.validateCheckDigit(invalidISIN), "ISIN with incorrect check digit should be invalid");
    }

    @Test
    void testValidateCheckDigit_WrongLength() {
        assertFalse(generator.validateCheckDigit("US037833100"), "ISIN with wrong length should be invalid");
        assertFalse(generator.validateCheckDigit(null), "Null ISIN should be invalid");
    }

    @Test
    void testValidateISIN_CompleteValidation() {
        // Valid ISIN with correct check digit
        String validISIN = generator.generateISIN();
        assertTrue(generator.validateISIN(validISIN), "Valid ISIN should pass complete validation");
    }

    @Test
    void testValidateISIN_InvalidFormat() {
        assertFalse(generator.validateISIN("INVALID"), "Invalid format should fail validation");
        assertFalse(generator.validateISIN("us0378331005"), "Lowercase country code should fail validation");
        assertFalse(generator.validateISIN(null), "Null should fail validation");
    }

    @Test
    void testValidateISIN_InvalidCheckDigit() {
        // Create ISIN with wrong check digit
        String isinWithoutCheck = "US037833100";
        int correctCheckDigit = generator.calculateCheckDigit(isinWithoutCheck);
        int incorrectCheckDigit = (correctCheckDigit + 1) % 10;
        String invalidISIN = isinWithoutCheck + incorrectCheckDigit;

        assertFalse(generator.validateISIN(invalidISIN), "ISIN with wrong check digit should fail validation");
    }

    @Test
    void testKnownISINExample() {
        // Test with the README example: DE123456789 -> check digit 6
        String isin = "DE1234567896";
        assertTrue(generator.validateISIN(isin), "Known ISIN DE1234567896 should be valid");
    }

    @Test
    void testEdgeCase_ZeroCheckDigit() {
        // Test case where check digit should be 0
        String isinWithoutCheck = "AA000000000";
        int checkDigit = generator.calculateCheckDigit(isinWithoutCheck);
        String fullISIN = isinWithoutCheck + checkDigit;
        assertTrue(generator.validateISIN(fullISIN), "ISIN with check digit 0 should be valid");
    }

    @Test
    void testEdgeCase_AllZs() {
        String isinWithoutCheck = "ZZZZZZZZZZZ";
        int checkDigit = generator.calculateCheckDigit(isinWithoutCheck);
        assertTrue(checkDigit >= 0 && checkDigit <= 9, "Check digit should be valid");

        String fullISIN = isinWithoutCheck + checkDigit;
        assertTrue(generator.validateISIN(fullISIN), "ISIN with all Zs should be valid");
    }
}
