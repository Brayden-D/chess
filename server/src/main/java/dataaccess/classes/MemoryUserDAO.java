package dataaccess.classes;

import dataaccess.interfaces.UserDAO;
import model.UserData;

import java.io.*;
import java.nio.file.*;

public class MemoryUserDAO implements UserDAO {

    private static final String BASE_PATH = "server/tempDatabase";
    private static final String FILE_PATH = BASE_PATH + "/users.txt";

    public MemoryUserDAO() {
        try {
            initializeFile();
        } catch (IOException e) {
            System.err.println("Error initializing users.txt: " + e.getMessage());
        }
    }

    private void initializeFile() throws IOException {
        // Ensure directory exists
        Path baseDir = Paths.get(BASE_PATH);
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }

        // Ensure users.txt exists
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public void create(UserData userData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(userData.username() + "," + userData.password() + "," + userData.email());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing user data to file", e);
        }
    }

    public void clear() {
        try {
            Path path = Path.of(FILE_PATH);
            Files.deleteIfExists(path); // delete old file
            Files.createFile(path);     // recreate it empty
        } catch (IOException ignored) {
        }
    }

    public UserData findUser(String username) {
        try {
            for (String line : Files.readAllLines(Paths.get(FILE_PATH))) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    return new UserData(parts[0], parts[1], parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
