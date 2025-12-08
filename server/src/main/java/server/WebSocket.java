package server;

import chess.*;
import dataaccess.classes.SQLGameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import server.recordclasses.PlayerSession;

import java.lang.module.InvalidModuleDescriptorException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Integer.parseInt;

public class WebSocket {

    private final  Map<Integer, Map<String, PlayerSession>> games = new ConcurrentHashMap<>();


    public void onConnect(WsConnectContext ctx) {
        String auth = ctx.queryParam("auth");
        Integer gameID = parseInt(ctx.queryParam("game"));
        String color = ctx.queryParam("color");

        games.putIfAbsent(gameID, new ConcurrentHashMap<>());
        games.get(gameID).put(auth, new PlayerSession(ctx, color));

        ctx.send("CONNECTED to game " + gameID + " as " + color);
    }

    public void onMessage(WsMessageContext ctx) {
        String auth = ctx.queryParam("auth");
        Integer gameID = parseInt(ctx.queryParam("game"));
        String color = ctx.queryParam("color");
        String msg = ctx.message();

        Map<String, PlayerSession> gamePlayers = games.getOrDefault(gameID, Map.of());

        if (msg.split(" ")[0].equals("move")) {
            SQLGameDAO gameDAO = new SQLGameDAO();
            GameData game = gameDAO.getGame(gameID);

            String start = msg.split(" ")[1];
            String end = msg.split(" ")[2];
            ChessPosition startPos = new ChessPosition(start.charAt(0) - 'a' + 1, start.charAt(1));
            ChessPosition endPos = new ChessPosition(end.charAt(0) - 'a', end.charAt(1));
            ChessPiece.PieceType promotionPiece = null;
            try {
                promotionPiece = switch (msg.split(" ")[3]) {
                    case "n", "knight" -> ChessPiece.PieceType.KNIGHT;
                    case "b", "bishop" -> ChessPiece.PieceType.BISHOP;
                    case "r", "rook" -> ChessPiece.PieceType.ROOK;
                    case "q", "queen" -> ChessPiece.PieceType.QUEEN;
                    default -> promotionPiece;
                };
            } catch (Exception ignored) {}
            ChessMove move = new ChessMove(startPos, endPos, promotionPiece)

            try {
                if (color.equals(game.game().getTeamTurn().name())
                        && game.game().validMoves(startPos).contains(move)) {
                    game.game().makeMove(move);
                } else {
                    throw new InvalidMoveException();
                }
            } catch (InvalidMoveException e) {
                //TODO send "invalid move" to person who tried
            }

            //TODO update database with new board


        }

        /*
        System.out.println("Broadcasting message from " + color + " in game " + gameID + ": " + msg);
        gamePlayers.values().forEach(playerSession -> {
            playerSession.ctx().send(msg);
        });
        */

    }

    public void onClose(WsCloseContext ctx) {

    }


}
