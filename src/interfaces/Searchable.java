package interfaces;

import java.util.ArrayList;

public interface Searchable {
    public abstract Savable searchById(int id);
    public abstract ArrayList<Savable> searchByName(String name);
    public abstract ArrayList<Savable> listAll();
}
