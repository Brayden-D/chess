package service;


import dataaccess.classes.MemoryAuthDAO;
import dataaccess.classes.MemoryGameDAO;
import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLGameDAO;
import model.GameData;
import server.recordclasses.JoinData;

import java.util.ArrayList;

public class GameService {
    public GameData createGame(String gameName, String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        if (authDAO.authTokenExists(authToken)) {
            return gameDAO.createGame(gameName);
        }
        throw  new IllegalArgumentException("Error: unauthorized");
    }

    public ArrayList<GameData> listGames(String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        if (authDAO.authTokenExists(authToken)) {
            return gameDAO.findGames();
        }
        throw  new IllegalArgumentException("Error: unauthorized");
    }

    public void joinGame(JoinData data, String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        if (authDAO.authTokenExists(authToken)) {
            gameDAO.setPlayer(data.gameID(), data.playerColor(), authDAO.getUsername(authToken));
            return;
        }
        throw  new IllegalArgumentException("Error: unauthorized");
    }


}
