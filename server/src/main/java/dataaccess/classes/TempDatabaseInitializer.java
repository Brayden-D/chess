package dataaccess.classes;

import java.io.IOException;
import java.nio.file.*;

public class TempDatabaseInitializer {

    private static final String BASE_DIR = "tempDatabase";
    private static final String USERS_FILE = BASE_DIR + "/users.txt";
    private static final String GAMES_FILE = BASE_DIR + "/games.json";
    private static final String LOGGED_IN_FILE = BASE_DIR + "/loggedIn.txt";

    public static void initialize() {
        try {
            Path dirPath = Paths.get(BASE_DIR);
            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("Created directory: " + dirPath);
            }

            createFileIfMissing(USERS_FILE);
            createFileIfMissing(GAMES_FILE);
            createFileIfMissing(LOGGED_IN_FILE);

        } catch (IOException e) {
            System.err.println("Failed to initialize temp database files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createFileIfMissing(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            Files.createFile(path);
            System.out.println("Created file: " + filePath);

            // Initialize games.json as empty JSON array
            if (filePath.endsWith("games.json")) {
                Files.writeString(path, "[]");
            }
        }
    }
}
