package facade;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    String serverURL = "http://localhost:8080";

    public ServerFacade() {
    }

    // logged out functions
    public LoginResponse register(String username, String password, String email) throws Exception {
        RegisterRequest user = new RegisterRequest(username, password, email);
        var response = request("POST", "/user", user);
        return handleResponse(response, LoginResponse.class);
    }

    public LoginResponse login(String username, String password) throws Exception {
        LoginRequest user = new LoginRequest(username, password);
        var response = request("POST", "/session", user);
        return handleResponse(response, LoginResponse.class);
    }

    // logged in functions
    public void logout() {
        request("DELETE", "/session", null);
    }

    public void createGame(String gameName) {

    }

    public void listGames() {

    }

    public void playGame(String gameName) {

    }

    public void observeGame(String gameName) {

    }

    public HttpResponse<String> request(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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
