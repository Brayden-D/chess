package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class Printer {

    public void printGame(GameData data, ChessGame.TeamColor color) throws Exception {
        ChessBoard board = data.game().getBoard();
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);

        // headers
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
            int printedRank = isWhite ? 8 - displayRow : displayRow + 1;

            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    " " + printedRank + " " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR);

            for (int displayCol = 0; displayCol < 8; displayCol++) {
                int file;
                int rank;

                if (isWhite) {
                    rank = 8 - displayRow;
                    file = displayCol + 1;
                } else {
                    rank = displayRow + 1;
                    file = 8 - displayCol;
                }

                boolean lightSquare = ((rank - 1) + (file - 1)) % 2 == 0;
                System.out.print(lightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);
                printPiece(board.getPiece(new ChessPosition(rank, file)));
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
            System.out.print(SET_TEXT_COLOR_BLUE);
        } else {
            System.out.print(SET_TEXT_COLOR_RED);
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
        Gson gson = new Gson();

        ServerMessage base = gson.fromJson(json, ServerMessage.class);

        switch (base.getServerMessageType()) {

            case LOAD_GAME -> {
                LoadGameMessage msg = gson.fromJson(json, LoadGameMessage.class);
                GameData game = msg.getGame();

                try {
                    printGame(game, userColor);
                } catch (Exception e) {
                    System.out.println("Unable to print board");
                }
            }

            case ERROR -> {
                ErrorMessage msg = gson.fromJson(json, ErrorMessage.class);
                System.out.println("SERVER ERROR: " + msg.getErrorMessage());
            }

            case NOTIFICATION -> {
                NotificationMessage msg = gson.fromJson(json, NotificationMessage.class);
                System.out.println("[Notification] " + msg.getMessage());
            }

            default -> System.out.println("Unknown server message: " + json);
        }
    }

}
