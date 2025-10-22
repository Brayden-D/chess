package dataaccess.classes;

import dataaccess.interfaces.UserDAO;
import model.AuthData;
import model.UserData;


import java.io.*;
import java.nio.file.*;

public class MemoryUserDAO implements UserDAO {

    private static final String FILE_PATH = "src/main/java/tempDatabase/users.txt";

    public void create(UserData userData) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(userData.username() + "," + userData.password() + "," + userData.email());
                writer.newLine();
            }
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
