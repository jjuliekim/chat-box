package me.julie.chatlink.console;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.Scanner;

import static com.jackmeng.ansicolors.jm_Ansi.make;


public class ClientManager {
    public void run() throws IOException {
        WebSocketFactory factory = new WebSocketFactory();
        factory.setConnectionTimeout(5000);
        WebSocket ws = factory.createSocket("ws://localhost:20202/chat");
        Scanner scanner = new Scanner(System.in);
        try {
            ws.connect();
        } catch (WebSocketException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }

        make("Connected to ChatLink!").green().bold().print("");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        ws.sendText("username " + username);

        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) { // when server sends client something
                // sign up prompts
                if (message.equals("Display Name: ")) {
                    System.out.print(message);
                    String name = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    ws.sendText("signup " + username + " " + password);
                    ws.sendText("displayname " + username + " " + name);
                } else if (message.equals("Password: ")) { // log in prompts
                    System.out.print(message);
                    String password = scanner.nextLine();
                    ws.sendText("login " + username + " " + password);
                } else {
                    System.out.println(username + ": " + message);
                }
            }

        });
    }
}
