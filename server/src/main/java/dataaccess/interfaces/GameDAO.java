package dataaccess.interfaces;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;

public interface GameDAO {
    GameData createGame(String gameName);
    ArrayList<GameData> findGames();
    GameData setPlayer(int gameID, ChessGame.TeamColor color,  String username);
}