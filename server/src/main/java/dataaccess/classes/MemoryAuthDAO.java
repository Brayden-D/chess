package dataaccess.classes;

import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private static final String FILE_PATH = "tempDatabase/loggedIn.txt";

    /*
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
    */

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

    public void deleteAuth(String authToken) throws Exception {
        Path path = Paths.get(FILE_PATH);
        List<String> lines = Files.readAllLines(path);

        boolean removed = lines.removeIf(line -> {
            String[] parts = line.split(",");
            return parts.length == 2 && parts[1].equals(authToken);
        });

        if (!removed) {
            throw new Exception("Error: unauthorized");
        }

        Files.write(path, lines);
    }

    public String getUsername(String authToken) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[1].equals(authToken)) {
                    return parts[0];
                }
            }
        } catch (IOException e) {

        }
        return null;
    }

    public boolean authTokenExists(String authToken) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[1].equals(authToken)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
