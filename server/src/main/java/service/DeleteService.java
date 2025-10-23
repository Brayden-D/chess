package service;

import dataaccess.classes.MemoryAuthDAO;
import dataaccess.classes.MemoryGameDAO;
import dataaccess.classes.MemoryUserDAO;
import server.recordclasses.DeleteResult;

public class DeleteService {
    public DeleteResult deleteAll() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return new DeleteResult(true);
    }
}
