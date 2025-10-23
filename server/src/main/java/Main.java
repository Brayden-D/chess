import dataaccess.classes.TempDatabaseInitializer;
import server.Server;

public class Main {
    public static void main(String[] args) {

        TempDatabaseInitializer.initialize();

        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}