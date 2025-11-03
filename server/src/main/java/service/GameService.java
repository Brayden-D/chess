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
            if (!authDAO.authTokenExists(authToken)) {
                throw new RuntimeException("Error: unauthorized");
            }

            return gameDAO.createGame(gameName);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error: internal server error", e);
        }
    }

    public ArrayList<GameData> listGames(String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();

        try {
            if (!authDAO.authTokenExists(authToken)) {
                throw new RuntimeException("Error: unauthorized");
            }

            return gameDAO.findGames();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error: internal server error", e);
        }
    }

    public void joinGame(JoinData data, String authToken) {
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();

        try {
            if (!authDAO.authTokenExists(authToken)) {
                throw new RuntimeException("Error: unauthorized");
            }

            String username = authDAO.getUsername(authToken);
            gameDAO.setPlayer(data.gameID(), data.playerColor(), username);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error: internal server error", e);
        }
    }
}
