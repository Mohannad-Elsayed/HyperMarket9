package repositories;

import interfaces.Savable;
import models.Employee;
import util.Config;
import java.util.ArrayList;

public class EmployeeRepository extends BaseRepository {
    public EmployeeRepository() {
        super(Config.USERS_FILE);
    }

    @Override
    protected Savable mapLineToSavable(String line) {
        return (Savable) Employee.toObject(line);
    }

    @Override
    public ArrayList<Savable> searchByName(String name) { return null; }

    @Override
    public Employee searchByUserName(String userName) {
        for (Savable s : items) {
            Employee e = (Employee) s;
            if (e.getUserName().equals(userName)) {
                return e;
            }
        }
        return null;
    }
}
