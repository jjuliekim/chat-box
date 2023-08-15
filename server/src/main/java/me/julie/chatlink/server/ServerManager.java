package me.julie.chatlink.server;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import me.julie.chatlink.server.data.JsonManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerManager {
    private final JsonManager jsonManager = new JsonManager();

    public void startServer() throws IOException {
        var app = Javalin.create().start(20202);
        // load the files
        jsonManager.load();
        // save the files whenever you edit the info
        Map<WsContext, String> connections = new HashMap<>();
        app.ws("/chat", ws -> {
            ws.onMessage(ctx -> { // onMessage = u sending message to server
                System.out.println("received message: " + ctx.message());
                // start prompt, check if username exists
                if (ctx.message().startsWith("username ")) {
                    System.out.println("received username: " + ctx.message());
                    String[] info = ctx.message().split(" ");
                    if (!jsonManager.getLoginInfo().getLogins().containsKey(info[1])) {
                        // sign up
                        ctx.send("Display Name -> ");
                    } else { // log in
                        ctx.send("Password -> ");
                    }
                }

                // put username/pw info into json
                if (ctx.message().startsWith("signup")) {
                    String[] info = ctx.message().split(" ");
                    jsonManager.getLoginInfo().getLogins().put(info[1], info[2]);
                    jsonManager.save();
                    connections.put(ctx, info[1]);
                }

                // put username/display name info into json
                if (ctx.message().startsWith("displaynamemsg#")) {
                    String[] info = ctx.message().split("#");
                    jsonManager.getNameInfo().getDisplayNames().put(info[1], info[2]);
                    jsonManager.save();
                    ctx.send("connected@Welcome " + info[2] + "!");
                }

                // check if username and password match
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

                // send the display name given the username
                if (ctx.message().startsWith("getDisplayName ")) {
                    String[] info = ctx.message().split(" ");
                    ctx.send("displayName@" + jsonManager.getNameInfo().getDisplayNames().get(info[1]));
                }

                // change the value of <username, displayname> given the key
                if (ctx.message().startsWith("changeDisplayName")) {
                    String[] info = ctx.message().split("@");
                    jsonManager.getNameInfo().getDisplayNames().put(info[1], info[2]);
                    jsonManager.save();
                    ctx.send("updatedNotif");
                    ctx.send("displaySettings");
                }

                // sends contact display names
                if (ctx.message().equals("allContactNames")) {
                    if (!jsonManager.getContactInfo().getContacts().isEmpty()) {
                        int index = 1;
                        for (String name : jsonManager.getContactInfo().getContacts().values()) {
                            ctx.send("contactName@" + index + "@" + name);
                            index++;
                        }
                    }
                    ctx.send("displayContacts");
                }

                // create new contact in json
                if (ctx.message().startsWith("newContact@")) {
                    String[] info = ctx.message().split("@");
                    if (!jsonManager.getLoginInfo().getLogins().containsKey(info[1])) {
                        ctx.send("notValidUser");
                        ctx.send("displayContactsMenu");
                    } else {
                        String defaultName = jsonManager.getNameInfo().getDisplayNames().get(info[1]);
                        jsonManager.getContactInfo().getContacts().put(info[1], defaultName);
                        jsonManager.save();
                        ctx.send("displayContactsMenu");
                    }
                }

            });

            ws.onClose(connections::remove);
        });
    }
}
