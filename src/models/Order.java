package models;

import java.util.*;

import interfaces.Identifiable;
import interfaces.Savable;
import util.*;

public class Order implements Savable, Identifiable {
    private final int id;
    private ArrayList<OrderItem> orderItems;

    public Order(int id, ArrayList<OrderItem> orderItems) {
        this.id = id;
        this.orderItems = orderItems;
    }

    public Order() {
        this(IdManager.nextId(), new ArrayList<OrderItem>());
    }

    public int getId() {
        return id;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
    }

    public void addProduct(Product product, int quantity) {
        this.addOrderItem(new OrderItem(product, quantity));
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public double getTotalPrice() {
        double retPrice = 0;

        for (OrderItem orderItem : orderItems)
            retPrice += orderItem.getPrice();

        return retPrice;
    }

    public static Savable toObject(ArrayList<String> dataAL) {
        Order ret = new Order(Integer.parseInt(dataAL.get(0)), new ArrayList<>());

        dataAL.removeFirst();
        while(dataAL.size() > 3) {
            OrderItem curOrder = new OrderItem(
                    Integer.parseInt(dataAL.get(0)),
                    Integer.parseInt(dataAL.get(1)),
                    Double.parseDouble(dataAL.get(2)),
                    Integer.parseInt(dataAL.get(3))

            );
            ret.addOrderItem(curOrder);
            for (int i = 0; i < 4; i++) dataAL.removeFirst();
        }

        return ret;
    }
    public static Savable toObject(String[] data) {
        ArrayList<String> dataAL = new ArrayList<String>();
        dataAL.addAll(Arrays.asList(data));
        return toObject(dataAL);
    }
    public static Savable toObject(String line) {
        String[] data = line.split(Config.CSV_DELIMITER);
        return toObject(data);
    }
    
    @Override
    public String toFile() {
        StringBuilder ret = new StringBuilder(id + Config.CSV_DELIMITER);
        for (OrderItem orderItem : orderItems) {
            ret.append(orderItem.toFile());
            ret.append(Config.CSV_DELIMITER);
        }
        return ret.toString();
    }
}
