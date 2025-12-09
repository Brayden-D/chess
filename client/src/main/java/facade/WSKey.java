package facade;

import chess.ChessGame;

public record WSKey(int gameID, ChessGame.TeamColor color) {}
