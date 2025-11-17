package com.solvians.showcase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    public App(String threads, String quotes) {

    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            int threads = Integer.parseInt(args[0]);
            int quotes = Integer.parseInt(args[1]);

            CertificateUpdateGenerator certificateUpdateGenerator = new CertificateUpdateGenerator(threads, quotes);
            List<CertificateUpdate> updates = certificateUpdateGenerator.generateQuotes().collect(Collectors.toList());
            
            // Print all generated updates
            for (CertificateUpdate update : updates) {
                System.out.println(update.toCSVString());
            }
        } else {
            throw new RuntimeException("Expect at least number of threads and number of quotes. But got: " + args);
        }
    }
}
