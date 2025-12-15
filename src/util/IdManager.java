package util;

import java.io.IOException;
import java.util.ArrayList;

public class IdManager {
    private static int last = -1;

    public static void setLast(int start) throws IllegalArgumentException {
        if (start < 0)
            throw new IllegalArgumentException("Id can't be negative.");

        last = start;
    }

    public static void setLast() throws IOException, IllegalArgumentException{
        if (last != -1)
            return;

        ArrayList<String> data = FileManager.readFile(Config.ID_TRACKER_FILE);
        if (data.isEmpty()) {
            setLast(0);
            return;
        }
        try {
            setLast(Integer.parseInt(data.get(0)));
        } catch (NumberFormatException e) {
            throw new IOException("Id tracker file is corrupted. Couldn't parse Id");
        }
    }

    public static void flush() throws IOException {
        FileManager.writeLine(Config.ID_TRACKER_FILE, Integer.toString(last), false);
    }

    public static int nextId() {
        return ++last;
    }
}
