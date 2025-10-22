package service;

import dataaccess.classes.MemoryAuthDAO;
import dataaccess.classes.MemoryUserDAO;
import model.*;
import server.recordClasses.DeleteResult;

public class DeleteService {
    public DeleteResult deleteAll() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        userDAO.clear();
        authDAO.clear();
        return new DeleteResult(true);
    }
}
