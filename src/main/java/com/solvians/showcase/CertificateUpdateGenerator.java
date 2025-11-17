package com.solvians.showcase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class CertificateUpdateGenerator {

    private final int threads;
    private final int quotes;
    private final ISINGenerator isinGenerator;

    public CertificateUpdateGenerator(int threads, int quotes) {
        this.threads = threads;
        this.quotes = quotes;
        this.isinGenerator = new ISINGenerator();
    }

    public Stream<CertificateUpdate> generateQuotes() {
        try {
            List<String> csvStrings = generateCertificateUpdates();
            return csvStrings.stream()
                .map(CertificateUpdate::fromCSVString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate quotes", e);
        }
    }

    
    public List<String> generateCertificateUpdates() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<String>> futures = new ArrayList<>();

        try {
            // Submit tasks to generate certificate updates
            for (int i = 0; i < quotes; i++) {
                SingleCertificateGenerator generator = new SingleCertificateGenerator(isinGenerator);
                Future<String> future = executorService.submit(generator);
                futures.add(future);
            }

            // Collect results
            List<String> results = new ArrayList<>();
            for (Future<String> future : futures) {
                results.add(future.get());
            }

            return results;

        } finally {
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        }
    }
}
