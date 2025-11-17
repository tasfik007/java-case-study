package com.solvians.showcase;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CertificateUpdateGeneratorTest {

    @Test
    public void generateQuotes() {
        int quoteCount = 100;
        CertificateUpdateGenerator certificateUpdateGenerator = new CertificateUpdateGenerator(10, quoteCount);
        Stream<CertificateUpdate> quotes = certificateUpdateGenerator.generateQuotes();
        assertNotNull(quotes);
        assertEquals(quoteCount, quotes.count());

    }
}