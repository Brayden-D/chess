package client;

import facade.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    ServerFacade facade = new ServerFacade();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);


    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void deleteTest() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
    }


    @Test
    public void registerTest() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertDoesNotThrow(() -> facade.register("a", "a", "a"));
    }

    @Test
    public void registerTestNeg() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertDoesNotThrow(() -> facade.register("a", "a", "a"));
        Assertions.assertThrows(Exception.class, () -> facade.register("a", "a", "a"));
    }

    @Test
    public void loginTest() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertDoesNotThrow(() -> facade.register("a", "a", "a"));
        Assertions.assertDoesNotThrow(() -> facade.login("a", "a"));
    }

    @Test
    public void loginTestNeg() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertDoesNotThrow(() -> facade.register("a", "a", "a"));
        Assertions.assertThrows(Exception.class, () -> facade.login("a", "b"));
    }

    @Test
    public void loginTestNeg2() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertThrows(Exception.class, () -> facade.login("a", "b"));
    }

    @Test
    public void logoutTest() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertDoesNotThrow(() -> facade.register("a", "a", "a"));
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }



}
