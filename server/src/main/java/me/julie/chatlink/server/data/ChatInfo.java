package me.julie.chatlink.server.data;

public class ChatInfo {
    private String username;
    private String message;
    private String date;
    private String time;

    public ChatInfo(String username, String message, String date, String time) {
        this.username = username;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public ChatInfo(String username, String message) {
        this.username = username;
        this.message = message;
        this.date = "";
        this.time = "";
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
