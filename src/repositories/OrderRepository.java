package repositories;

import interfaces.Savable;
import models.Order;
import util.Config;

import java.util.ArrayList;

public class OrderRepository extends BaseRepository {
    public OrderRepository() {
        super(Config.ORDERS_FILE);
    }

    @Override
    protected Savable mapLineToSavable(String line) {
        return (Savable) Order.toObject(line);
    }

    @Override
    public Savable searchByUserName(String userName) { return null; }

    @Override
    public ArrayList<Savable> searchByName(String name) {
        return null;
    }
}
