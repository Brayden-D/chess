package facade;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

class WSListener implements WebSocket.Listener {

    private final Consumer<String> onMessageCallback;

    WSListener(Consumer<String> callback) {
        this.onMessageCallback = callback;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("Connected to game");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        onMessageCallback.accept(data.toString());
        webSocket.request(1);
        return null;
    }
}

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private WebSocket webSocket;
    String serverURL;
    //only public for testing purposes
    public String authToken;

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
    public void joinWebSocket(int gameID, String color, Consumer<String> callback) throws Exception {
        String url = serverURL.replaceFirst("^http", "ws") + "/ws" +
                "?auth=" + authToken +
                "&game=" + gameID +
                "&color=" + color;

        webSocket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(url), new WSListener(callback))
                .join();
    }

    public void sendWebSocketMessage(String message) {
        System.out.println("Sending message: " + message);
        if (webSocket != null) {
            webSocket.sendText(message, true);
        }
    }


}
