package facade;

import java.net.http.HttpClient;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public void help() {

    }

    // logged out functions
    public void quit() {

    }

    public void login(String username, String password) {

    }

    public void register(String username, String password, String email) {

    }

    // logged in functions
    public void logout() {

    }

    public void createGame(String gameName) {

    }

    public void listGames() {

    }

    public void playGame(String gameName) {

    }

    public void observeGame(String gameName) {

    }



}
