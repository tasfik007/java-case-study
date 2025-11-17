package com.solvians.showcase;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;


class SingleCertificateGenerator implements Callable<String> {
    private final ISINGenerator isinGenerator;

    public SingleCertificateGenerator(ISINGenerator isinGenerator) {
        this.isinGenerator = isinGenerator;
    }

    @Override
    public String call() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long timestamp = System.currentTimeMillis();
        String isin = isinGenerator.generateISIN();
        
        double bidPrice = generatePrice(random);

        int bidSize = random.nextInt(1000, 5001);

        double askPrice = generateAskPrice(random, bidPrice);

        int askSize = random.nextInt(1000, 10001);

        CertificateUpdate update = new CertificateUpdate(timestamp, isin, bidPrice, bidSize, askPrice, askSize);
        return update.toCSVString();
    }

    private double generatePrice(ThreadLocalRandom random) {
        int cents = random.nextInt(10000, 20001); // 100.00 → 200.00
        return cents / 100.0;
    }

    private double generateAskPrice(ThreadLocalRandom random, double bidPrice) {
        int cents = random.nextInt((int) (bidPrice * 100), 20001); // bidPrice → 200.00
        return cents / 100.0;
    }
}
