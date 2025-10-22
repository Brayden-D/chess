package service;

import dataaccess.classes.MemoryUserDAO;
import model.*;
import server.recordClasses.DeleteResult;

public class DeleteService {
    public DeleteResult deleteAll() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.clear();
        return new DeleteResult(true);
    }
}
