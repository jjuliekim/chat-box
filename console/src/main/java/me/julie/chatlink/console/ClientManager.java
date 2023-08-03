package me.julie.chatlink.console;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.util.Scanner;

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
        ws.sendText(scanner.nextLine());
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) { // when server sends client something
                System.out.println(message);
                ws.sendText(scanner.nextLine());
            }
        });
    }
}
