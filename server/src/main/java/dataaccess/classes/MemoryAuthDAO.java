package dataaccess.classes;

import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private static final String FILE_PATH = "src/main/java/tempDatabase/loggedIn.txt";


    public AuthData getAuthData(String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return new AuthData(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    public void clear() {
        try {
            Path path = Path.of(FILE_PATH);
            Files.deleteIfExists(path); // delete old file
            Files.createFile(path);     // recreate it empty
        } catch (IOException ignored) {

        }
    }

    public AuthData createAuthData(String username) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(username, token);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + token);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return authData;
    }

    public void deleteAuth(String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            lines.removeIf(line -> line.startsWith(username + ","));
            Files.write(Paths.get(FILE_PATH), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
