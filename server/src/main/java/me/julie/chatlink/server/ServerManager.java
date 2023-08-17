package me.julie.chatlink.server;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import me.julie.chatlink.server.data.ContactInfo;
import me.julie.chatlink.server.data.JsonManager;
import me.julie.chatlink.server.data.UserInfo;

import java.io.IOException;
import java.util.ArrayList;
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
                System.out.println("received: " + ctx.message());
                // start prompt, check if username exists
                if (ctx.message().startsWith("username@")) {
                    String[] info = ctx.message().split("@");
                    if (!jsonManager.getLoginInfo().getLogins().containsKey(info[1])) {
                        // sign up
                        System.out.println("signing up");
                        ctx.send("Display Name -> ");
                    } else { // log in
                        System.out.println("logging in");
                        ctx.send("Password -> ");
                    }
                }

                // put new user info into json
                if (ctx.message().startsWith("signup@")) {
                    String[] info = ctx.message().split("@");
                    jsonManager.getLoginInfo().getLogins().put(info[1],
                            new UserInfo(info[1], info[2], info[3], new ArrayList<>()));
                    jsonManager.save();
                    connections.put(ctx, info[1]);
                    ctx.send("connected@Welcome " + info[3] + "!");
                }

                // check if username and password match
                if (ctx.message().startsWith("login@")) {
                    String[] info = ctx.message().split("@");
                    UserInfo user = jsonManager.getLoginInfo().getLogins().get(info[1]);
                    if (user.getPassword().equals(info[2])) {
                        connections.put(ctx, info[1]);
                        String name = jsonManager.getLoginInfo().getLogins().get(info[1]).getDisplayName();
                        ctx.send("connected@Logged in as " + name + "!");
                    } else {
                        ctx.send("Incorrect password.");
                        ctx.send("Password -> ");
                    }
                }

                // send the display name given the username
                if (ctx.message().startsWith("getDisplayName@")) {
                    String[] info = ctx.message().split("@");
                    ctx.send("displayName@" + jsonManager.getLoginInfo().getLogins().get(info[1]).getDisplayName());
                }

                // change the value of <username, displayname> given the key
                if (ctx.message().startsWith("changeDisplayName")) {
                    String[] info = ctx.message().split("@");
                    jsonManager.getLoginInfo().getLogins().get(info[1]).setDisplayName(info[2]);
                    jsonManager.save();
                    ctx.send("greenMessage@[UPDATED]");
                    ctx.send("displaySettings");
                }

                // sends contact display names
                if (ctx.message().equals("allContactNames")) {
                    int index = 1;
                    if (!jsonManager.getLoginInfo().getLogins().get(connections.get(ctx)).getContacts().isEmpty()) {
                        for (ContactInfo contact : jsonManager.getLoginInfo().getLogins().get(connections.get(ctx)).getContacts()) {
                            ctx.send("contactName@" + index + "@" + contact.getDisplayName());
                            index++;
                        }
                    }
                    ctx.send("displayContacts@" + index);
                }

                // create new contact in json
                if (ctx.message().startsWith("newContact@")) {
                    String[] info = ctx.message().split("@");
                    if (!jsonManager.getLoginInfo().getLogins().containsKey(info[1])) {
                        ctx.send("notValidUser");
                    } else {
                        jsonManager.getLoginInfo().getLogins()
                                .get(connections.get(ctx)).getContacts().add(new ContactInfo(info[1],
                                        jsonManager.getLoginInfo().getLogins().get(info[1]).getDisplayName()));
                        jsonManager.save();
                        ctx.send("greenMessage@[CONTACT ADDED]");
                    }
                    System.out.println("contact update");
                    ctx.send("displayContactsMenu");
                }

                // get the contact info
                if (ctx.message().startsWith("getContactInfo")) {
                    String[] info = ctx.message().split(" ");
                    ContactInfo contact = jsonManager.getLoginInfo().getLogins()
                            .get(connections.get(ctx)).getContacts().get(Integer.parseInt(info[1]) - 1);
                    ctx.send("contactInfo@" + contact.getUsername() + "@" + contact.getDisplayName());
                }

                // change contact's display name
                if (ctx.message().startsWith("changeContactName@")) {
                    String[] info = ctx.message().split("@");
                    int index = jsonManager.getLoginInfo().getLogins()
                            .get(connections.get(ctx)).getContactUsernames().indexOf(info[1]);
                    jsonManager.getLoginInfo().getLogins().get(connections.get(ctx)).getContacts()
                            .get(index).setDisplayName(info[2]);
                    jsonManager.save();
                    ctx.send("greenMessage@[UPDATED]");
                    ctx.send("displayContactsMenu");
                }

                // remove contact
                if (ctx.message().startsWith("removeContact@")) {
                    String[] info = ctx.message().split("@");
                    int index = jsonManager.getLoginInfo().getLogins()
                            .get(connections.get(ctx)).getContactUsernames().indexOf(info[1]);
                    jsonManager.getLoginInfo().getLogins().get(connections.get(ctx)).getContacts()
                            .remove(index);
                    jsonManager.save();
                    ctx.send("greenMessage@[CONTACT REMOVED]");
                    ctx.send("displayContactsMenu");
                }


            });
            ws.onClose(connections::remove);
        });
    }
}
