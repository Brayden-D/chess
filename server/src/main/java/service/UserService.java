package service;


import dataaccess.classes.MemoryAuthDAO;
import dataaccess.classes.MemoryUserDAO;
import model.AuthData;
import server.recordClasses.*;
import model.UserData;

public class UserService {
    public RegisterResult register(UserData data) {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        if (userDAO.findUser(data.username()) != null) {
            throw new RuntimeException("Error: username already taken");
        }
        userDAO.create(data);
        AuthData auth = authDAO.createAuthData(data.username());
        return new RegisterResult(auth.username(), auth.authToken());

    }

    public AuthData login(String username, String password) {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
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
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        try {
            authDAO.deleteAuth(authToken);
            return new DeleteResult(true);

        }
        catch (Exception e) {
            throw new RuntimeException("Error: unauthorized");
        }
    }



}
