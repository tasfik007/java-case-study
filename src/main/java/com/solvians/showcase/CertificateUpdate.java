package com.solvians.showcase;

import java.util.Objects;


public class CertificateUpdate {

    private long timestamp;
    private String isin;
    private double bidPrice;
    private int bidSize;
    private double askPrice;
    private int askSize;

    public CertificateUpdate() {
    }

    public CertificateUpdate(long timestamp, String isin, double bidPrice, int bidSize, double askPrice, int askSize) {
        this.timestamp = timestamp;
        this.isin = isin;
        this.bidPrice = bidPrice;
        this.bidSize = bidSize;
        this.askPrice = askPrice;
        this.askSize = askSize;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidSize() {
        return bidSize;
    }

    public void setBidSize(int bidSize) {
        this.bidSize = bidSize;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public int getAskSize() {
        return askSize;
    }

    public void setAskSize(int askSize) {
        this.askSize = askSize;
    }
    
    public String toCSVString() {
        return String.format("%d,%s,%.2f,%d,%.2f,%d",
            timestamp, isin, bidPrice, bidSize, askPrice, askSize);
    }

    public static CertificateUpdate fromCSVString(String csvString) {
        if (csvString == null || csvString.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV string cannot be null or empty");
        }

        String[] parts = csvString.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException("CSV string must have exactly 6 fields, but got: " + parts.length);
        }

        try {
            long timestamp = Long.parseLong(parts[0].trim());
            String isin = parts[1].trim();
            double bidPrice = Double.parseDouble(parts[2].trim());
            int bidSize = Integer.parseInt(parts[3].trim());
            double askPrice = Double.parseDouble(parts[4].trim());
            int askSize = Integer.parseInt(parts[5].trim());

            return new CertificateUpdate(timestamp, isin, bidPrice, bidSize, askPrice, askSize);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV string: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "CertificateUpdate{" +
            "timestamp=" + timestamp +
            ", isin='" + isin + '\'' +
            ", bidPrice=" + bidPrice +
            ", bidSize=" + bidSize +
            ", askPrice=" + askPrice +
            ", askSize=" + askSize +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateUpdate that = (CertificateUpdate) o;
        return timestamp == that.timestamp &&
            Double.compare(that.bidPrice, bidPrice) == 0 &&
            bidSize == that.bidSize &&
            Double.compare(that.askPrice, askPrice) == 0 &&
            askSize == that.askSize &&
            Objects.equals(isin, that.isin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, isin, bidPrice, bidSize, askPrice, askSize);
    }
}
