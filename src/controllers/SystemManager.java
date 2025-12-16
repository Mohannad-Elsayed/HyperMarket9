package controllers;

import models.*;
import util.IdManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SystemManager {
    EmployeeManager employeeManager = new EmployeeManager();
    ProductManager productManager = new ProductManager();
    OrderManager orderManager = new OrderManager();

    private Employee currentUser;
    private static final SystemManager instance = new SystemManager();

    private SystemManager() {}
    public static SystemManager getInstance() { IdManager.setLast(); return instance; }

    public void login(String userName, String password) {
        currentUser = null;
        currentUser = employeeManager.verify(userName, password);
        // if the previous line gone well, this is safe
    }

    public void logout() {
        flushAll();
        currentUser = null;
    }

    public Employee getCurrentUser() { return currentUser; }

    // update only name, email, phone: all employees can do it
    public void updateMyInfo(String name, String email, String phone) {
        isLoggedIn();
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        employeeManager.update(currentUser.getId(), currentUser);
    }

    // ====== Admin functionalities ======
    /* update username and password */
    public void updateMyInfo(String username, String password, String name, String email, String phone) {
        checkPermission(EmployeeRole.ADMIN);
        updateMyInfo(name, email, phone);
        currentUser.setUserName(username);
        currentUser.setPassword(password);
        employeeManager.update(currentUser.getId(), currentUser);
    }

    public void addEmployee(Employee e) {
        checkPermission(EmployeeRole.ADMIN);
        employeeManager.add(e);
    }

    public void removeEmployee(int id) {
        checkPermission(EmployeeRole.ADMIN);

        if (currentUser.getId() == id)
            throw new IllegalArgumentException("You cannot delete your own account.");

        employeeManager.remove(id);
    }

    public void updateEmployee(int id, Employee after) {
        checkPermission(EmployeeRole.ADMIN);
        if (id != after.getId())
            throw new IllegalArgumentException("Cannot change employee Id.");
        employeeManager.update(id, after);
    }

    public ArrayList<Employee> listAllEmployees() {
        checkPermission(EmployeeRole.ADMIN);
        return employeeManager.listAll();
    }

    public Employee searchEmployeeById(int id) {
        checkPermission(EmployeeRole.ADMIN);
        // multiple layers to separate the work
        return employeeManager.searchById(id);
    }

    public Employee searchEmployeeByUsername(String userName) {
        checkPermission(EmployeeRole.ADMIN);
        return employeeManager.searchByUserName(userName);
    }

    // ====== Marketer functionalities ======
    public void setProductDeal(int id, double deal) {
        checkPermission(EmployeeRole.MARKETER);
        productManager.setDeal(id, deal);
    }

    // ====== Inventory methods ======
    public void addProduct(Product p) {
        checkPermission(EmployeeRole.INVENTORY);
        productManager.add(p);
    }

    public void removeProduct(int id) {
        checkPermission(EmployeeRole.INVENTORY);
        productManager.remove(id);
    }

    public void updateProduct(int id, Product p) {
        checkPermission(EmployeeRole.INVENTORY);
        productManager.update(id, p);
    }

    public void resolveDamagedStock(int id) {
        checkPermission(EmployeeRole.INVENTORY);
        productManager.removeDamagedStock(id);
    }

    public void resolveReturnedStock(int id) {
        checkPermission(EmployeeRole.INVENTORY);
        productManager.resolveReturnedStock(id);
    }

    // returns a list of alerts (in a form of strings) for the inventory manager
    public ArrayList<String> getInventoryNotifications() {
        checkPermission(EmployeeRole.INVENTORY);
        ArrayList<String> alerts = new ArrayList<>();

        for (Product p : productManager.getExpired()) {
            alerts.add(String.format("[%s] ", p.getExpiryDate().isBefore(LocalDateTime.now()) ? "EXPIRED" : "NEAR EXPIRED") + p.getName() + " expires on " + p.getExpiryDate().toLocalDate());
        }

        for (Product p : productManager.getMalStock()) {
            String level =
                    p.getStock() < p.getRecommendedQuantityRange().getMin()
                            ? "LOW"
                            : "HIGH";

            alerts.add(String.format(
                    "[%s STOCK] %s: %d units present. Recommended range: %s",
                    level,
                    p.getName(),
                    p.getStock(),
                    p.getRecommendedQuantityRange()
            ));
        }

        return alerts;
    }

    public boolean productInActiveOrder(int id) {
        return orderManager.productInActiveOrder(id);
    }

    // ====== Sales functionalities ======
    public Order createOrder(ArrayList<OrderItem> cart) {
        checkPermission(EmployeeRole.SALES);
        return orderManager.createOrder(cart);
    }

    public void returnOrder(int id) {
        checkPermission(EmployeeRole.SALES);
        orderManager.returnOrder(id);
    }

    public ArrayList<Order> listOrders() {
        checkPermission(EmployeeRole.SALES);
        return orderManager.listAll();
    }

    public Order searchOrderById(int id) {
        checkPermission(EmployeeRole.SALES);
        return orderManager.searchById(id);
    }

    // Shared functionalities
    public ArrayList<Product> listProducts() {
        checkPermission(EmployeeRole.INVENTORY, EmployeeRole.MARKETER, EmployeeRole.SALES);
        return productManager.listAll();
    }

    public Product searchProductById(int id) {
        checkPermission(EmployeeRole.INVENTORY, EmployeeRole.MARKETER, EmployeeRole.SALES);
        return productManager.searchById(id);
    }

    public ArrayList<Product> searchProductByName(String name) {
        checkPermission(EmployeeRole.INVENTORY, EmployeeRole.MARKETER, EmployeeRole.SALES);
        return productManager.searchByName(name);
    }

    public void flushAll() {
        isLoggedIn();
        employeeManager.flush();
        productManager.flush();
        orderManager.flush();
        IdManager.flush();
    }

    // ====== Helper methods ======
    private void isLoggedIn() {
        if (currentUser == null)
            throw new SecurityException("Not logged in.");
    }

    private void checkPermission(EmployeeRole... allowedRoles) {
        isLoggedIn();

        EmployeeRole currentRole = currentUser.getRole();

        // uncomment to allow admin to do everything in the system
        // if (currentRole == EmployeeRole.ADMIN) return;

        for (EmployeeRole role : allowedRoles)
            if (currentRole == role) return;

        throw new SecurityException("Access Denied: You do not have permission.");
    }
}
