package dataaccess;

import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    public void deleteAuthTestBadRequest() {
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

}


