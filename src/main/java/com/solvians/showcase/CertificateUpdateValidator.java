package com.solvians.showcase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CertificateUpdateValidator {

    private static final double MIN_PRICE = 100.00;
    private static final double MAX_PRICE = 200.00;
    private static final int MIN_BID_SIZE = 1000;
    private static final int MAX_BID_SIZE = 5000;
    private static final int MIN_ASK_SIZE = 1000;
    private static final int MAX_ASK_SIZE = 10000;
    private static final long MIN_TIMESTAMP = 946684800000L; // Year 2000

    private final ISINGenerator isinGenerator;

    public CertificateUpdateValidator() {
        this.isinGenerator = new ISINGenerator();
    }

    public CertificateUpdateValidator(ISINGenerator isinGenerator) {
        this.isinGenerator = isinGenerator;
    }

    public ValidationResult validate(CertificateUpdate update) {
        if (update == null) {
            return ValidationResult.invalid("Certificate update cannot be null");
        }

        List<String> errors = new ArrayList<>();

        errors.addAll(validateTimestamp(update.getTimestamp()));
        errors.addAll(validateISIN(update.getIsin()));
        errors.addAll(validateBidPrice(update.getBidPrice()));
        errors.addAll(validateBidSize(update.getBidSize()));
        errors.addAll(validateAskPrice(update.getAskPrice()));
        errors.addAll(validateAskSize(update.getAskSize()));

        if (update.getAskPrice() < update.getBidPrice()) {
            errors.add("Ask price (" + update.getAskPrice() + ") cannot be less than bid price (" + update.getBidPrice() + ")");
        }

        if (errors.isEmpty()) {
            return ValidationResult.valid();
        } else {
            return ValidationResult.invalid(errors);
        }
    }

    public List<String> validateTimestamp(long timestamp) {
        List<String> errors = new ArrayList<>();

        if (timestamp <= 0) {
            errors.add("Timestamp must be a positive value");
        } else if (timestamp < MIN_TIMESTAMP) {
            errors.add("Timestamp must be after year 2000 (got: " + timestamp + ")");
        } else if (timestamp > System.currentTimeMillis() + 86400000L) { // Allow 1 day in future for clock skew
            errors.add("Timestamp cannot be more than 1 day in the future");
        }

        return errors;
    }

    public List<String> validateISIN(String isin) {
        List<String> errors = new ArrayList<>();

        if (isin == null || isin.trim().isEmpty()) {
            errors.add("ISIN cannot be null or empty");
            return errors;
        }

        if (!isinGenerator.validateISINFormat(isin)) {
            errors.add("ISIN format is invalid: " + isin);
        }

        if (!isinGenerator.validateCheckDigit(isin)) {
            errors.add("ISIN check digit is invalid: " + isin);
        }

        return errors;
    }

    public List<String> validateBidPrice(double bidPrice) {
        List<String> errors = new ArrayList<>();

        if (bidPrice < MIN_PRICE) {
            errors.add("Bid price must be at least " + MIN_PRICE + " (got: " + bidPrice + ")");
        } else if (bidPrice > MAX_PRICE) {
            errors.add("Bid price must be at most " + MAX_PRICE + " (got: " + bidPrice + ")");
        }

        if (!hasMaxTwoDecimalPlaces(bidPrice)) {
            errors.add("Bid price must have at most 2 decimal places (got: " + bidPrice + ")");
        }

        return errors;
    }

    public List<String> validateBidSize(int bidSize) {
        List<String> errors = new ArrayList<>();

        if (bidSize < MIN_BID_SIZE) {
            errors.add("Bid size must be at least " + MIN_BID_SIZE + " (got: " + bidSize + ")");
        } else if (bidSize > MAX_BID_SIZE) {
            errors.add("Bid size must be at most " + MAX_BID_SIZE + " (got: " + bidSize + ")");
        }

        if (bidSize <= 0) {
            errors.add("Bid size must be positive (got: " + bidSize + ")");
        }

        return errors;
    }

    public List<String> validateAskPrice(double askPrice) {
        List<String> errors = new ArrayList<>();

        if (askPrice < MIN_PRICE) {
            errors.add("Ask price must be at least " + MIN_PRICE + " (got: " + askPrice + ")");
        } else if (askPrice > MAX_PRICE) {
            errors.add("Ask price must be at most " + MAX_PRICE + " (got: " + askPrice + ")");
        }

        if (!hasMaxTwoDecimalPlaces(askPrice)) {
            errors.add("Ask price must have at most 2 decimal places (got: " + askPrice + ")");
        }

        return errors;
    }

    public List<String> validateAskSize(int askSize) {
        List<String> errors = new ArrayList<>();

        if (askSize < MIN_ASK_SIZE) {
            errors.add("Ask size must be at least " + MIN_ASK_SIZE + " (got: " + askSize + ")");
        } else if (askSize > MAX_ASK_SIZE) {
            errors.add("Ask size must be at most " + MAX_ASK_SIZE + " (got: " + askSize + ")");
        }

        if (askSize <= 0) {
            errors.add("Ask size must be positive (got: " + askSize + ")");
        }

        return errors;
    }

    private boolean hasMaxTwoDecimalPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue() == value;
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, new ArrayList<>());
        }

        public static ValidationResult invalid(String error) {
            List<String> errors = new ArrayList<>();
            errors.add(error);
            return new ValidationResult(false, errors);
        }

        public static ValidationResult invalid(List<String> errors) {
            return new ValidationResult(false, errors);
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }

        @Override
        public String toString() {
            if (valid) {
                return "ValidationResult{valid=true}";
            } else {
                return "ValidationResult{valid=false, errors=" + errors + "}";
            }
        }
    }
}
