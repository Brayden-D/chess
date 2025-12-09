package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLGameDAO;
import dataaccess.interfaces.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import server.recordclasses.PlayerSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import static java.lang.Integer.parseInt;

public class WebSocket {

    private final Map<Integer, Map<String, PlayerSession>> games = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    void onConnect(WsConnectContext wsConnectContext) {}

    public void onMessage(WsMessageContext ctx) {
        String msg = ctx.message();

        UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);
        String auth = command.getAuthToken();
        Integer gameID = command.getGameID();
        UserGameCommand.CommandType type = command.getCommandType();

        games.putIfAbsent(gameID, new ConcurrentHashMap<>());
        Map<String, PlayerSession> gamePlayers = games.get(gameID);

        try {
            switch (type) {
                case CONNECT -> handleConnect(ctx, command);
                case MAKE_MOVE -> {}
                case LEAVE -> {}
                case RESIGN -> {}
            }
        } catch (Exception e) {
            ctx.send(gson.toJson(Map.of(
                    "type", "ERROR",
                    "message", e.getMessage()
            )));
        }
    }

    void onClose(WsCloseContext wsCloseContext) {}

    private void handleConnect(WsMessageContext ctx, UserGameCommand cmd) {
        String auth = cmd.getAuthToken();
        Integer gameID = cmd.getGameID();

        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();
        String username = authDAO.getUsername(auth);
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame.TeamColor color;
        if (gameData.whiteUsername().equals(username)) {
            color = ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername().equals(username)) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            color = ChessGame.TeamColor.OBSERVER;
        }

        games.putIfAbsent(gameID, new ConcurrentHashMap<>());
        games.get(gameID).put(auth, new PlayerSession(ctx, color.name()));
        Gson gson = new Gson();

        NotificationMessage notification = new NotificationMessage(
                username + " joined the game as " + color.name().toLowerCase()
        );
        games.get(gameID).forEach((otherAuth, session) -> {
            if (!otherAuth.equals(auth)) { // skip the new user
                session.ctx().send(gson.toJson(notification));
            }
        });

        SQLGameDAO dao = new SQLGameDAO();
        GameData game = dao.getGame(gameID);
        LoadGameMessage loadMsg = new LoadGameMessage(game);
        ctx.send(gson.toJson(loadMsg));
    }

    /*
    private void handleMove(String auth, Integer gameID, WsMessageContext ctx, UserGameCommand command) throws InvalidMoveException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameData game = gameDAO.getGame(gameID);

        String start = command.start;
        String end = command.end;
        ChessPosition startPos = new ChessPosition(start.charAt(1) - '0', start.charAt(0) - 'a' + 1);
        ChessPosition endPos = new ChessPosition(end.charAt(1) - '0', end.charAt(0) - 'a' + 1);

        ChessPiece.PieceType promotionPiece = null;
        try {
            if (command.promotion != null) {
                promotionPiece = switch (command.promotion.toLowerCase()) {
                    case "n", "knight" -> ChessPiece.PieceType.KNIGHT;
                    case "b", "bishop" -> ChessPiece.PieceType.BISHOP;
                    case "r", "rook" -> ChessPiece.PieceType.ROOK;
                    case "q", "queen" -> ChessPiece.PieceType.QUEEN;
                    default -> null;
                };
            }
        } catch (Exception ignored) {}

        ChessMove move = new ChessMove(startPos, endPos, promotionPiece);

        if (!game.game().validMoves(startPos).contains(move) ||
                !game.game().getTeamTurn().name().equalsIgnoreCase(games.get(gameID).get(auth).color())) {
            throw new InvalidMoveException();
        }

        game.game().makeMove(move);

        // Update the game in the DB
        GameData updated = new GameData(
                game.gameID(),
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game()
        );
        gameDAO.updateGame(updated);

        // Broadcast to all clients (players + observers)
        games.get(gameID).values().forEach(p ->
                p.ctx().send(gson.toJson(Map.of(
                        "type", "MOVE",
                        "game", updated
                )))
        );
    }

     */

}
