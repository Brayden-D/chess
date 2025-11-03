package service;


import dataaccess.DataAccessException;
import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLUserDAO;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;
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
        if (!BCrypt.checkpw(password, user.password())) {
            throw new RuntimeException("Error: unauthorized");
        }
        return authDAO.createAuthData(username);
    }

    public DeleteResult logout(String authToken) {
        SQLAuthDAO authDAO = new SQLAuthDAO();
        try {
            if (!authDAO.authTokenExists(authToken)) {
                throw new RuntimeException("Error: unauthorized");
            }

            authDAO.deleteAuth(authToken);
            return new DeleteResult(true);

        } catch (DataAccessException e) {
            throw new RuntimeException("Error: Database connection failed", e);
        } catch (RuntimeException e) {
            if ("Error: unauthorized".equals(e.getMessage())) {
                throw e;
            }
            throw new RuntimeException("Error: internal server error", e);
        }
    }



}
