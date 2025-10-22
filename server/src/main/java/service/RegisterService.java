package service;


import server.recordClasses.*;

public class RegisterService {
    public RegisterResult register(RegisterData data) {
        return new RegisterResult(data.username(), "test");
    }

}
