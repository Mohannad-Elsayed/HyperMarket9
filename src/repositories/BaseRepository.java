package repositories;

import interfaces.Editable;
import interfaces.Identifiable;
import interfaces.Savable;
import util.FileManager;

import java.util.ArrayList;

public abstract class BaseRepository implements Editable {
    private final String filePath;
    private final FileManager fileManager;
    ArrayList<Savable> items;

    public BaseRepository(String filePath) {
        this.items = new ArrayList<Savable>();
        this.filePath = filePath;
        this.fileManager = new FileManager(filePath);
        this.load();
    }

    @Override
    public ArrayList<Savable> listAll() {
        return items;
    }

    @Override
    public void add(Savable object) {
        items.add(object);
        save();
    }

    @Override
    public void remove(Savable object) {
        if (object != null) {
            items.remove(object);
            save();
        }
    }

    @Override
    public void remove(int id) {
        Savable target = searchById(id);
        this.remove(target);
    }

    @Override
    public void update(Savable before, Savable after) {
        this.items.remove(before);
        this.items.add(after);
        save();
    }

    @Override
    public Savable searchById(int id) {
        for (Savable s : items) {
            if (s instanceof Identifiable e) {
                if (e.getId() == id) {
                    return s;
                }
            }
        }
        return null;
    }

    @Override
    public void update(int id, Savable after) {
        Savable target = searchById(id);
        update(target, after);
    }

    private void load() {
        try {
            ArrayList<String> lines = fileManager.readFile();
            for (String line : lines) {
                // calls the subclass to get an instance of the Savable object
                Savable obj = mapLineToSavable(line);
                if (obj != null) {
                    items.add(obj);
                }
            }
        } catch (Exception e) {
            IO.println(e);
        }
    }

    public void save() {
        ArrayList<String> lines = new ArrayList<>();
        for (Savable obj : items) {
            lines.add(obj.toFile());
        }
        try {
            fileManager.flushFile(lines);
        } catch (Exception e) {
            throw new RuntimeException("Error saving to file: " + filePath);
        }
    }

    protected abstract Savable mapLineToSavable(String line);
}
