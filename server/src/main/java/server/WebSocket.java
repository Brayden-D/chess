package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.classes.SQLGameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import server.recordclasses.PlayerSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Integer.parseInt;

public class WebSocket {

    private final  Map<Integer, Map<String, PlayerSession>> games = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void onConnect(WsConnectContext ctx) {
        String auth = ctx.queryParam("auth");
        Integer gameID = parseInt(ctx.queryParam("game"));
        String color = ctx.queryParam("color");

        games.putIfAbsent(gameID, new ConcurrentHashMap<>());
        games.get(gameID).put(auth, new PlayerSession(ctx, color));
        System.out.println("Connected to server");

        ctx.send("CONNECTED to game " + gameID + " as " + color);
    }

    public void onMessage(WsMessageContext ctx) {
        System.out.println("Received message: " + ctx.message());
        String auth = ctx.queryParam("auth");
        Integer gameID = parseInt(ctx.queryParam("game"));
        String color = ctx.queryParam("color");
        String msg = ctx.message();

        Map<String, PlayerSession> gamePlayers = games.getOrDefault(gameID, Map.of());
        String[] input = msg.split(" ");

        if (input[0].equals("move")) {
            SQLGameDAO gameDAO = new SQLGameDAO();
            GameData game = gameDAO.getGame(gameID);

            String start = input[1];
            String end = input[2];
            ChessPosition startPos = new ChessPosition(start.charAt(1) - '0', start.charAt(0) - 'a' + 1);
            ChessPosition endPos = new ChessPosition(end.charAt(1) - '0', end.charAt(0) - 'a' + 1);
            ChessPiece.PieceType promotionPiece = null;
            try {
                promotionPiece = switch (input[3]) {
                    case "n", "knight" -> ChessPiece.PieceType.KNIGHT;
                    case "b", "bishop" -> ChessPiece.PieceType.BISHOP;
                    case "r", "rook" -> ChessPiece.PieceType.ROOK;
                    case "q", "queen" -> ChessPiece.PieceType.QUEEN;
                    default -> promotionPiece;
                };
            } catch (Exception ignored) {}
            ChessMove move = new ChessMove(startPos, endPos, promotionPiece);

            try {
                if (color.equalsIgnoreCase(game.game().getTeamTurn().name())
                        && game.game().validMoves(startPos).contains(move)) {
                    game.game().makeMove(move);
                } else {
                    throw new InvalidMoveException();
                }
            } catch (InvalidMoveException e) {
                ctx.send(gson.toJson(Map.of(
                        "type", "ERROR",
                        "message", "Invalid move"
                )));
                return;
            }

            GameData updated = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );
            gameDAO.updateGame(updated);
            System.out.print(updated.game().getBoard());
            gamePlayers.values().forEach(p ->
                    p.ctx().send(gson.toJson(Map.of(
                            "type", "MOVE",
                            "game", updated))));
            return;

        }

        System.out.println("Broadcasting message from " + color + " in game " + gameID + ": " + msg);
        gamePlayers.values().forEach(playerSession -> {
            playerSession.ctx().send(msg);
        });

    }

    public void onClose(WsCloseContext ctx) {

    }


}
