package service;

import dataaccess.classes.*;
import server.recordclasses.DeleteResult;

public class DeleteService {
    public DeleteResult deleteAll() {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return new DeleteResult(true);
    }
}
