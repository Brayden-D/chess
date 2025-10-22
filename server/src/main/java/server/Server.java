package server;

import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import server.recordClasses.*;
import service.DeleteService;
import service.RegisterService;
import model.*;

import java.util.Objects;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        // Delete endpoint
        javalin.delete("/db", this::delete);

        // Register endpoint
        javalin.post("/user", this::register);
    }

    private void delete(Context ctx) {
        DeleteService deleteService = new DeleteService();
        DeleteResult deleteResult = deleteService.deleteAll();

        ctx.status(200);
    }

    private void register(Context ctx) {
        try {
            UserData data = ctx.bodyAsClass(UserData.class);
            RegisterService registerService = new RegisterService();
            RegisterResult result = registerService.register(data);

            ctx.json(result).status(200);
        } catch (Exception e) {
            if (Objects.equals(e.getMessage(), "Error: username already taken")) {
                ctx.status(403).result(e.getMessage());
            } else {
                ctx.status(500).result(e.getMessage());
            }
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
