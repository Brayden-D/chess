package service;


import dataaccess.classes.MemoryAuthDAO;
import dataaccess.classes.MemoryGameDAO;
import model.GameData;

public class GameService {
    public GameData createGame(String gameName, String authToken) {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        if (authDAO.authTokenExists(authToken)) {
            return gameDAO.createGame(gameName);
        }
        throw  new IllegalArgumentException("Error: unauthorized");
    }


}
