package controllers;

import interfaces.Savable;
import interfaces.Searchable;
import models.Employee;
import repositories.EmployeeRepository;

import java.util.ArrayList;

class EmployeeManager {
    static final EmployeeRepository repo = new EmployeeRepository();

    public EmployeeManager() {}

    public Employee verify(String userName, String password) {
        for (Savable obj : repo.listAll()) if (obj instanceof Employee e) {
            if (e.getUserName().equals(userName)) {
                if (e.getPassword().equals(password)) {
                    return e;
                } else {
                    throw new SecurityException("Incorrect Password.");
                }
            }
        }
        throw new SecurityException("User not found.");
    }

    public void add(Employee u) {
        for (Savable obj : repo.listAll()) if (obj instanceof Employee e) {
            if (e.getUserName().equals(u.getUserName())) {
                throw new IllegalArgumentException("User with the same username already exist.");
            }
        }
        u.setUserName(u.getUserName().trim().toLowerCase());
        if (u.getUserName().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty.");
        if (u.getPassword().length() < 4)
            throw new IllegalArgumentException("Password must be 4 or more characters.");
        // no need to check id uniqueness, it's guaranteed to be unique using IdManager
        repo.add(u);
    }

    public void remove(int id) {
        repo.remove(id);
    }

    public void remove(Employee e) {
        repo.remove(e);
    }

    public void update(int id, Employee after) {
        Employee before = (Employee) repo.searchById(id);
        if (before == null)
            throw new IllegalArgumentException(String.format("Employee with Id: %d doesn't exist.", id));

        for (Savable obj : repo.listAll()) if (obj instanceof Employee e) {
            if (e.getUserName().equals(after.getUserName()) && e.getId() != id)
                throw new IllegalArgumentException("Username already taken by another employee.");
        }

        repo.update(before, after);
    }

    public ArrayList<Employee> listAll() {
        ArrayList<Employee> ret = new ArrayList<Employee>();
        for (Savable obj : repo.listAll()) if (obj instanceof Employee e) {
            ret.add(e);
        }
        return ret;
    }

    public Employee searchById(int id) {
        return (Employee) repo.searchById(id);
    }

    public ArrayList<Employee> searchByUserName(String userName) {
        ArrayList<Savable> data = repo.searchByName(userName);
        ArrayList<Employee> ret = new ArrayList<Employee>();
        for (Savable s : data)
            ret.add((Employee) s);
        return ret;
    }

    public void flush() {
        repo.save();
    }
}
