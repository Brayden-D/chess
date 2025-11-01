package service;

import dataaccess.classes.*;
import server.recordclasses.DeleteResult;

public class DeleteService {
    public DeleteResult deleteAll() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return new DeleteResult(true);
    }
}
