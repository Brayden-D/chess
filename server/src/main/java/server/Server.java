package server;

import chess.ChessGame;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import server.recordclasses.*;
import service.DeleteService;
import service.GameService;
import service.UserService;
import model.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Server {

    private final Javalin server;
    WebSocket webSocket = new WebSocket();

    public Server() {
        server = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        // Delete endpoint
        server.delete("/db", this::delete);

        // Register endpoint
        server.post("/user", this::register);

        // Login endpoint
        server.post("/session", this::login);

        // Logout endpoint
        server.delete("/session", this::logout);

        // CreateGame endpoint
        server.post("/game", this::createGame);

        // JoinGame endpoint
        server.put("/game", this::joinGame);

        // ListGames endpoint
        server.get("/game", this::listGames);

        // WebSocket endpoint
        server.ws("/ws", ws -> {
            ws.onConnect(webSocket::onConnect);
            ws.onMessage(webSocket::onMessage);
            ws.onClose(webSocket::onClose);
        });

        server.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        });
    }


    private void delete(Context ctx) {
        DeleteService deleteService = new DeleteService();
        DeleteResult deleteResult = deleteService.deleteAll();

        ctx.status(200);
    }

    private boolean isAuthError(Exception e) {
        return Objects.equals(e.getMessage(), "Error: unauthorized");
    }

    private void register(Context ctx) {

        try {
            UserData data = ctx.bodyAsClass(UserData.class);
            if (data.username() == null || data.password() == null || data.email() == null) {
                ctx.status(400);
                ctx.json(Map.of("message", "Error: Username or Password or Email is null"));
                return;
            }
            UserService registerService = new UserService();
            RegisterResult result = registerService.register(data);

            ctx.json(result).status(200);
        } catch (Exception e) {
            if (Objects.equals(e.getMessage(), "Error: username already taken")) {
                ctx.status(403);
                ctx.json(Map.of("message", "Error: username already taken"));
            } else {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: Internal server error"));
            }
        }
    }

    private void login(Context ctx) {
        try{
            UserData data = ctx.bodyAsClass(UserData.class);
            UserService loginService = new UserService();
            if (data.username() == null || data.password() == null) {
                ctx.status(400);
                ctx.json(Map.of("message", "Error: Username or Password is null"));
                return;
            }
            AuthData loginResult = loginService.login(data.username(), data.password());
            ctx.json(loginResult).status(200);

        } catch (Exception e) {
            if (isAuthError(e)) {
                ctx.status(401);
                ctx.json(Map.of("message", "Error: unauthorized"));
            } else if (Objects.equals(e.getMessage(), "Error: Bad Request") && ctx.status().getCode() != 500) {
                ctx.status(401);
                ctx.json(Map.of("message", "Error: Bad Request"));
            } else {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: Internal server error"));
            }
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            UserService logoutService = new UserService();
            DeleteResult loginResult = logoutService.logout(authToken);
            ctx.json(loginResult).status(200);
        } catch (Exception e) {
            if (isAuthError(e)) {
                ctx.status(401);
                ctx.json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: Internal server error"));
            }
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            GameData gameData = ctx.bodyAsClass(GameData.class);

            if (gameData.gameName() == null) {
                ctx.status(400);
                ctx.json(Map.of("message", "Error: Bad Request"));
                return;
            }

            GameService gameService = new GameService();
            GameData newGame = gameService.createGame(gameData.gameName(), authToken);

            ctx.json(Map.of("gameID", newGame.gameID())).status(200);

        } catch (Exception e) {
            if (isAuthError(e)) {
                ctx.status(401);
                ctx.json(Map.of("message", "Error: unauthorized"));
                return;
            }
            ctx.status(500);
            ctx.json(Map.of("message", "Error: Internal server error"));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinData joinData = ctx.bodyAsClass(JoinData.class);

            if (joinData.playerColor() != ChessGame.TeamColor.WHITE && joinData.playerColor() != ChessGame.TeamColor.BLACK) {
                ctx.status(400);
                ctx.json(Map.of("message", "Error: Bad Request"));
                return;
            }

            GameService gameService = new GameService();
            gameService.joinGame(joinData, authToken);
            ctx.status(200);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (Objects.equals(e.getMessage(), "Error: Game ID not found")) {
                ctx.status(400);
                ctx.json(Map.of("message", e.getMessage()));
            } else if (Objects.equals(e.getMessage(), "Error: unauthorized")) {
                ctx.status(401);
                ctx.json(Map.of("message", e.getMessage()));
            } else if (Objects.equals(e.getMessage(), "Error: already taken")) {
                ctx.status(403);
                ctx.json(Map.of("message", e.getMessage()));
            } else {
                ctx.status(500);
                ctx.json(Map.of("message", e.getMessage()));
            }
        }
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            GameService gameService = new GameService();
            ArrayList<GameData> gameList = gameService.listGames(authToken);

            ctx.json(Map.of("games", gameList)).status(200);

        } catch (Exception e) {
            if (Objects.equals(e.getMessage(), "Error: unauthorized")) {
                ctx.status(401);
                ctx.json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: Internal server error"));
            }
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
