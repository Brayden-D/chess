package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class Printer {

    public void printGame(GameData data, ChessGame.TeamColor color) throws Exception {
        ChessBoard board = data.game().getBoard();
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);

        if (isWhite) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    a  b  c  d  e  f  g  h    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        } else {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    h  g  f  e  d  c  b  a    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        }

        for (int displayRow = 0; displayRow < 8; displayRow++) {
            int row = isWhite ? displayRow : 7 - displayRow;
            int printedRank = isWhite ? 8 - displayRow : displayRow + 1;

            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    " " + printedRank + " " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR);

            for (int displayCol = 0; displayCol < 8; displayCol++) {
                int col = isWhite ? displayCol : 7 - displayCol;
                boolean lightSquare = (row + col) % 2 == 0;
                System.out.print(lightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);
                printPiece(board.getPiece(new ChessPosition(row + 1, col + 1)));
                System.out.print(RESET_BG_COLOR);
            }

            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    " " + printedRank + " " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        }

        if (isWhite) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    a  b  c  d  e  f  g  h    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n\n");
        } else {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    h  g  f  e  d  c  b  a    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n\n");
        }
    }

    void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print("   ");
            return;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            System.out.print(SET_TEXT_COLOR_RED);
        } else {
            System.out.print(SET_TEXT_COLOR_BLUE);
        }

        String symbol = switch (piece.getPieceType()) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case ROOK -> " R ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case PAWN -> " P ";
        };

        System.out.print(symbol);
        System.out.print(RESET_TEXT_COLOR);
    }

    class WSMessage {
        String type;
        String message;
        GameData game;
    }

    public void handleWSMessage(String json, ChessGame.TeamColor userColor) {
        var gson = new Gson();
        var event = gson.fromJson(json, WSMessage.class);

        switch (event.type) {

            case "MOVE" -> {
                System.out.println("2");
                try {
                    printGame(event.game, userColor);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            case "ERROR" -> {
                System.out.println("Server error: " + event.message);
            }

            default -> {
                System.out.println("Unknown WS message: " + json);
            }
        }
    }

}
