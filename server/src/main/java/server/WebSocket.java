package server;

import dataaccess.classes.SQLGameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import server.recordclasses.PlayerSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Integer.parseInt;

public class WebSocket {

    private final  Map<Integer, Map<String, PlayerSession>> games = new ConcurrentHashMap<>();


    public void onConnect(WsConnectContext ctx) {
        String auth = ctx.queryParam("auth");
        Integer gameID = parseInt(ctx.queryParam("game"));
        String color = ctx.queryParam("color");

        games.putIfAbsent(gameID, new ConcurrentHashMap<>());
        games.get(gameID).put(auth, new PlayerSession(ctx, color));

        ctx.send("CONNECTED to game " + gameID + " as " + color);
    }

    public void onMessage(WsMessageContext ctx) {
        String auth = ctx.queryParam("auth");
        Integer gameID = parseInt(ctx.queryParam("game"));
        String color = ctx.queryParam("color");
        String msg = ctx.message();

        Map<String, PlayerSession> gamePlayers = games.getOrDefault(gameID, Map.of());

        if (msg.split(" ")[0].equals("move")) {
            SQLGameDAO gameDAO = new SQLGameDAO();


            String start = msg.split(" ")[1];
            String end = msg.split(" ")[2];
            start = "" + (start.charAt(0) - 'a' + 1) + start.charAt(1);
            end = "" + (end.charAt(0) - 'a' + 1) + end.charAt(1);


        }

        System.out.println("Broadcasting message from " + color + " in game " + gameID + ": " + msg);
        gamePlayers.values().forEach(playerSession -> {
            playerSession.ctx().send(msg);
        });

    }

    public void onClose(WsCloseContext ctx) {

    }


}
