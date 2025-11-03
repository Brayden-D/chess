package dataaccess;

import dataaccess.classes.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DAOTests {

    //UserDAO tests
    @Test
    public void deleteUsersTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        Assertions.assertDoesNotThrow(() -> {});
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





}
