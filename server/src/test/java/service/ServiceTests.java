package service;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.recordclasses.JoinData;
import server.recordclasses.RegisterResult;

public class ServiceTests {


    @Test
    public void deleteTest() {
        DeleteService deleteService = new DeleteService();
        Assertions.assertDoesNotThrow(() -> {});
        Assertions.assertTrue(deleteService.deleteAll().success());
    }

    //UserService tests
    @Test
    public void registerValid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        Assertions.assertNotNull(result);
        Assertions.assertEquals("user1", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void registerInvalid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        UserService userService = new UserService();

        userService.register(new UserData("user1", "password", "user1@gmail.com"));
        Assertions.assertThrows(
                Exception.class,  // The type of exception expected
                () -> userService.register(new UserData("user1", "password", "user1@gmail.com"))
        );
    }

    @Test
    public void logoutValid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        userService.logout(result.authToken());

    }

    @Test
    public void loginValid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        userService.logout(result.authToken());
        userService.login("user1", "password");

    }

    @Test
    public void loginInvalid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        userService.logout(result.authToken());
        Assertions.assertThrows(
                Exception.class,  // The type of exception expected
                () -> userService.login("user2", "password")
        );

    }

    @Test
    public void logoutInvalid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        Assertions.assertThrows(
                Exception.class,  // The type of exception expected
                () -> userService.logout(result.authToken() + "1")
        );
    }

    //GameService Tests
    @Test
    public void createGameValid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        GameService gameService = new GameService();
        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        Assertions.assertNotNull(gameService.createGame("aaa", result.authToken()));
    }

    @Test
    public void createGameInvalid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        GameService gameService = new GameService();
        Assertions.assertThrows(
                Exception.class,  // The type of exception expected
                () -> gameService.createGame("aaa", "bbb")
        );
    }

    @Test
    public void listGamesValid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        GameService gameService = new GameService();
        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        Assertions.assertNotNull(gameService.listGames(result.authToken()));
    }

    @Test
    public void listGamesInvalid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        GameService gameService = new GameService();
        Assertions.assertThrows(
                Exception.class,  // The type of exception expected
                () -> gameService.listGames("bbb")
        );
    }

    @Test
    public void joinGameValid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        GameService gameService = new GameService();
        UserService userService = new UserService();

        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        GameData gameData = gameService.createGame("aaa", result.authToken());

        Assertions.assertDoesNotThrow(() ->
                gameService.joinGame(new JoinData(ChessGame.TeamColor.BLACK, gameData.gameID()), result.authToken())
        );
    }

    @Test
    public void joinGameInvalid() {
        DeleteService deleteService = new DeleteService();
        deleteService.deleteAll();

        GameService gameService = new GameService();
        UserService userService = new UserService();
        RegisterResult result = userService.register(new UserData("user1", "password", "user1@gmail.com"));
        GameData gameData = gameService.createGame("aaa", result.authToken());
        Assertions.assertThrows(
                Exception.class,  // The type of exception expected
                () -> gameService.joinGame(new JoinData(ChessGame.TeamColor.BLACK, gameData.gameID()), result.authToken() + "1")
        );
    }

}
