package com.solvians.showcase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CertificateUpdateGeneratorTest {

    private ISINGenerator isinGenerator;
    private CertificateUpdateValidator validator;

    @BeforeEach
    void setUp() {
        isinGenerator = new ISINGenerator();
        validator = new CertificateUpdateValidator(isinGenerator);
    }

    @Test
    void testGenerateQuotes_ReturnsStream() {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 10);
        Stream<CertificateUpdate> quotes = generator.generateQuotes();

        assertNotNull(quotes);
        assertEquals(10, quotes.count());
    }

    @Test
    void testGenerateQuotes_AllUpdatesValid() {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 10);
        Stream<CertificateUpdate> quotes = generator.generateQuotes();

        quotes.forEach(update -> {
            CertificateUpdateValidator.ValidationResult result = validator.validate(update);
            assertTrue(result.isValid(), "Generated update should be valid: " + result.getErrorMessage());
        });
    }

    @Test
    void testGenerateCertificateUpdates_GeneratesCorrectCount() throws Exception {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 10);
        List<String> updates = generator.generateCertificateUpdates();

        assertNotNull(updates);
        assertEquals(10, updates.size(), "Should generate exactly 10 updates");
    }

    @Test
    void testGenerateCertificateUpdates_AllValidCSV() throws Exception {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(3, 30);
        List<String> updates = generator.generateCertificateUpdates();

        for (String csvString : updates) {
            assertNotNull(csvString);
            assertFalse(csvString.isEmpty());

            String[] parts = csvString.split(",");
            assertEquals(6, parts.length, "CSV string should have 6 fields");

            CertificateUpdate update = CertificateUpdate.fromCSVString(csvString);
            CertificateUpdateValidator.ValidationResult result = validator.validate(update);
            assertTrue(result.isValid(), "Generated update should be valid: " + result.getErrorMessage());
        }
    }

    @Test
    void testGenerateCertificateUpdates_ISINsValid() throws Exception {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 20);
        List<String> updates = generator.generateCertificateUpdates();

        for (String csvString : updates) {
            CertificateUpdate update = CertificateUpdate.fromCSVString(csvString);
            assertTrue(isinGenerator.validateISIN(update.getIsin()),
                "Generated ISIN should be valid: " + update.getIsin());
        }
    }

    @Test
    void testGenerateCertificateUpdates_PricesInRange() throws Exception {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 50);
        List<String> updates = generator.generateCertificateUpdates();

        for (String csvString : updates) {
            CertificateUpdate update = CertificateUpdate.fromCSVString(csvString);

            assertTrue(update.getBidPrice() >= 100.00 && update.getBidPrice() <= 200.00,
                "Bid price should be in range: " + update.getBidPrice());
            assertTrue(update.getAskPrice() >= 100.00 && update.getAskPrice() <= 200.00,
                "Ask price should be in range: " + update.getAskPrice());
            assertTrue(update.getAskPrice() >= update.getBidPrice(),
                "Ask price should be >= bid price");
        }
    }

    @Test
    void testGenerateCertificateUpdates_SizesInRange() throws Exception {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 50);
        List<String> updates = generator.generateCertificateUpdates();

        for (String csvString : updates) {
            CertificateUpdate update = CertificateUpdate.fromCSVString(csvString);

            assertTrue(update.getBidSize() >= 1000 && update.getBidSize() <= 5000,
                "Bid size should be in range: " + update.getBidSize());
            assertTrue(update.getAskSize() >= 1000 && update.getAskSize() <= 10000,
                "Ask size should be in range: " + update.getAskSize());
        }
    }

    @Test
    void testGenerateCertificateUpdates_MultiThreaded() throws Exception {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(10, 100);
        List<String> updates = generator.generateCertificateUpdates();

        assertNotNull(updates);
        assertEquals(100, updates.size());

        // Verify all are valid
        for (String csvString : updates) {
            CertificateUpdate update = CertificateUpdate.fromCSVString(csvString);
            CertificateUpdateValidator.ValidationResult result = validator.validate(update);
            assertTrue(result.isValid(), "All generated updates should be valid");
        }
    }

    @Test
    void testGenerateCertificateUpdates_SingleThread() throws Exception {
        CertificateUpdateGenerator generator = new CertificateUpdateGenerator(1, 5);
        List<String> updates = generator.generateCertificateUpdates();

        assertNotNull(updates);
        assertEquals(5, updates.size());
    }
}
