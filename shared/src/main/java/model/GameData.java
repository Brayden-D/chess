package model;

import chess.ChessGame;

public class GameData {

    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame chessGame;

    GameData(int gameID, String whiteUsername, String blackUsername, String gameName,  ChessGame chessGame) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.chessGame = chessGame;
    }

    int getGameID() {
        return gameID;
    }

    void setGameID(int gameID) {
        this.gameID = gameID;
    }

    String getWhiteUsername() {
        return whiteUsername;
    }

    void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    String getBlackUsername() {
        return blackUsername;
    }

    void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    String getGameName() {
        return gameName;
    }

    void setGameName(String gameName) {
        this.gameName = gameName;
    }

    ChessGame getChessGame() {
        return chessGame;
    }

    void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

}
