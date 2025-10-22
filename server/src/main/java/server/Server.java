package server;

import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import server.recordClasses.*;
import service.DeleteService;
import service.RegisterService;
import model.*;

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
        UserData data = ctx.bodyAsClass(UserData.class);
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
