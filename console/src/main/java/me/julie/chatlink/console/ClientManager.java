package me.julie.chatlink.console;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
            // welcome screen
            System.out.println();
            System.out.println();
            printlnReset(GREEN + "Connected to Server");
            printReset(BOLD + "Username -> ");
            username = scanner.nextLine();
            ws.sendText("username@" + username);
        } catch (WebSocketException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }

        ws.addListener(new WebSocketAdapter() {
            // when server sends client something:
            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                // sign up prompts
                if (message.startsWith("Display Name -> ")) {
                    printReset(BOLD + message);
                    String name = scanner.nextLine();
                    printReset(BOLD + "Password -> ");
                    String password = scanner.nextLine();
                    ws.sendText("signup@" + username + "@" + password + "@" + name);
                }

                // log in prompts
                if (message.startsWith("Password -> ")) {
                    printReset(BOLD + message);
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
                if (message.startsWith("displayContacts@")) {
                    String[] info = message.split("@");
                    contactsScreenInput(Integer.parseInt(info[1]));
                }

                // username does not exist, load contacts screen again
                if (message.equals("notValidUser")) {
                    printlnReset(RED + ITALICS + "[USER DOES NOT EXIST]");
                }

                // contact added green message
                if (message.startsWith("greenMessage@")) {
                    String[] info = message.split("@");
                    printlnReset(GREEN + ITALICS + info[1]);
                }

                // display contacts menu (back, new)
                if (message.equals("displayContactsMenu")) {
                    contactsMenu();
                }

                // individual contact's info/settings
                if (message.startsWith("contactInfo@")) {
                    System.out.println();
                    String[] info = message.split("@");

                    System.out.println("username: " + info[1]);
                    System.out.println("display: " + info[2]);

                    printlnReset(BOLD + hex("#f5d773") + info[1]);
                    printReset(BOLD + hex("#ebac1a") + "[1] ");
                    printlnReset(BOLD + "Change display name");
                    printReset(BOLD + hex("#ebac1a") + "[2] ");
                    printlnReset(BOLD + "Remove contact");
                    printReset(BOLD + hex("#ebac1a") + "[3] ");
                    printlnReset(BOLD + "Open conversation");
                    printReset(BOLD + hex("#ebac1a") + "[4] ");
                    printlnReset(BOLD + "Back");
                    System.out.print("-> ");

                    // results of contact info choice
                    String choice = scanner.nextLine();
                    System.out.println("choice: " + choice);
                    switch (choice) {
                        case "1" -> {
                            System.out.println("Current display name -> " + info[2]);
                            System.out.print("New display name -> ");
                            String newName = scanner.nextLine();
                            ws.sendText("changeContactName@" + info[1] + "@" + newName);
                        }
                        case "2" -> ws.sendText("removeContact@" + info[1]);
                        case "3" -> {
                            // check if user is added back
                            ws.sendText("openConversation@" + username + "@" + info[1]);
                            ws.sendText("responseConversation@" + username + "@" + info[1]);
                        }
                        case "4" -> contactsMenu();
                    }
                }

                // line for user to type their message
                if (message.startsWith("arrowConversation(*)")) {
                    System.out.println();
                    String[] info = message.split("\\(\\*\\)");
                    String displayName = info[1];
                    String otherUser = info[2];
                    printReset(BOLD + displayName);
                    System.out.print(" -> ");
                    String msg = scanner.nextLine();
                    if (msg.equals("*exit*")) {
                        chatsMenu();
                    } else {
                        LocalDateTime dateTime = LocalDateTime.now();
                        ws.sendText("sendingMsg(*)" + username + "(*)" + otherUser + "(*)" + msg + "(*)" +
                                dateTime.getMonthValue() + "/" + dateTime.getDayOfMonth() +
                                "(*)" + dateTime.getHour() + ":" + dateTime.getMinute());
                    }
                }

                // if message was sent by user
                if (message.startsWith("yourMessage(*)")) {
                    String[] info = message.split("\\(\\*\\)");
                    printReset(BRIGHT_WHITE + BOLD + info[1] + ": ");
                    System.out.println(info[2]);
                }

                // if message was sent by other user
                if (message.startsWith("theirMessage(*)")) {
                    String[] info = message.split("\\(\\*\\)");
                    printReset(BRIGHT_BLUE + BOLD + info[1] + ": ");
                    System.out.println(info[2]);
                }

                // if message was sent by the system
                if (message.startsWith("systemMessage(*)")) {
                    String[] info = message.split("\\(\\*\\)");
                    printlnReset(ITALICS + GREEN + info[1]);
                }

                // chats menu and chat previews
                if (message.startsWith("chatPreviewList(*)")) {
                    String[] info = message.split("\\(\\*\\)");
                    System.out.println("info1:" + info[1]);
                    System.out.println("info2:" + info[2]);
                    info[1] = info[1].substring(1, info[1].length() - 1);
                    info[2] = info[2].substring(1, info[2].length() - 1);
                    ArrayList<String> usernames = new ArrayList<>(Arrays.asList(info[1].split(", *")));
                    ArrayList<String> previews = new ArrayList<>(Arrays.asList(info[2].split("],")));
                    System.out.println("usernames: " + usernames);
                    System.out.println("previews: " + previews);
                    int i;
                    for (i = 0; i < usernames.size(); i++) {
                        printReset(BOLD + hex("#1e72e3") + "[" + (i + 1) + "] ");
                        printlnReset(BOLD + usernames.get(i));
                        printlnReset("   " + previews.get(i));
                    }
                    chatScreenInput(i + 1);
                }
            }
        });
    }

    // displays the main menu
    private void mainMenu() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        String formattedDate = myDateObj.format(myFormatObj);

        System.out.println();
        printlnReset(BOLD + hex("#bc94e0") + "== Main Menu ==");
        System.out.println(formattedDate);
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
        printlnReset(BOLD + hex("#ebac1a") + "[BACK]  [NEW]");
        ws.sendText("allContactNames");
    }

    // input for contacts screen
    private void contactsScreenInput(int numOfContacts) {
        System.out.print("-> ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("back")) {
            mainMenu();
            return;
        }
        if (input.equalsIgnoreCase("new")) {
            System.out.print("New contact username -> ");
            String contactName = scanner.nextLine();
            ws.sendText("newContact@" + contactName);
            return;
        }
        try {
            if (Integer.parseInt(input) <= numOfContacts) {
                ws.sendText("getContactInfo " + input);
                return;
            }
        } catch (NumberFormatException ignored) {}
        printlnReset(RED + ITALICS + "[INVALID OPTION]");
        contactsMenu();
    }

    // the chats screen
    private void chatsMenu() {
        System.out.println();
        printlnReset(BOLD + hex("#78aff5") + "== Conversations ==");
        printlnReset(BOLD + hex("#1e72e3") + "[BACK]");
        ws.sendText("allConversationPreview@" + username);
    }

    // the settings screen
    private void settingsMenu() {
        System.out.println();
        printlnReset(BOLD + hex("#e38fa7") + "== Settings ==");
        ws.sendText("getDisplayName@" + username);
    }

    // input for the chats screen
    private void chatScreenInput(int numOfChats) {
        System.out.print("-> ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("back")) {
            mainMenu();
            return;
        }
        try {
            if (Integer.parseInt(input) <= numOfChats) {
                ws.sendText("openConversation@" + username + "@" + Integer.parseInt(input));
                return;
            }
        } catch (NumberFormatException ignored) {}
        printlnReset(RED + ITALICS + "[INVALID OPTION]");
        chatsMenu();
    }
}
