package facade;

import com.google.gson.Gson;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    String serverURL = "http://localhost:8080";
    String authToken;

    public ServerFacade() {
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
        request("DELETE", "/session", null);
        authToken = null;
    }

    public void createGame(String gameName) throws Exception {
        request("POST", "/game", gameName);
    }

    public GameData[] listGames() throws Exception {
        var response = request("POST", "/session", null);
        return handleResponse(response, GameData[].class);
    }

    public void playGame(String gameName) {

    }

    public void observeGame(String gameName) {

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
            e.printStackTrace();
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

}
