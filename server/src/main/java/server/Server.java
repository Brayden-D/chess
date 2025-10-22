package server;

import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import org.eclipse.jetty.security.LoginService;
import server.recordClasses.*;
import service.DeleteService;
import service.UserService;
import model.*;

import java.util.Map;
import java.util.Objects;

public class Server {

    private final Javalin server;

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
    }

    private void delete(Context ctx) {
        DeleteService deleteService = new DeleteService();
        DeleteResult deleteResult = deleteService.deleteAll();

        ctx.status(200);
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
                ctx.json(Map.of("message", "Internal server error"));
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
            if (Objects.equals(e.getMessage(), "Error: unauthorized")) {
                ctx.status(401);
                ctx.json(Map.of("message", "Error: unauthorized"));
            } else {
                ctx.status(500);
                ctx.json(Map.of("message", "Internal server error"));
            }
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {server.stop();
    }
}
