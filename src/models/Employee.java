package models;

import interfaces.Identifiable;
import interfaces.Savable;
import util.Config;
import util.IdManager;
import java.time.LocalDateTime;

public class Employee extends User implements Savable, Identifiable {
    private EmployeeRole role;

    public Employee(
            int id,
            String name,
            String email,
            String phone,
            String userName,
            String password,
            String registerDate,
            String role) {
        super(id, name, email, phone, userName, password, registerDate);

        if (role.equalsIgnoreCase("ADMIN")) {
            this.role = EmployeeRole.ADMIN;
        } else if (role.equalsIgnoreCase("SALES")) {
            this.role = EmployeeRole.SALES;
        } else if (role.equalsIgnoreCase("INVENTORY")) {
            this.role = EmployeeRole.INVENTORY;
        } else if (role.equalsIgnoreCase("MARKETER")) {
            this.role = EmployeeRole.MARKETER;
        }
    }

    // a constructor without id for generating new instance with a new id
    public Employee(String name, String email, String phone, String userName, String password, String role) {
        this(IdManager.nextId(), name, email, phone, userName, password, LocalDateTime.now().toString(), role);
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public EmployeeRole getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role.toString() + " " + super.getName() + " ID: " + super.getId();
    }

    public static Savable toObject(String[] data) {
        return new Employee(
                Integer.parseInt(data[0]),
                data[1], data[2], data[3], data[4], data[5], data[6], data[7]
        );
    }
    public static Savable toObject(String line) {
        String[] data =  line.split(Config.CSV_DELIMITER);
        return toObject(data);
    }

    @Override
    public String toFile() {
        return super.getId() +                          Config.CSV_DELIMITER +
                Config.sanitize(super.getName()) +      Config.CSV_DELIMITER +
                Config.sanitize(super.getEmail()) +     Config.CSV_DELIMITER +
                Config.sanitize(super.getPhone()) +     Config.CSV_DELIMITER +
                Config.sanitize(super.getUserName()) +  Config.CSV_DELIMITER +
                Config.sanitize(super.getPassword()) +  Config.CSV_DELIMITER +
                Config.sanitize(super.getRegisterDate().toString()) + Config.CSV_DELIMITER +
                Config.sanitize(role.toString());
    }
}
