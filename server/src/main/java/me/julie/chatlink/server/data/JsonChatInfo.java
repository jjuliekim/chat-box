package me.julie.chatlink.server.data;

import java.util.List;
import java.util.Map;

public class JsonChatInfo {
    private Map<List<String>, List<ChatInfo>> chatLogs;

    // <usernames, all their chat logs>
    public JsonChatInfo(Map<List<String>, List<ChatInfo>> chatLogs) {
        this.chatLogs = chatLogs;
    }

    public Map<List<String>, List<ChatInfo>> getChatLogs() {
        return chatLogs;
    }

    public void setChatLogs(Map<List<String>, List<ChatInfo>> chatLogs) {
        this.chatLogs = chatLogs;
    }
}