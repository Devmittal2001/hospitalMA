package stockcollection;

import java.util.*;

class Stock {
    /** Data about a particular stock. */
    String symbol;
    String name;

    Stock(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Stock stock = (Stock) other;
        return Objects.equals(symbol, stock.symbol) && Objects.equals(name, stock.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, name);
    }
}

class PriceRecord {
    /** Data and methods about a single price record of a stock. */
    Stock stock;
    int price;
    String date; // YYYY-MM-DD

    PriceRecord(Stock stock, int price, String date) {
        this.stock = stock;
        this.price = price;
        this.date = date;
    }
}

class StockCollection {
    /**
     * Data for a collection of price records for a particular stock, and methods for
     * getting useful statistics about the stock's prices.
     */
    ArrayList<PriceRecord> priceRecords = new ArrayList<>();
    Stock stock;

    StockCollection(Stock stock) {
        this.stock = stock;
    }

    int getNumPriceRecords() {
        return priceRecords.size();
    }

    void addPriceRecord(PriceRecord priceRecord) {
        if (!priceRecord.stock.equals(this.stock)) {
            throw new IllegalArgumentException(
                    "PriceRecord's Stock is not the same as the StockCollection's");
        }
        priceRecords.add(priceRecord);
    }

    int getMaxPrice() {
        if (priceRecords.isEmpty()) {
            return -1;
        }
        return priceRecords.stream().mapToInt(record -> record.price).max().getAsInt();
    }

    int getMinPrice() {
        if (priceRecords.isEmpty()) {
            return -1;
        }
        return priceRecords.stream().mapToInt(record -> record.price).min().getAsInt();
    }

    double getAvgPrice() {
        if (priceRecords.isEmpty()) {
            return -1.0;
        }
        double total = priceRecords.stream().mapToInt(record -> record.price).sum();
        return total / priceRecords.size();
    }

    Object[] getBiggestChange() {
        if (priceRecords.size() < 2) {
            return null;
        }

        ArrayList<PriceRecord> sortedRecords = new ArrayList<>(priceRecords);
        Collections.sort(sortedRecords, Comparator.comparing(record -> record.date));

        PriceRecord first = sortedRecords.get(0);
        PriceRecord second = sortedRecords.get(1);

        int biggestChange = second.price - first.price;
        String earlierDate = first.date;
        String laterDate = second.date;

        for (int i = 1; i < sortedRecords.size() - 1; i++) {
            PriceRecord current = sortedRecords.get(i);
            PriceRecord next = sortedRecords.get(i + 1);

            int change = next.price - current.price;
            if (Math.abs(change) > Math.abs(biggestChange)) {
                biggestChange = change;
                earlierDate = current.date;
                laterDate = next.date;
            }
        }

        return new Object[] { biggestChange, earlierDate, laterDate };
    }
}

public class Main {

    public static void main(String[] args) {
        testPriceRecord();
        testStockCollection();
        testGetBiggestChange();
        System.out.println("All tests passed.");
    }

    public static void testPriceRecord() {
        System.out.println("Running testPriceRecord");
        Stock testStock = new Stock("AAPL", "Apple Inc.");
        PriceRecord testPriceRecord = new PriceRecord(testStock, 100, "2023-07-01");

        assertEquals(testStock, testPriceRecord.stock);
        assertEquals(100, testPriceRecord.price);
        assertEquals("2023-07-01", testPriceRecord.date);
    }

    private static StockCollection makeStockCollection(Stock stock, Object[][] priceData) {
        StockCollection stockCollection = new StockCollection(stock);
        for (Object[] priceRecordData : priceData) {
            PriceRecord priceRecord =
                    new PriceRecord(stock, (int) priceRecordData[0], (String) priceRecordData[1]);
            stockCollection.addPriceRecord(priceRecord);
        }
        return stockCollection;
    }

    public static void testStockCollection() {
        System.out.println("Running testStockCollection");

        Stock testStock = new Stock("AAPL", "Apple Inc.");
        StockCollection stockCollection = new StockCollection(testStock);

        assertEquals(0, stockCollection.getNumPriceRecords());
        assertEquals(-1, stockCollection.getMaxPrice());
        assertEquals(-1, stockCollection.getMinPrice());
        assertEquals(1.0, Math.abs(-1.0 - stockCollection.getAvgPrice()) < 0.001 ? 1.0 : 0.0);

        Object[][] priceData = {
                {110, "2023-06-29"},
                {112, "2023-07-01"},
                {90, "2023-06-28"},
                {105, "2023-07-06"}
        };

        stockCollection = makeStockCollection(testStock, priceData);

        assertEquals(priceData.length, stockCollection.getNumPriceRecords());
        assertEquals(112, stockCollection.getMaxPrice());
        assertEquals(90, stockCollection.getMinPrice());
        assertEquals(1.0, Math.abs(104.25 - stockCollection.getAvgPrice()) < 0.1 ? 1.0 : 0.0);
    }

    public static void testGetBiggestChange() {
        System.out.println("Running testGetBiggestChange");

        Stock testStock = new Stock("AAPL", "Apple Inc.");
        StockCollection stockCollection = new StockCollection(testStock);

        assertEquals(null, stockCollection.getBiggestChange());

        Object[][] priceData = {
                {110, "2023-06-29"},
                {112, "2023-07-01"},
                {90, "2023-06-25"},
                {105, "2023-07-06"}
        };

        stockCollection = makeStockCollection(testStock, priceData);
        assertArrayEquals(
                new Object[] {20, "2023-06-25", "2023-06-29"},
                stockCollection.getBiggestChange());

        Object[][] priceData2 = {
                {200, "2000-01-04"},
                {210, "1999-12-30"},
                {190, "2000-01-03"},
                {180, "2000-01-01"}
        };

        stockCollection = makeStockCollection(testStock, priceData2);
        assertArrayEquals(
                new Object[] {-30, "1999-12-30", "2000-01-01"},
                stockCollection.getBiggestChange());
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected: " + expected + ", but got: " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", but got: " + actual);
        }
    }

    private static void assertArrayEquals(Object[] expected, Object[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                    "Expected: " + Arrays.toString(expected) + ", but got: " + Arrays.toString(actual));
        }
    }
}

