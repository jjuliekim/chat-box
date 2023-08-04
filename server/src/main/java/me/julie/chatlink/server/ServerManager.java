package me.julie.chatlink.server;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import me.julie.chatlink.server.data.JsonManager;

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
            ws.onMessage(ctx -> { // onMessage = u sending message to server
                if (ctx.message().startsWith("username ")) {
                    String[] info = ctx.message().split(" ");
                    if (!jsonManager.getLoginInfo().getLogins().containsKey(info[1])) {
                        // sign up
                        ctx.send("Display Name -> ");
                    } else { // log in
                        ctx.send("Password: ");
                    }
                }

                if (ctx.message().startsWith("signup")) {
                    String[] info = ctx.message().split(" ");
                    jsonManager.getLoginInfo().getLogins().put(info[1], info[2]);
                    jsonManager.save();
                    connections.put(ctx, info[1]);
                }

                if (ctx.message().startsWith("displayname ")) {
                    String[] info = ctx.message().split(" ");
                    jsonManager.getNameInfo().getDisplayNames().put(info[1], info[2]);
                    jsonManager.save();
                    ctx.send("connected@Welcome " + info[2] + "!");
                }

                if (ctx.message().startsWith("login ")) {
                    String[] info = ctx.message().split(" ");
                    if (jsonManager.getLoginInfo().getLogins().get(info[1]).equals(info[2])) {
                        connections.put(ctx, info[1]);
                        String name = jsonManager.getNameInfo().getDisplayNames().get(info[1]);
                        ctx.send("connected@Logged in as " + name + "!");
                    } else {
                        ctx.send("Incorrect password.");
                        ctx.send("Password -> ");
                    }
                }

                if (ctx.message().startsWith("getDisplayName ")) {
                    String[] info = ctx.message().split(" ");
                    ctx.send("displayName@" + jsonManager.getNameInfo().getDisplayNames().get(info[1]));
                }

                if (ctx.message().startsWith("changeDisplayName")) {
                    String[] info = ctx.message().split(" ");
                    // jsonManager.getNameInfo().getDisplayNames().change...
                }

            });

            ws.onClose(connections::remove);
        });
    }
}
