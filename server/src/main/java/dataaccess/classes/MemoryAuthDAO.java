package dataaccess.classes;

import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private static final String BASE_PATH = "server/tempDatabase";
    private static final String USERS_FILE = BASE_PATH + "/users.txt";
    private static final String LOGGEDIN_FILE = BASE_PATH + "/loggedIn.txt";
    private static final String GAMES_FILE = BASE_PATH + "/games.json";

    public MemoryAuthDAO() {
        try {
            initializeFiles();
        } catch (IOException e) {
            System.err.println("Error initializing database files: " + e.getMessage());
        }
    }

    private void initializeFiles() throws IOException {
        // Ensure directory exists
        Path baseDir = Paths.get(BASE_PATH);
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }

        // Ensure each file exists
        ensureFileExists(USERS_FILE);
        ensureFileExists(LOGGEDIN_FILE);

        // For games.json, ensure it starts with an empty list if not present
        Path gamesPath = Paths.get(GAMES_FILE);
        if (!Files.exists(gamesPath)) {
            Files.writeString(gamesPath, "[]", StandardOpenOption.CREATE);
        }
    }

    private void ensureFileExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
            System.out.println("Created file: " + path.toAbsolutePath());
        }
    }

    public void clear() {
        try {
            Files.writeString(Paths.get(LOGGEDIN_FILE), "");
        } catch (IOException ignored) {
        }
    }

    public AuthData createAuthData(String username) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(username, token);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOGGEDIN_FILE, true))) {
            writer.write(username + "," + token);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return authData;
    }

    public void deleteAuth(String authToken) throws Exception {
        Path path = Paths.get(LOGGEDIN_FILE);
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
            List<String> lines = Files.readAllLines(Paths.get(LOGGEDIN_FILE));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[1].equals(authToken)) {
                    return parts[0];
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public boolean authTokenExists(String authToken) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(LOGGEDIN_FILE));
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
