package service;

import dataaccess.DataAccessException;
import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLGameDAO;
import model.GameData;
import server.recordclasses.JoinData;

import java.util.ArrayList;

public class GameService {

    public GameData createGame(String gameName, String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        try {
            if (authDAO.authTokenExists(authToken)) {
                return gameDAO.createGame(gameName);
            }
            throw new RuntimeException("Error: unauthorized");
        } catch (Exception e) {
            throw new RuntimeException("Error: Database connection failed");
        }
    }

    public ArrayList<GameData> listGames(String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        try {
            if (authDAO.authTokenExists(authToken)) {
                return gameDAO.findGames();
            }
            throw new RuntimeException("Error: unauthorized");
        } catch (Exception e) {
            throw new RuntimeException("Error: Database connection failed");
        }
    }

    public void joinGame(JoinData data, String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        try {
            if (authDAO.authTokenExists(authToken)) {
                gameDAO.setPlayer(data.gameID(), data.playerColor(), authDAO.getUsername(authToken));
                return;
            }
            throw new RuntimeException("Error: unauthorized");
        } catch (Exception e) {
            throw new RuntimeException("Error: Database connection failed");
        }
    }
}
