import dataaccess.classes.TempDatabaseInitializer;
import server.Server;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        String basePath = "server\\tempDatabase";
        String[] files = {"users.txt", "games.json", "loggedIn.txt"};

        File dir = new File(basePath);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Created directory: " + dir.getAbsolutePath());
            }
        }

        for (String fileName : files) {
            File file = new File(dir, fileName);
            try {
                if (file.createNewFile()) {
                    System.out.println("Created file: " + file.getAbsolutePath());
                } else {
                    System.out.println("File already exists: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Error creating file " + fileName + ": " + e.getMessage());
            }
        }

        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
