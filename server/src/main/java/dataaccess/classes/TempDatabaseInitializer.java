package dataaccess.classes;

import java.io.IOException;
import java.nio.file.*;

public class TempDatabaseInitializer {

    public static void initialize() {
        try {
            // Use absolute path within the server folder
            Path baseDir = Paths.get("tempDatabase").toAbsolutePath();

            if (Files.notExists(baseDir)) {
                Files.createDirectories(baseDir);
                System.out.println("✅ Created directory: " + baseDir);
            } else {
                System.out.println("📁 Directory already exists: " + baseDir);
            }

            createFileIfMissing(baseDir.resolve("users.txt"));
            createFileIfMissing(baseDir.resolve("games.json"));
            createFileIfMissing(baseDir.resolve("loggedIn.txt"));

        } catch (IOException e) {
            System.err.println("❌ Failed to initialize temp database files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createFileIfMissing(Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
            System.out.println("✅ Created file: " + filePath);
            if (filePath.toString().endsWith("games.json")) {
                Files.writeString(filePath, "[]");
            }
        } else {
            System.out.println("📄 File already exists: " + filePath);
        }
    }
}
