package me.julie.chatlink.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import me.julie.chatlink.server.data.JsonManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerManager {
    private final JsonManager jsonManager = new JsonManager();

    public void startServer() throws IOException {
        var app = Javalin.create().start(20202);
        jsonManager.load();
        Map<WsContext, String> connections = new HashMap<>();
        app.ws("/chat", ws -> {
            ws.onConnect(ctx -> { // ctx = unique id for this connection
                System.out.println("Connected");
            });

            ws.onMessage(ctx -> { // onMessage = u sending message to server
                if (!connections.containsKey(ctx)) { // will always run w first msg
                    connections.put(ctx, ctx.message());
                    ctx.send("Logged in as " + ctx.message());
                    return;
                }
                String username = connections.get(ctx);
                ctx.send(username + ": " + ctx.message());
            });

            ws.onClose(ctx -> {
                System.out.println("Logged out");
                connections.remove(ctx);
            });
        });
    }
}
