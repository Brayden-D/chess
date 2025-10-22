package server;

import io.javalin.*;
import io.javalin.http.Context;
import server.recordClasses.RegisterData;
import server.recordClasses.RegisterResult;
import service.RegisterService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Delete endpoint
        javalin.delete("/db", this::delete);

        // Register endpoint
        javalin.post("/user", this::register);
    }

    private void delete(Context ctx) {

    }

    private void register(Context ctx) {
        RegisterData data = ctx.bodyAsClass(RegisterData.class);
        RegisterService registerService = new RegisterService();
        RegisterResult result = registerService.register(data);

        ctx.json(result).status(200);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
