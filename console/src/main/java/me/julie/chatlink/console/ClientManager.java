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
        ws.sendText("username@ " + username);

        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                // when server sends client something:

                // sign up prompts
                if (message.equals("Display Name -> ")) {
                    System.out.print(message);
                    String name = scanner.nextLine();
                    System.out.print("Password -> ");
                    String password = scanner.nextLine();
                    ws.sendText("signup@" + username + "@" + password + "@" + name);
                }

                // log in prompts
                if (message.equals("Password -> ")) {
                    System.out.print(message);
                    String password = scanner.nextLine();
                    ws.sendText("login@" + username + "@" + password);
                }

                // red incorrect message
                if (message.equals("Incorrect password.")) {
                    printlnReset(RED + message);
                }

                // green connected message w name
                if (message.startsWith("connected@")) {
                    String[] info = message.split("@");
                    printlnReset(GREEN + info[1]);
                    mainMenu();
                }

                // rest of settings menu
                if (message.startsWith("displayName@")) {
                    // settings options and print w name
                    String[] info = message.split("@");
                    printReset(BOLD + "Username: ");
                    System.out.println(username);
                    printReset(BOLD + "Display Name: ");
                    System.out.println(info[1]);
                    printReset(BOLD + hex("#d43d68") + "[1] ");
                    printlnReset(BOLD + "Change display name");
                    printReset(BOLD + hex("#d43d68") + "[2] ");
                    printlnReset(BOLD + "Log out");
                    printReset(BOLD + hex("#d43d68") + "[3] ");
                    printlnReset(BOLD + "Back");
                    System.out.print("-> ");

                    // results of settings choice
                    String choice = scanner.nextLine();
                    switch (choice) {
                        case "1" -> {
                            System.out.print("New display name -> ");
                            String newName = scanner.nextLine();
                            ws.sendText("changeDisplayName@" + username + "@" + newName);
                        }
                        case "2" -> {
                            printlnReset(RED + ITALICS + "[LOGGING OUT]");
                            ws.disconnect();
                        }
                        case "3" -> mainMenu();
                    }
                }

                // green updated text
                if (message.equals("updatedNotif")) {
                    printlnReset(GREEN + ITALICS + "[UPDATED]");
                }

                // calls method to display the settings menu w options
                if (message.equals("displaySettings")) {
                    settingsMenu();
                }

                // show list of contacts
                if (message.startsWith("contactName@")) {
                    String[] info = message.split("@");
                    printReset(BOLD + hex("#ebac1a") + "[" + info[1] + "] ");
                    printlnReset(BOLD + info[2]);
                }

                // rest of contacts screen
                if (message.equals("displayContacts")) {
                    contactsScreenInput();
                }

                // username does not exist, load contacts screen again
                if (message.equals("notValidUser")) {
                    printlnReset(RED + ITALICS + "[INVALID USERNAME]");
                }

                // contact added green message
                if (message.equals("contactAdded")) {
                    printlnReset(GREEN + ITALICS + "[CONTACT ADDED]");
                }

                // display contacts menu (back, new)
                if (message.equals("displayContactsMenu")) {
                    contactsMenu();
                }

            }
        });
    }

    // displays the main menu
    private void mainMenu() {
        System.out.println();
        printlnReset(BOLD + hex("#bc94e0") + "== Main Menu ==");
        printReset(BOLD + hex("#853ec7") + "[1] ");
        printlnReset(BOLD + "Contacts");
        printReset(BOLD + hex("#853ec7") + "[2] ");
        printlnReset(BOLD + "Chats");
        printReset(BOLD + hex("#853ec7") + "[3] ");
        printlnReset(BOLD + "Settings");
        System.out.print("-> ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> contactsMenu();
            case "2" -> chatsMenu();
            case "3" -> settingsMenu();
            default -> {
                printlnReset(RED + ITALICS + "[INVALID OPTION]");
                mainMenu();
            }
        }
    }

    // the contacts screen
    private void contactsMenu() {
        // choose contact to edit name, remove, or start/continue a conversation
        System.out.println();
        printlnReset(BOLD + hex("#f5d773") + "== Contacts ==");
        printlnReset(BOLD + hex("#ebac1a") + "[BACK]");
        printlnReset(BOLD + hex("#ebac1a") + "[NEW]");
        // [name] last msg preview...
        ws.sendText("allContactNames");
    }

    // input for contacts screen
    private void contactsScreenInput() {
        System.out.print("-> ");
        String input = scanner.nextLine().toLowerCase();
        if (input.equals("back")) {
            mainMenu();
        } else if (input.equals("new")) {
            System.out.print("New contact username -> ");
            String contactName = scanner.nextLine();
            ws.sendText("newContact@" + contactName);
        }
    }

    // the chats screen
    private void chatsMenu() {
        System.out.println();
        printlnReset(BOLD + hex("#78aff5") + "== Conversations ==");
        printReset(BOLD + hex("#1e72e3") + "[BACK]");
    }

    // the settings screen
    private void settingsMenu() {
        System.out.println();
        printlnReset(BOLD + hex("#e38fa7") + "== Settings ==");
        ws.sendText("getDisplayName " + username);
    }
}
