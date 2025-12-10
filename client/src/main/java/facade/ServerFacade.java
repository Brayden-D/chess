package facade;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import ui.Printer;
import websocket.commands.UserGameCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class WSListener implements WebSocket.Listener {

    Printer printer =  new Printer();
    ChessGame.TeamColor teamColor;

    WSListener(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("Connected to game");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        System.out.println(); //if this line isn't here it breaks. Like, not the formatting, the actual program. w h a t
        CompletableFuture.runAsync(() -> {
            printer.handleWSMessage(data.toString(), teamColor);
        });
        webSocket.request(1);
        return null;
    }
}

public class ServerFacade {

    private final Map<WSKey, WebSocket> sockets = new ConcurrentHashMap<>();
    private final HttpClient client = HttpClient.newHttpClient();
    String serverURL;
    //only public for testing purposes
    public String authToken;
    int gameID;
    ChessGame.TeamColor color;
    Gson gson = new Gson();

    public void setServerURL(String url) {
        this.serverURL = url;
    }

    public void deleteALL() throws Exception {
        authToken = null;
        request("DELETE", "/db", null);
    }

    // logged out functions
    public void register(String username, String password, String email) throws Exception {
        RegisterRequest user = new RegisterRequest(username, password, email);
        var rawResponse = request("POST", "/user", user);
        authToken = handleResponse(rawResponse, LoginResponse.class).authToken();
    }

    public void login(String username, String password) throws Exception {
        LoginRequest user = new LoginRequest(username, password);
        var rawResponse = request("POST", "/session", user);
        authToken = handleResponse(rawResponse, LoginResponse.class).authToken();
    }

    // logged in functions
    public void logout() throws Exception {
        if (authToken == null) {
            throw new Exception("not logged in");
        }
        request("DELETE", "/session", null);
        authToken = null;
    }

    public void createGame(String gameName) throws Exception {
        request("POST", "/game", new CreateGameRequest(gameName));
    }

    public ArrayList<GameData> listGames() throws Exception {
        var response = request("GET", "/game", null);
        return handleResponse(response, ListGamesResponse.class).games();
    }

    public void playGame(ChessGame.TeamColor color, int gameNum) throws Exception {
        request("PUT", "/game", new JoinRequest(color, gameNum));
    }

    public HttpResponse<String> request(String method, String path, Object body) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        try {
            return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        }
        return HttpRequest.BodyPublishers.noBody();
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!(status / 100 == 2)) {
            throw new Exception("Server returned status code " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    // websocket methods
    public void joinWebSocket(int gameID, ChessGame.TeamColor color) {

        WSKey key = new WSKey(gameID, color);
        this.gameID = gameID;
        this.color = color;

        String url = serverURL.replace("http", "ws") + "/ws" +
                "?auth=" + authToken;

        WebSocket ws = client
                .newWebSocketBuilder()
                .buildAsync(URI.create(url), new WSListener(color))
                .join();

        sockets.put(key, ws);
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );
        sendWebSocketMessage(gson.toJson(cmd));
    }

    public void sendWebSocketMessage(String message) {
        WSKey key = new WSKey(gameID, color);
        WebSocket ws = sockets.get(key);

        if (ws != null) {
            ws.sendText(message, true);
        } else {
            System.out.println("No WebSocket for " + gameID + " (" + color + ")");
        }

    }

    public void move(String input) {
        String[] args = input.split(" ");

        String from = args[1];
        String to = args[2];

        ChessPosition start = new ChessPosition(
                from.charAt(1) - '0',
                from.charAt(0) - 'a' + 1
        );

        ChessMove move = getChessMove(to, args, start);

        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID
        );

        cmd.setMove(move);
        sendWebSocketMessage(gson.toJson(cmd));
    }

    public void resign() {
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        );
        sendWebSocketMessage(gson.toJson(cmd));
    }

    public void leave() {
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        sendWebSocketMessage(gson.toJson(cmd));
    }

    private static ChessMove getChessMove(String to, String[] args, ChessPosition start) {
        ChessPosition end = new ChessPosition(
                to.charAt(1) - '0',
                to.charAt(0) - 'a' + 1
        );

        ChessPiece.PieceType promotion = null;
        if (args.length > 3) {
            switch (args[3].trim().toLowerCase()) {
                case "n", "knight" -> promotion = ChessPiece.PieceType.KNIGHT;
                case "b", "bishop" -> promotion = ChessPiece.PieceType.BISHOP;
                case "r", "rook"   -> promotion = ChessPiece.PieceType.ROOK;
                case "q", "queen"  -> promotion = ChessPiece.PieceType.QUEEN;
            }
        }

        ChessMove move = new ChessMove(start, end, promotion);
        return move;
    }


}
