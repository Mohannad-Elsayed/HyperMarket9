package controllers;

import interfaces.Savable;
import models.Order;
import models.OrderItem;
import models.Product;
import repositories.OrderRepository;

import java.util.ArrayList;

class OrderManager {
    public static final OrderRepository repo = new OrderRepository();
    OrderManager() {}

    public void add(Order order) {
        repo.add(order);
    }

    public void remove(Order order) {
        repo.remove(order);
    }

    public void remove(int id) {
        repo.remove(id);
    }

    // TODO: make system singleton
    public Order createOrder(ArrayList<OrderItem> cart) throws IllegalArgumentException {
        // validate the whole cart first
        for (OrderItem item : cart) {
            // yes this is not the best way to call the system manager, but fixing it would require a complete system redesign
            Product p = (Product) SystemManager.getInstance().productManager.searchById(item.getProductId());
            if (p == null)
                throw new IllegalArgumentException("Item not found.");
            if (p.getStock() < item.getQuantity())
                throw new IllegalArgumentException(String.format("Product %s has low stock %d.", p.getName(), p.getStock()));
        }
        Order order = new Order();
        for (OrderItem item : cart) {
            SystemManager.getInstance().productManager.sell(item.getProductId(), item.getQuantity());
            order.addOrderItem(item);
        }
        this.add(order);
        return order;
    }

    public void cancelOrder(int id) throws IllegalArgumentException {
        Order order = (Order) repo.searchById(id);
        if (order == null)
            throw new IllegalArgumentException(String.format("Order with Id: %d can't be found!", id));

        for (OrderItem item : order.getOrderItems()) {
            SystemManager.getInstance().productManager.cancelItem(item);
        }
        this.remove(order);
    }

    public ArrayList<Order> listAll() {
        ArrayList<Order> ret = new ArrayList<Order>();
        for (Savable obj : repo.listAll()) if (obj instanceof Order e) {
            ret.add(e);
        }
        return ret;
    }

    public Order searchById(int id) {
        return (Order) repo.searchById(id);
    }

    public ArrayList<Order> searchByName(String name) {
        return null;
    }

    public void flush() {
        repo.save();
    }
}
