package dataaccess.classes;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import com.google.gson.Gson;

public class SQLGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    public GameData createGame(String gameName) {
        String sql = "INSERT INTO games (game_json) VALUES (?)";
        ChessGame newGame = new ChessGame();

        GameData newGameData = new GameData(0, null, null, gameName, newGame);
        String gameJson = gson.toJson(newGameData);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, gameJson);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new GameData(id, null, null, gameName, newGame);
                } else {
                    throw new RuntimeException("Failed to retrieve generated game ID");
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error creating game", e);
        }
    }

    public ArrayList<GameData> findGames() {
        return null;
    }

    public GameData setPlayer(int gameID, ChessGame.TeamColor color, String username) {
        return null;
    }

    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM games");

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear game table", e);
        }
    }

}
