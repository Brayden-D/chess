package service;


import dataaccess.classes.MemoryUserDAO;
import server.recordClasses.*;
import model.UserData;

public class RegisterService {
    public RegisterResult register(UserData data) {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        return new RegisterResult(data.username(), "test");
    }



}
