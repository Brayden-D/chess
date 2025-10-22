package server;

import io.javalin.*;
import io.javalin.http.Context;

record RegisterData (String username, String password,  String email) {}

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);

    }

    private void register (Context ctx) {
        RegisterData data =  new RegisterData(ctx.pathParam("username"), ctx.pathParam("password"), ctx.pathParam("email"));

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
