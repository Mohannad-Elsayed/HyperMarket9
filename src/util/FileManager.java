package util;

import java.io.*;
import java.util.ArrayList;

public class FileManager {
    private final String path;

    public FileManager(String path) {
        this.path = path;
    }

    public ArrayList<String> readFile() throws IOException {
        return FileManager.readFile(path);
    }

    public void writeLine(String data, boolean append) throws IOException {
        FileManager.writeLine(path, data, append);
    }

    public void flushFile(ArrayList<String> data) throws IOException {
        FileManager.flushFile(path, data);
    }

    public static ArrayList<String> readFile(String filePath) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + filePath);
        }
        return lines;
    }

    public static void writeLine(String filePath, String data, boolean append) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, append))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + filePath);
        }
    }

    public static void flushFile(String filePath, ArrayList<String> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : data) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + filePath);
        }
    }
}