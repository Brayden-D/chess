package ui;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class Printer {

    public void printGame(GameData data, ChessGame.TeamColor color, ChessPosition highlightPiece) throws Exception {
        ChessBoard board = data.game().getBoard();
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);
        String output = "";

        // get valid moves if a piece is highlighted
        var validMoves = highlightPiece != null ? data.game().validMoves(highlightPiece) : null;

        // headers
        String files = isWhite ? "a  b  c  d  e  f  g  h" : "h  g  f  e  d  c  b  a";
        output += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    " + files + "    " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";

        for (int displayRow = 0; displayRow < 8; displayRow++) {
            int printedRank = isWhite ? 8 - displayRow : displayRow + 1;
            output += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + printedRank + " " + RESET_BG_COLOR + RESET_TEXT_COLOR;

            for (int displayCol = 0; displayCol < 8; displayCol++) {
                int file = isWhite ? displayCol + 1 : 8 - displayCol;
                int rank = isWhite ? 8 - displayRow : displayRow + 1;
                boolean lightSquare = ((rank - 1) + (file - 1)) % 2 == 0;
                ChessPosition pos = new ChessPosition(rank, file);

                if (validMoves != null && validMoves.contains(new ChessMove(highlightPiece, pos, null))) {
                    output += SET_BG_COLOR_YELLOW;
                } else {
                    output += lightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                }

                output += printPiece(board.getPiece(pos));
                output += RESET_BG_COLOR;
            }

            output += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + printedRank + " " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
        }

        output += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    " + files + "    " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n\n";
        System.out.println(output);
    }



    String printPiece(ChessPiece piece) {
        String output = "";
        if (piece == null) {
            return "   ";
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            output = output + (SET_TEXT_COLOR_BLUE);
        } else {
            output = output + (SET_TEXT_COLOR_RED);
        }

        String symbol = switch (piece.getPieceType()) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case ROOK -> " R ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case PAWN -> " P ";
        };

        output = output + (symbol);
        output = output + (RESET_TEXT_COLOR);
        return output;
    }

    class WSMessage {
        String type;
        String message;
        GameData game;
    }

    public GameData handleWSMessage(String json, ChessGame.TeamColor userColor) {
        Gson gson = new Gson();

        ServerMessage base = gson.fromJson(json, ServerMessage.class);

        switch (base.getServerMessageType()) {

            case LOAD_GAME -> {
                LoadGameMessage msg = gson.fromJson(json, LoadGameMessage.class);
                GameData game = msg.getGame();

                try {
                    printGame(game, userColor, null);
                } catch (Exception e) {
                    System.out.println("Unable to print board");
                }
                return game;
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
        return null;
    }

}
