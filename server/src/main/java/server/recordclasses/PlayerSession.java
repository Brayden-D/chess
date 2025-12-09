package server.recordclasses;

import io.javalin.websocket.WsContext;

public record PlayerSession(WsContext ctx, String color) {
    public void send(String message) {
        ctx.send(message);
    }
}

