package com.solvians.showcase;

import java.util.concurrent.ThreadLocalRandom;


public class ISINGenerator {

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public String generateISIN() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Generate 2 uppercase letters for country code
        String countryCode = generateRandomString(UPPERCASE_LETTERS, 2, random);

        // Generate 9 alphanumeric characters
        String alphanumeric = generateRandomString(ALPHANUMERIC, 9, random);

        // Combine and calculate check digit
        String isinWithoutCheckDigit = countryCode + alphanumeric;
        int checkDigit = calculateCheckDigit(isinWithoutCheckDigit);

        return isinWithoutCheckDigit + checkDigit;
    }

    public boolean validateISIN(String isin) {
        if (!validateISINFormat(isin)) {
            return false;
        }
        return validateCheckDigit(isin);
    }

    public boolean validateISINFormat(String isin) {
        if (isin == null || isin.length() != 12) {
            return false;
        }

        // First 2 characters must be uppercase letters
        for (int i = 0; i < 2; i++) {
            char c = isin.charAt(i);
            if (c < 'A' || c > 'Z') {
                return false;
            }
        }

        // Next 9 characters must be alphanumeric
        for (int i = 2; i < 11; i++) {
            char c = isin.charAt(i);
            if (!((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
                return false;
            }
        }

        // Last character must be a digit
        char lastChar = isin.charAt(11);
        if (lastChar < '0' || lastChar > '9') {
            return false;
        }

        return true;
    }
    
    public boolean validateCheckDigit(String isin) {
        if (isin == null || isin.length() != 12) {
            return false;
        }

        String isinWithoutCheckDigit = isin.substring(0, 11);
        int expectedCheckDigit = calculateCheckDigit(isinWithoutCheckDigit);
        int actualCheckDigit = Character.getNumericValue(isin.charAt(11));

        return expectedCheckDigit == actualCheckDigit;
    }
    
    public int calculateCheckDigit(String isinWithoutCheckDigit) {
        StringBuilder numericString = new StringBuilder();
        for (char c : isinWithoutCheckDigit.toCharArray()) {
            if (Character.isLetter(c)) {
                int value = c - 'A' + 10;
                numericString.append(value);
            } else {
                numericString.append(c);
            }
        }

        String digits = numericString.toString();
        int sum = 0;
        
        for (int i = digits.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(digits.charAt(i));
            int positionFromRight = digits.length() - 1 - i;

            if (positionFromRight % 2 == 0) {
                digit *= 2;
            }

            if (digit > 9) {
                sum += (digit / 10) + (digit % 10);
            } else {
                sum += digit;
            }
        }

        int nextMultipleOfTen = ((sum / 10) + 1) * 10;
        if (sum % 10 == 0) {
            return 0;
        }
        return nextMultipleOfTen - sum;
    }
    
    private String generateRandomString(String charset, int length, ThreadLocalRandom random) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            result.append(charset.charAt(index));
        }
        return result.toString();
    }
}
