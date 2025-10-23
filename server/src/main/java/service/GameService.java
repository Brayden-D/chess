package service;


import dataaccess.classes.MemoryAuthDAO;
import dataaccess.classes.MemoryGameDAO;
import model.GameData;
import server.recordClasses.JoinData;

import java.util.ArrayList;

public class GameService {
    public GameData createGame(String gameName, String authToken) {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        if (authDAO.authTokenExists(authToken)) {
            return gameDAO.createGame(gameName);
        }
        throw  new IllegalArgumentException("Error: unauthorized");
    }

    public ArrayList<GameData> listGames(String authToken) {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        if (authDAO.authTokenExists(authToken)) {
            return gameDAO.findGames();
        }
        throw  new IllegalArgumentException("Error: unauthorized");
    }

    public void joinGame(JoinData data, String authToken) {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        if (authDAO.authTokenExists(authToken)) {
            gameDAO.setPlayer(data.gameID(), data.playerColor(), authDAO.getUsername(authToken));
            return;
        }
        throw  new IllegalArgumentException("Error: unauthorized");
    }


}
