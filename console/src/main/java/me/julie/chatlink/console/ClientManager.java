package me.julie.chatlink.console;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.Scanner;

import static me.julie.chatlink.console.Colors.*;


public class ClientManager {
    private WebSocket ws;
    private Scanner scanner;
    private String username;

    public void run() throws IOException {
        WebSocketFactory factory = new WebSocketFactory();
        factory.setConnectionTimeout(8000);
        ws = factory.createSocket("ws://localhost:20202/chat");
        scanner = new Scanner(System.in);
        try {
            ws.connect();
        } catch (WebSocketException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }

        // welcome screen
        printlnReset(GREEN + "Connected to Server");
        System.out.print("Username -> ");
        username = scanner.nextLine();
        ws.sendText("username " + username);

        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) { // when server sends client something
                // debug: System.out.println("received message: " + message);
                // sign up/log in prompts
                if (message.equals("Display Name -> ")) {
                    System.out.print(message);
                    String name = scanner.nextLine();
                    System.out.print("Password -> ");
                    String password = scanner.nextLine();
                    ws.sendText("signup " + username + " " + password);
                    ws.sendText("displayname#" + username + "#" + name);
                } else if (message.equals("Password -> ")) { // log in prompts
                    System.out.print(message);
                    String password = scanner.nextLine();
                    ws.sendText("login " + username + " " + password);
                }
                if (message.equals("Incorrect password.")) {
                    printlnReset(RED + message);
                }
                if (message.startsWith("connected@")) {
                    String[] info = message.split("@");
                    printlnReset(GREEN + info[1]);
                    mainMenu();
                }

                // settings menu
                if (message.startsWith("displayName@")) {
                    String[] info = message.split("@");
                    printReset(BOLD + "Username: ");
                    System.out.println(username);
                    printReset(BOLD + "Display Name: ");
                    System.out.println(info[1]);
                    printReset(BOLD + hex("#e07c10") + "[1] ");
                    printlnReset(BOLD + "Change display name");
                    printReset(BOLD + hex("#e07c10") + "[2] ");
                    printlnReset(BOLD + "Log out");
                    printReset(BOLD + hex("#e07c10") + "[3] ");
                    printlnReset(BOLD + "Back");
                    System.out.print("-> ");

                    // settings choice
                    String choice = scanner.nextLine();
                    switch (choice) {
                        case "1" -> {
                            System.out.print("New display name -> ");
                            String newName = scanner.nextLine();
                            ws.sendText("changeDisplayName@" + username + "@" + newName);
                        }
                        case "2" -> {
                            printlnReset(RED + BOLD + ITALICS + "Logging out...");
                            ws.disconnect();
                        }
                        case "3" -> mainMenu();
                    }
                }

                if (message.equals("updatedNotif")) {
                    printlnReset(GREEN + "Updated!");
                }

                if (message.equals("displaySettings")) {
                    settingsMenu();
                }

                // show list of contacts

                // show conversations & msg preview
            }
        });
    }

    private void mainMenu() {
        System.out.println();
        printlnReset(BOLD + hex("#78aff5") + "== Main Menu ==");
        printReset(BOLD + hex("#1e72e3") + "[1] ");
        printlnReset(BOLD + "Contacts");
        printReset(BOLD + hex("#1e72e3") + "[2] ");
        printlnReset(BOLD + "Chats");
        printReset(BOLD + hex("#1e72e3") + "[3] ");
        printlnReset(BOLD + "Settings");
        System.out.print("-> ");
        String choice = scanner.nextLine();

        // [1] show contacts
        if (choice.equals("1")) {
            System.out.println("contacts");
        }

        // [2] show chats
        if (choice.equals("2")) {
            System.out.println("chats");
        }

        // [3] show user settings
        if (choice.equals("3")) {
            settingsMenu();
        }
    }

    // the settings screen
    private void settingsMenu() {
        System.out.println();
        printlnReset(BOLD + hex("#f5ab5d") + "== Settings ==");
        ws.sendText("getDisplayName " + username);
        // print # of contacts too
    }
}
