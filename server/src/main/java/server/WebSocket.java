package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.classes.SQLAuthDAO;
import dataaccess.classes.SQLGameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import server.recordclasses.PlayerSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;


public class WebSocket {

    private final Map<Integer, Map<String, PlayerSession>> games = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    SQLGameDAO gameDAO = new SQLGameDAO();
    SQLAuthDAO authDAO = new SQLAuthDAO();

    void onConnect(WsConnectContext wsConnectContext) {}
    void onClose(WsCloseContext wsCloseContext) {}

    public void onMessage(WsMessageContext ctx) {
        System.out.println("Received message: " + ctx.message());
        try {
            String msg = ctx.message();

            UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);
            Integer gameID = command.getGameID();
            UserGameCommand.CommandType type = command.getCommandType();

            games.putIfAbsent(gameID, new ConcurrentHashMap<>());
            Map<String, PlayerSession> gamePlayers = games.get(gameID);


            switch (type) {
                case CONNECT -> handleConnect(ctx, command);
                case MAKE_MOVE -> handleMove(ctx, command);
                case LEAVE -> handleLeave(ctx, command);
                case RESIGN -> handleResign(ctx, command);
            }
        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage(
                    "Error: " + e.getMessage()
            );
            ctx.send(gson.toJson(error));
        }
    }

    private void handleConnect(WsMessageContext ctx, UserGameCommand cmd) throws Exception {
        String auth = cmd.getAuthToken();
        Integer gameID = cmd.getGameID();

        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();
        String username = authDAO.getUsername(auth);
        if (username == null) {
            throw new Exception("Username not found");
        }
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception("Bad gameID");
        }
        ChessGame.TeamColor color;
        if (username.equals(gameData.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            color = ChessGame.TeamColor.OBSERVER;
        }

        games.putIfAbsent(gameID, new ConcurrentHashMap<>());
        games.get(gameID).put(auth, new PlayerSession(ctx, color.name()));

        NotificationMessage notification = new NotificationMessage(
                username + " joined the game as " + color.name().toLowerCase()
        );
        games.get(gameID).forEach((otherAuth, session) -> {
            if (!otherAuth.equals(auth)) {
                session.ctx().send(gson.toJson(notification));
            }
        });

        SQLGameDAO dao = new SQLGameDAO();
        GameData game = dao.getGame(gameID);
        LoadGameMessage loadMsg = new LoadGameMessage(game);
        ctx.send(gson.toJson(loadMsg));
    }


    private void handleMove(WsMessageContext ctx, UserGameCommand command) throws Exception {
        System.out.println("Move message: " + ctx.message());
        GameData game = gameDAO.getGame(command.getGameID());
        if (game.game().getTeamTurn() == null) {
            throw new Exception("Game is over, cannot make a move");
        }
        int gameID = command.getGameID();
        String auth = command.getAuthToken();

        String username = authDAO.getUsername(auth);
        if (username == null ||
                (!username.equals(game.whiteUsername()) && game.game().getTeamTurn() == ChessGame.TeamColor.WHITE) ||
                (!username.equals(game.blackUsername()) && game.game().getTeamTurn() == ChessGame.TeamColor.BLACK)) {
            throw new Exception("Not authorized");
        }

        ChessMove move = command.getMove();
        game.game().makeMove(move);
        if (game.game().isInCheckmate(game.game().getTeamTurn())) {
            NotificationMessage checkmate = new NotificationMessage("Game over, checkmate!");
            games.get(gameID).forEach((otherAuth, session) -> {
                session.ctx().send(gson.toJson(checkmate));
            });
        }
        if (game.game().isInStalemate(game.game().getTeamTurn())) {
            NotificationMessage checkmate = new NotificationMessage("Game over, stalemate!");
            games.get(gameID).forEach((otherAuth, session) -> {
                session.ctx().send(gson.toJson(checkmate));
            });
        }

        GameData updated = new GameData(
                game.gameID(),
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game()
        );
        gameDAO.updateGame(updated);

        LoadGameMessage loadMsg = new LoadGameMessage(updated);
        games.get(gameID).forEach((otherAuth, session) -> {
            try {
                session.ctx().send(gson.toJson(loadMsg));
                System.out.println("Sending to " + otherAuth + ": " + loadMsg);
            } catch (Exception ex) {
                System.out.println("FAILED to send WS message to " + otherAuth);
            }
        });
        NotificationMessage notification = new NotificationMessage(
                move.toString()
        );
        games.get(gameID).forEach((otherAuth, session) -> {
            if (!otherAuth.equals(auth) && game.game().getTeamTurn() != null) {
                session.ctx().send(gson.toJson(notification));
            }
        });

    }

    public void handleResign(WsMessageContext ctx, UserGameCommand command) throws Exception {
        GameData game;
        try {
            game = gameDAO.getGame(command.getGameID());
        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage("Game not found for gameID " + command.getGameID());
            ctx.send(gson.toJson(error));
            return;
        }
        String auth = command.getAuthToken();
        int gameID = command.getGameID();

        String username = authDAO.getUsername(auth);
        if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
            throw new Exception("Not authorized");
        }
        if (game.game().getTeamTurn() == null) {
            throw new Exception("Game is over, cannot resign");
        }

        NotificationMessage resign = new NotificationMessage("Game over, " + username + " resigned!");
        game.game().setTeamTurn(null);
        games.get(gameID).forEach((otherAuth, session) -> {
            session.ctx().send(gson.toJson(resign));
        });
        GameData updated = new GameData(
                game.gameID(),
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game()
        );
        gameDAO.updateGame(updated);
    }

    public void handleLeave(WsMessageContext ctx, UserGameCommand command) throws Exception {
        String auth = command.getAuthToken();
        int gameID = command.getGameID();
        String username = authDAO.getUsername(auth);
        GameData game = gameDAO.getGame(gameID);

        if (username.equals(game.whiteUsername())) {
            gameDAO.setPlayer(gameID, ChessGame.TeamColor.WHITE, null);
        }
        if (username.equals(game.blackUsername())) {
            gameDAO.setPlayer(gameID, ChessGame.TeamColor.BLACK, null);
        }

        Map<String, PlayerSession> sessions = games.get(gameID);
        if (sessions != null) {
            sessions.remove(auth);

            NotificationMessage msg = new NotificationMessage(username + " has left the game");
            String json = new Gson().toJson(msg);

            for (PlayerSession session : sessions.values()) {
                try {
                    session.send(json);
                } catch (Exception ignored) {}
            }
            if (sessions.isEmpty()) {
                games.remove(gameID);
            }
        }

        ctx.session.close();
    }

}
