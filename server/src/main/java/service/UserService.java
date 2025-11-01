package service;


import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLUserDAO;
import model.AuthData;
import server.recordclasses.*;
import model.UserData;

public class UserService {
    public RegisterResult register(UserData data) {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        if (userDAO.findUser(data.username()) != null) {
            throw new RuntimeException("Error: username already taken");
        }
        userDAO.create(data);
        AuthData auth = authDAO.createAuthData(data.username());
        return new RegisterResult(auth.username(), auth.authToken());

    }

    public AuthData login(String username, String password) {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        UserData user = userDAO.findUser(username);

        if (user == null) {
            throw new RuntimeException("Error: Bad Request");
        }
        if (!user.password().equals(password)) {
            throw new RuntimeException("Error: unauthorized");
        }
        return authDAO.createAuthData(username);
    }

    public DeleteResult logout(String authToken) {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        try {
            authDAO.deleteAuth(authToken);
            return new DeleteResult(true);

        }
        catch (Exception e) {
            throw new RuntimeException("Error: unauthorized");
        }
    }



}
