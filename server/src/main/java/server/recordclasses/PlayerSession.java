package server.recordclasses;

import io.javalin.websocket.WsContext;

public record PlayerSession(WsContext ctx, String color) {
}

