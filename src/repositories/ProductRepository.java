package repositories;

import interfaces.Savable;
import models.Product;
import util.Config;

import java.util.ArrayList;

public class ProductRepository extends BaseRepository {
    public ProductRepository() {
        super(Config.PRODUCTS_FILE);
    }

    @Override
    protected Savable mapLineToSavable(String line) {
        return (Savable) Product.toObject(line);
    }

    @Override
    public Savable searchByUserName(String userName) { return null; }
    
    @Override
    public ArrayList<Savable> searchByName(String name) {
        ArrayList<Savable> ret = new ArrayList<Savable>();
        for (Savable s : items) {
            Product p = (Product) s;
            if (p.getName().equals(name)) {
                ret.add(p);
            }
        }
        return ret;
    }
}
