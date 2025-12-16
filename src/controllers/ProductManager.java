package controllers;

import interfaces.Savable;
import models.OrderItem;
import models.Product;
import repositories.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

class ProductManager {
    public static final ProductRepository repo = new ProductRepository();
    ProductManager() {}

    public void add(Product product) {
        for (Savable obj : repo.listAll()) if (obj instanceof Product p) {
                if (product.getId() == p.getId())
                    throw new IllegalArgumentException("Duplicate product found. Please update product instead.");
            }
        if (product.getStock() < 1)
            throw new IllegalArgumentException("Product stock must be greater than 1");
        if (product.getPrice() < 0.0)
            throw new IllegalArgumentException("Price must be greater than 0.");
        if (product.getDeal() < 0.0 || product.getDeal() > 100.0)
            throw new IllegalArgumentException("Deal value must be between 0% and 100%");
        if (!product.getRecommendedQuantityRange().valid())
            throw new IllegalArgumentException("Invalid quantity range.");
        if (product.getProductionDate().isAfter(product.getExpiryDate()))
            throw new IllegalArgumentException("Production date must predate expiry date");

        repo.add(product);
    }

    public void remove(int id) {
        repo.remove(id);
    }

    public void remove(Product product) {
        repo.remove(product);
    }

    public void update(int id, Product after) {
        repo.update(id, after);
    }

    public void setDeal(int id, double dealPercentage) {
        Product before = (Product) repo.searchById(id);
        if (before == null)
            throw new IllegalArgumentException(String.format("Product with Id: %d doesn't exist.", id));
        if (dealPercentage < 0.0 || dealPercentage > 100)
            throw new IllegalArgumentException(String.format("Incorrect deal percentage %f. Provide a value between [0, 100].", dealPercentage));

        before.setDeal(dealPercentage);
        repo.save(); // must call save here 3ashan changed the repo internally but without saving
    }

    public ArrayList<Product> getExpired() {
        ArrayList<Product> ret = new ArrayList<Product>();

        for (Savable obj : repo.listAll()) if (obj instanceof Product p) {
            if (p.getExpiryDate().minusDays(7).isBefore(LocalDateTime.now()))
                ret.add(p);
        }

        return ret;
    }

    public ArrayList<Product> getMalStock() {
        ArrayList<Product> ret = new ArrayList<Product>();

        for (Savable obj : repo.listAll()) if (obj instanceof Product p) {
            if (!p.getRecommendedQuantityRange().IsInside(p.getStock()))
                ret.add(p);
        }

        return ret;
    }

    public void cancelItem(OrderItem item) {
        Product product = (Product) repo.searchById(item.getProductId());
        int currentTotal = product.getTotalStockCount();
        product.setTotalStockCount(currentTotal + item.getQuantity());
        repo.save();
    }

    public void removeDamagedStock(int id) {
        Product p = (Product) repo.searchById(id);
        if (p == null)
            throw new IllegalArgumentException(String.format("Product with Id: %d doesn't exist.", id));
        p.resolveDamaged();
        repo.save();
    }

    public void resolveReturnedStock(int id) {
        Product p = (Product) repo.searchById(id);
        if (p == null)
            throw new IllegalArgumentException(String.format("Product with Id: %d doesn't exist.", id));
        p.resolveReturned();
        repo.save();
    }

    public void sell(int id, int quantity) {
        Product p = (Product) repo.searchById(id);
        if (p == null)
            throw new IllegalArgumentException(String.format("Product with Id: %d doesn't exist.", id));
        if (!p.sellProduct(quantity))
            throw new IllegalArgumentException("Not enough stock or expired.");

        repo.save();
    }

    public ArrayList<Product> listAll() {
        ArrayList<Product> ret = new ArrayList<Product>();
        for (Savable obj : repo.listAll()) if (obj instanceof Product e) {
            ret.add(e);
        }
        return ret;
    }

    public Product searchById(int id) {
        return (Product) repo.searchById(id);
    }

    public ArrayList<Product> searchByName(String name) {
        ArrayList<Savable> data = repo.searchByName(name);
        ArrayList<Product> ret = new ArrayList<Product>();
        for (Savable s : data)
            ret.add((Product) s);
        return ret;
    }

    public void flush() {
        repo.save();
    }
}