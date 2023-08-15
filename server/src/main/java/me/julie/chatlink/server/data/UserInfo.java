package me.julie.chatlink.server.data;

import java.util.List;

public class UserInfo {
    private String username;
    private String password;
    private String displayName;
    private List<String> contacts;

    public UserInfo(String username, String password, String displayName, List<String> contacts) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.contacts = contacts;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getContacts() {
        return contacts;
    }
}
