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
        if (gameName == null) {
            throw new RuntimeException("Game name is null");
        }
        String sql = "INSERT INTO games (game_json) VALUES (?)";
        String updateID = "UPDATE games SET game_json = ? WHERE id = ?";
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
                    GameData dataWithID = new GameData(id, null, null, gameName, newGame);
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateID)) {
                        updateStmt.setString(1, gson.toJson(dataWithID));
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                    }

                    return dataWithID;
                } else {
                    throw new RuntimeException("Error: Failed to retrieve generated game ID");
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error creating game", e);
        }
    }

    public ArrayList<GameData> findGames() {
        ArrayList<GameData> games = new ArrayList<>();
        String sql = "SELECT id, game_json FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String gameJson = rs.getString("game_json");
                GameData game = gson.fromJson(gameJson, GameData.class);
                games.add(game);
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error retrieving games", e);
        }

        return games;
    }

    public GameData setPlayer(int gameID, ChessGame.TeamColor color, String username) {
        if (color == null) {
            throw new RuntimeException("Error: Invalid color");
        }

        GameData updateData;
        String getGame = "SELECT game_json FROM games WHERE id = ?";
        String setPlayer = "UPDATE games SET game_json = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getGame)) {

            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Error: Game ID not found");
                }
                updateData = gson.fromJson(rs.getString("game_json"), GameData.class);
            }

            // validate and set player
            if (color == ChessGame.TeamColor.WHITE) {
                if (updateData.whiteUsername() != null) {
                    throw new RuntimeException("Error: already taken");
                }
                updateData = new GameData(
                        updateData.gameID(),
                        username,
                        updateData.blackUsername(),
                        updateData.gameName(),
                        updateData.game()
                );
            } else if (color == ChessGame.TeamColor.BLACK) {
                if (updateData.blackUsername() != null) {
                    throw new RuntimeException("Error: already taken");
                }
                updateData = new GameData(
                        updateData.gameID(),
                        updateData.whiteUsername(),
                        username,
                        updateData.gameName(),
                        updateData.game()
                );
            } else {
                throw new RuntimeException("Error: Invalid color");
            }

            // update DB
            try (PreparedStatement updateStmt = conn.prepareStatement(setPlayer)) {
                updateStmt.setString(1, gson.toJson(updateData));
                updateStmt.setInt(2, updateData.gameID());
                updateStmt.executeUpdate();
            }

            return updateData;

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error setting player", e);
        }
    }

    public GameData getGame(int gameID) {
        String sql = "SELECT game_json FROM games WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String gameJson = rs.getString("game_json");
                    return gson.fromJson(gameJson, GameData.class);
                } else {
                    throw new RuntimeException("Error: Game ID not found");
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error retrieving game with ID: " + gameID, e);
        }
    }



    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM games");

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: Failed to clear game table", e);
        }
    }

}
