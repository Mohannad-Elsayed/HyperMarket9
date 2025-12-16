package models;

import interfaces.Savable;
import util.Config;
import util.IdManager;

public class OrderItem implements Savable {
    private final int id;
    private final int productId;
    private final double productPrice;
    private final int quantity;

    public OrderItem(int id, int productId, double productPrice, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public OrderItem(Product product, int quantity) {
        this(IdManager.nextId(), product.getId(), product.getPrice(), quantity);
    }

    public OrderItem(Product product) {
        this(IdManager.nextId(), product.getId(), product.getPrice(), 1);
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return productPrice * this.quantity;
    }

    public int getId() {
        return id;
    }

    public static Savable toObject(String[] data) {
        return new OrderItem(
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]),
                Double.parseDouble(data[2]),
                Integer.parseInt(data[3])
        );
    }
    public static Savable toObject(String line) {
        String[] data = line.split(Config.CSV_DELIMITER);
        return toObject(data);
    }

    @Override
    public String toFile() {
        return id +            Config.CSV_DELIMITER +
                productId +    Config.CSV_DELIMITER +
                productPrice + Config.CSV_DELIMITER +
                quantity;
    }

}
