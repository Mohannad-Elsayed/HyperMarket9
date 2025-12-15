package interfaces;

public interface Editable extends Searchable {
    public abstract void add(Savable object);
    public abstract void remove(Savable object);
    public abstract void remove(int objectId);
    public abstract void update(Savable before, Savable after);
    public abstract void update(int id, Savable after);
}
