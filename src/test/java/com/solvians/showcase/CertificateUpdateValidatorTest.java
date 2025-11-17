package com.solvians.showcase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificateUpdateValidatorTest {

    private CertificateUpdateValidator validator;
    private ISINGenerator isinGenerator;

    @BeforeEach
    void setUp() {
        isinGenerator = new ISINGenerator();
        validator = new CertificateUpdateValidator(isinGenerator);
    }

    private CertificateUpdate createValidUpdate() {
        String isin = isinGenerator.generateISIN();
        return new CertificateUpdate(
            System.currentTimeMillis(),
            isin,
            150.00,
            3000,
            155.00,
            5000
        );
    }

    @Test
    void testValidate_CompletelyValidUpdate() {
        CertificateUpdate update = createValidUpdate();
        CertificateUpdateValidator.ValidationResult result = validator.validate(update);

        assertTrue(result.isValid(), "Valid update should pass validation");
        assertTrue(result.getErrors().isEmpty(), "Valid update should have no errors");
    }

    @Test
    void testValidate_NullUpdate() {
        CertificateUpdateValidator.ValidationResult result = validator.validate(null);

        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrorMessage().contains("cannot be null"));
    }

    // Timestamp validation tests
    @Test
    void testValidateTimestamp_Valid() {
        long validTimestamp = System.currentTimeMillis();
        var errors = validator.validateTimestamp(validTimestamp);
        assertTrue(errors.isEmpty(), "Current timestamp should be valid");
    }

    @Test
    void testValidateTimestamp_Negative() {
        var errors = validator.validateTimestamp(-1000);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("positive value"));
    }

    @Test
    void testValidateTimestamp_Zero() {
        var errors = validator.validateTimestamp(0);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("positive value"));
    }

    @Test
    void testValidateTimestamp_TooOld() {
        long year1990 = 631152000000L;
        var errors = validator.validateTimestamp(year1990);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("after year 2000"));
    }

    @Test
    void testValidateTimestamp_FarFuture() {
        long farFuture = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000); // 1 year from now
        var errors = validator.validateTimestamp(farFuture);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("future"));
    }

    // ISIN validation tests
    @Test
    void testValidateISIN_Valid() {
        String validISIN = isinGenerator.generateISIN();
        var errors = validator.validateISIN(validISIN);
        assertTrue(errors.isEmpty(), "Generated ISIN should be valid");
    }

    @Test
    void testValidateISIN_Null() {
        var errors = validator.validateISIN(null);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("cannot be null"));
    }

    @Test
    void testValidateISIN_Empty() {
        var errors = validator.validateISIN("");
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("cannot be null or empty"));
    }

    @Test
    void testValidateISIN_InvalidFormat() {
        var errors = validator.validateISIN("INVALID");
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("format is invalid"));
    }

    @Test
    void testValidateISIN_WrongCheckDigit() {
        String validISIN = isinGenerator.generateISIN();
        // Change the check digit
        String invalidISIN = validISIN.substring(0, 11) + ((Integer.parseInt(validISIN.substring(11)) + 1) % 10);
        var errors = validator.validateISIN(invalidISIN);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("check digit")));
    }

    // Bid price validation tests
    @Test
    void testValidateBidPrice_Valid() {
        var errors = validator.validateBidPrice(150.00);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateBidPrice_MinBoundary() {
        var errors = validator.validateBidPrice(100.00);
        assertTrue(errors.isEmpty(), "Min boundary should be valid");
    }

    @Test
    void testValidateBidPrice_MaxBoundary() {
        var errors = validator.validateBidPrice(200.00);
        assertTrue(errors.isEmpty(), "Max boundary should be valid");
    }

    @Test
    void testValidateBidPrice_BelowMin() {
        var errors = validator.validateBidPrice(99.99);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at least 100.0"));
    }

    @Test
    void testValidateBidPrice_AboveMax() {
        var errors = validator.validateBidPrice(200.01);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at most 200.0"));
    }

    @Test
    void testValidateBidPrice_TwoDecimals() {
        var errors = validator.validateBidPrice(150.55);
        assertTrue(errors.isEmpty(), "Two decimal places should be valid");
    }

    @Test
    void testValidateBidPrice_ThreeDecimals() {
        var errors = validator.validateBidPrice(150.555);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at most 2 decimal places"));
    }

    // Bid size validation tests
    @Test
    void testValidateBidSize_Valid() {
        var errors = validator.validateBidSize(3000);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateBidSize_MinBoundary() {
        var errors = validator.validateBidSize(1000);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateBidSize_MaxBoundary() {
        var errors = validator.validateBidSize(5000);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateBidSize_BelowMin() {
        var errors = validator.validateBidSize(999);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at least 1000"));
    }

    @Test
    void testValidateBidSize_AboveMax() {
        var errors = validator.validateBidSize(5001);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at most 5000"));
    }

    @Test
    void testValidateBidSize_Negative() {
        var errors = validator.validateBidSize(-1000);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("positive")));
    }

    @Test
    void testValidateBidSize_Zero() {
        var errors = validator.validateBidSize(0);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("positive")));
    }

    // Ask price validation tests
    @Test
    void testValidateAskPrice_Valid() {
        var errors = validator.validateAskPrice(150.00);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateAskPrice_MinBoundary() {
        var errors = validator.validateAskPrice(100.00);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateAskPrice_MaxBoundary() {
        var errors = validator.validateAskPrice(200.00);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateAskPrice_BelowMin() {
        var errors = validator.validateAskPrice(99.99);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at least 100.0"));
    }

    @Test
    void testValidateAskPrice_AboveMax() {
        var errors = validator.validateAskPrice(200.01);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at most 200.0"));
    }

    @Test
    void testValidateAskPrice_ThreeDecimals() {
        var errors = validator.validateAskPrice(150.555);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at most 2 decimal places"));
    }

    // Ask size validation tests
    @Test
    void testValidateAskSize_Valid() {
        var errors = validator.validateAskSize(5000);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateAskSize_MinBoundary() {
        var errors = validator.validateAskSize(1000);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateAskSize_MaxBoundary() {
        var errors = validator.validateAskSize(10000);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateAskSize_BelowMin() {
        var errors = validator.validateAskSize(999);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at least 1000"));
    }

    @Test
    void testValidateAskSize_AboveMax() {
        var errors = validator.validateAskSize(10001);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("at most 10000"));
    }

    @Test
    void testValidateAskSize_Negative() {
        var errors = validator.validateAskSize(-5000);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("positive")));
    }

    // Business rules tests
    @Test
    void testValidate_AskPriceLessThanBidPrice() {
        CertificateUpdate update = createValidUpdate();
        update.setBidPrice(155.00);
        update.setAskPrice(150.00);

        CertificateUpdateValidator.ValidationResult result = validator.validate(update);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Ask price"));
        assertTrue(result.getErrorMessage().contains("cannot be less than bid price"));
    }

    @Test
    void testValidate_AskPriceEqualsBidPrice() {
        CertificateUpdate update = createValidUpdate();
        update.setBidPrice(150.00);
        update.setAskPrice(150.00);

        CertificateUpdateValidator.ValidationResult result = validator.validate(update);
        assertTrue(result.isValid(), "Ask price equal to bid price should be valid");
    }

    // Multiple errors tests
    @Test
    void testValidate_MultipleErrors() {
        CertificateUpdate update = new CertificateUpdate(
            -1,                 // Invalid timestamp
            "INVALID",          // Invalid ISIN
            99.99,             // Invalid bid price (too low)
            100,               // Invalid bid size (too low)
            250.00,            // Invalid ask price (too high)
            20000              // Invalid ask size (too high)
        );

        CertificateUpdateValidator.ValidationResult result = validator.validate(update);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 5, "Should have multiple validation errors");
    }

    @Test
    void testValidationResult_Valid() {
        CertificateUpdateValidator.ValidationResult result = CertificateUpdateValidator.ValidationResult.valid();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidationResult_InvalidWithSingleError() {
        CertificateUpdateValidator.ValidationResult result =
            CertificateUpdateValidator.ValidationResult.invalid("Test error");
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("Test error", result.getErrorMessage());
    }
}
