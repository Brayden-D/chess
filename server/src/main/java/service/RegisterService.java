package service;


import dataaccess.classes.MemoryAuthDAO;
import dataaccess.classes.MemoryUserDAO;
import model.AuthData;
import server.recordClasses.*;
import model.UserData;

public class RegisterService {
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



}
