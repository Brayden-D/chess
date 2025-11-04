package dataaccess;

import chess.ChessGame;
import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLGameDAO;
import dataaccess.classes.SQLUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {

    //
    // UserDAO tests
    //
    @Test
    public void deleteUsersTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void createUserDataTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
        userDAO.create(new UserData("user", "pass", "user@mail.com"));
    }

    @Test
    public void createBadUserDataTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
        Assertions.assertThrows(Exception.class,
                () -> {userDAO.create(new UserData("user", null, "user@mail.com"));
        });
    }

    @Test
    public void findUserTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
        UserData user = new UserData("user", "pass", "user@mail.com");
        userDAO.create(user);
        Assertions.assertEquals(user.username(), userDAO.findUser("user").username());
        Assertions.assertEquals(user.email(), userDAO.findUser("user").email());
    }

    @Test
    public void findMissingUserTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        userDAO.clear();
        Assertions.assertNull(userDAO.findUser("user"));
    }

    //
    // AuthDAO tests
    //
    @Test
    public void clearAuthTest() {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAuthTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        authDAO.createAuthData("user");
    }

    @Test
    public void createAuthTestBadInput() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        Assertions.assertThrows(RuntimeException.class, () -> {
            authDAO.createAuthData("fakeUser");
        });
    }

    @Test
    public void deleteAuthTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        AuthData auth = authDAO.createAuthData("user");
        Assertions.assertDoesNotThrow(() -> {
            authDAO.deleteAuth(auth.authToken());
        });
    }

    @Test
    public void deleteAuthBadRequestTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        AuthData auth = authDAO.createAuthData("user");
        Assertions.assertThrows(RuntimeException.class, () -> {
            authDAO.deleteAuth(auth.authToken() + "1");
        });
    }

    @Test
    public void getUsernameTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        AuthData auth = authDAO.createAuthData("user");
        Assertions.assertThrows(RuntimeException.class, () -> {
            authDAO.deleteAuth(auth.authToken() + "1");
        });
    }

    @Test
    public void getBadUsernameTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        AuthData auth = authDAO.createAuthData("user");
        Assertions.assertThrows(RuntimeException.class, () -> {
            authDAO.deleteAuth(auth.authToken() + "1");
        });
    }

    @Test
    public void authTokenExistsTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        AuthData auth = authDAO.createAuthData("user");
        try {
            Assertions.assertTrue(authDAO.authTokenExists(auth.authToken()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void authTokenDoesntExistTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        userDAO.create(new UserData("user", "password", "email@example.com"));
        AuthData auth = authDAO.createAuthData("user");
        try {
            Assertions.assertFalse(authDAO.authTokenExists(auth.authToken() + "1"));
        } catch (Exception e) {
            fail();
        }
    }

    //
    // GameDAO tests
    //
    @Test
    public void clearGamesTest() {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGameTest() {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
        gameDAO.createGame("test");
    }

    @Test
    public void createGameBadInputTest() {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
        Assertions.assertThrows(RuntimeException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    public void findGamesTest() {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
        gameDAO.createGame("test");
        gameDAO.createGame("test2");
        gameDAO.createGame("test3");
        ArrayList<GameData> games = gameDAO.findGames();
        Assertions.assertNotNull(games);
        Assertions.assertFalse(games.isEmpty());
        Assertions.assertEquals(3, games.size());
    }

    @Test
    public void findGamesBadTest() {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
        Assertions.assertThrows(RuntimeException.class, () -> {
            gameDAO.createGame(null);
        });
        ArrayList<GameData> games = gameDAO.findGames();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void setPlayerTest() {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
        GameData game = gameDAO.createGame("test");
        gameDAO.setPlayer(game.gameID(), ChessGame.TeamColor.WHITE, "testUser");
        gameDAO.setPlayer(game.gameID(), ChessGame.TeamColor.BLACK, "testUser2");
    }

    @Test
    public void setPlayerBadInputTest() {
        SQLGameDAO gameDAO = new SQLGameDAO();
        gameDAO.clear();
        GameData game = gameDAO.createGame("test");
        gameDAO.setPlayer(game.gameID(), ChessGame.TeamColor.WHITE, "testUser");
        gameDAO.setPlayer(game.gameID(), ChessGame.TeamColor.BLACK, "testUser2");
        Assertions.assertThrows(RuntimeException.class, () -> {
            gameDAO.setPlayer(game.gameID(), ChessGame.TeamColor.WHITE, "testUser3");
        });
        Assertions.assertThrows(RuntimeException.class, () -> {
            gameDAO.setPlayer(game.gameID() + 100, ChessGame.TeamColor.WHITE, "testUser3");
        });
    }


}


