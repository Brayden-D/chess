package client;

import chess.ChessGame;
import facade.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
        facade.setServerURL("http://localhost:" + port);
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

    @Test
    public void logoutTestNeg() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertNull(facade.authToken);
        Assertions.assertThrows(Exception.class, () -> facade.logout());
    }

    @Test
    public void createGameTest() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertNull(facade.authToken);
        Assertions.assertDoesNotThrow(() -> facade.createGame(""));
    }

    @Test
    public void createGameTestNeg() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertNull(facade.authToken);
        Assertions.assertThrows(Exception.class, () -> {facade.createGame(null);
                                                        facade.listGames();});

    }

    @Test
    public void listGameTest() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertNull(facade.authToken);
        Assertions.assertDoesNotThrow(() -> facade.register("a", "a", "a"));
        Assertions.assertDoesNotThrow(() -> facade.createGame("test"));
        Assertions.assertDoesNotThrow(() -> facade.listGames());
    }

    @Test
    public void listGameTestNeg() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertNull(facade.authToken);
        Assertions.assertThrows(Exception.class, () -> {facade.createGame("test");
            facade.listGames();});

    }

    @Test
    public void joinGameTest() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertNull(facade.authToken);
        Assertions.assertDoesNotThrow(() -> facade.register("a", "a", "a"));
        Assertions.assertDoesNotThrow(() -> facade.createGame("test"));
        Assertions.assertDoesNotThrow(() -> facade.playGame(ChessGame.TeamColor.WHITE, 0));
    }

    @Test
    public void joinGameTestNeg() {
        Assertions.assertDoesNotThrow(() -> facade.deleteALL());
        Assertions.assertNull(facade.authToken);
        Assertions.assertDoesNotThrow(() -> facade.createGame("test"));
        Assertions.assertThrows(Exception.class, () -> facade.listGames());
        Assertions.assertDoesNotThrow(() -> facade.playGame(ChessGame.TeamColor.WHITE, 0));
    }



}
