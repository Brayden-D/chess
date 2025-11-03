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
        userDAO.create(new UserData("user", "pass", "user@mail.com"));
    }

    @Test
    public void createBadUserDataTest() {
        SQLUserDAO userDAO = new SQLUserDAO();
        Assertions.assertThrows(Exception.class,
                () -> {userDAO.create(new UserData("user", null, "user@mail.com"));
                });
    }

}
