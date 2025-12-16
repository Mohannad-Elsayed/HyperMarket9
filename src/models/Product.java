package models;

import java.time.*;
import java.util.*;

import interfaces.Identifiable;
import interfaces.Savable;
import util.*;

public class Product implements Savable, Identifiable {
    private final int id;
    private int stock; // represents the real stock value currently in the system
    private int returnedCounter;
    private int damagedCounter;
    private String name;
    private String description;
    private double price;
    private double deal;
    private Range recommendedQuantityRange;
    private final LocalDateTime addedDate;
    private LocalDateTime productionDate;
    private LocalDateTime expiryDate;

    public Product(
            int id, int stock, int returnedCounter, int damagedCounter, String name,
            String description, double price, double deal, Range recommendedQuantityRange,
            LocalDateTime addedDate, LocalDateTime productionDate, LocalDateTime expiryDate) {
        this.id = id;
        this.stock = stock;
        this.returnedCounter = returnedCounter;
        this.damagedCounter = damagedCounter;
        this.name = name;
        this.description = description;
        this.price = price;
        this.deal = deal;
        this.recommendedQuantityRange = recommendedQuantityRange;
        this.addedDate = addedDate;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
    }

    public Product(int stock, String name, String description, double price,
                   Range recommendedQuantityRange,
                   LocalDateTime productionDate, LocalDateTime expiryDate) {
        this(IdManager.nextId(), stock, 0, 0, name, description, price, 0.0,
                recommendedQuantityRange, LocalDateTime.now(), productionDate, expiryDate);
    }

    public double getRealPrice() {
        return price * (1.0 - (deal / 100.0));
    }

    public int getStock() { return this.stock; }

    public boolean sellProduct(int amount) {
        if (expiryDate.isBefore(LocalDateTime.now()) || getStock() < amount || amount <= 0) {
            return false;
        }
        this.stock -= amount;
        return true;
    }

    public void returnItem(int count) {
        this.returnedCounter += count;
    }

    public void addDamagedItem(int count) {
        this.damagedCounter += count;
    }

    // getters and Setters
    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price < 0.0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }

    public double getDeal() { return deal; }
    public void setDeal(double deal) {
        if (deal < 0.0 || deal > 100.0) {
            throw new IllegalArgumentException("Deal must be between 0 and 100.");
        }
        this.deal = deal;
    }

    public Range getRecommendedQuantityRange() { return recommendedQuantityRange; }
    public void setRecommendedQuantityRange(Range recommendedQuantityRange) {
        this.recommendedQuantityRange = recommendedQuantityRange;
    }

    public LocalDateTime getAddedDate() { return addedDate; }

    public LocalDateTime getProductionDate() { return productionDate; }
    public void setProductionDate(LocalDateTime productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getReturnedCounter() { return returnedCounter; }
    public void resolveReturned(int count) {
        this.returnedCounter -= count;
        this.stock += count;
    }
    public void resolveReturned() {
        this.resolveReturned(this.returnedCounter);
    }

    public int getDamagedCounter() { return damagedCounter; }
    public void resolveDamaged() {
        this.damagedCounter = 0;
    }

    public static Savable toObject(String[] data) {
        return new Product(
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]),
                Integer.parseInt(data[2]),
                Integer.parseInt(data[3]),
                data[4],
                data[5],
                Double.parseDouble(data[6]),
                Double.parseDouble(data[7]),
                (Range) Range.toObject(data[8]),
                LocalDateTime.parse(data[9]),
                LocalDateTime.parse(data[10]),
                LocalDateTime.parse(data[11])
        );
    }
    public static Savable toObject(String line) {
        String[] data = line.split(Config.CSV_DELIMITER);
        return toObject(data);
    }

    @Override
    public String toFile() {
        return this.id +                            Config.CSV_DELIMITER +
        this.stock +                                Config.CSV_DELIMITER +
        this.returnedCounter +                      Config.CSV_DELIMITER +
        this.damagedCounter +                       Config.CSV_DELIMITER +
        this.name +                                 Config.CSV_DELIMITER +
        this.description +                          Config.CSV_DELIMITER +
        this.price +                                Config.CSV_DELIMITER +
        this.deal +                                 Config.CSV_DELIMITER +
        this.recommendedQuantityRange.toFile() +    Config.CSV_DELIMITER +
        this.addedDate +                            Config.CSV_DELIMITER +
        this.productionDate +                       Config.CSV_DELIMITER +
        this.expiryDate;
    }
}