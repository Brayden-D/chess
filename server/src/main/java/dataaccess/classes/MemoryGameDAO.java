package dataaccess.classes;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class MemoryGameDAO implements GameDAO {

    private static final String FILE_PATH = "tempDatabase/games.json";
    private final Gson gson = new Gson();

    // ---------- Helper Methods ----------

    private List<GameData> loadGames() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "[]");
            }

            String json = Files.readString(path);
            Type listType = new TypeToken<ArrayList<GameData>>() {}.getType();
            List<GameData> games = gson.fromJson(json, listType);
            return (games != null) ? games : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error reading games file", e);
        }
    }

    private void saveGames(List<GameData> games) {
        try {
            String json = gson.toJson(games);
            Files.writeString(Paths.get(FILE_PATH), json);
        } catch (IOException e) {
            throw new RuntimeException("Error writing games file", e);
        }
    }

    private int getNextGameID(List<GameData> games) {
        return games.stream().mapToInt(GameData::gameID).max().orElse(0) + 1;
    }

    // ---------- Interface Methods ----------

    public GameData createGame(String gameName) {
        List<GameData> games = loadGames();

        // Prevent duplicate game names
        for (GameData g : games) {
            if (g.gameName().equals(gameName)) {
                throw new RuntimeException("Game name already exists");
            }
        }

        int newID = getNextGameID(games);
        GameData newGame = new GameData(newID, null, null, gameName, new ChessGame());
        games.add(newGame);
        saveGames(games);

        return newGame;
    }

    public ArrayList<GameData> findGames() {
        return new ArrayList<>(loadGames());
    }

    public GameData setPlayer(int gameID, ChessGame.TeamColor color, String username) {
        List<GameData> games = loadGames();

        for (int i = 0; i < games.size(); i++) {
            GameData g = games.get(i);
            if (g.gameID() == gameID) {
                GameData updated;
                if (color == ChessGame.TeamColor.WHITE && g.whiteUsername() == null) {
                    updated = new GameData(g.gameID(), username, g.blackUsername(), g.gameName(), g.game());
                } else if (color == ChessGame.TeamColor.BLACK && g.blackUsername() == null) {
                    updated = new GameData(g.gameID(), g.whiteUsername(), username, g.gameName(), g.game());
                } else {
                    throw new RuntimeException("Error: already taken");
                }

                games.set(i, updated);
                saveGames(games);
                return updated;
            }
        }

        throw new RuntimeException("Error: Game ID not found");
    }
    public void clear() {
        try {
            Path path = Path.of(FILE_PATH);
            Files.deleteIfExists(path); // delete old file
            Files.createFile(path);     // recreate it empty
        } catch (IOException ignored) {

        }
    }
}
