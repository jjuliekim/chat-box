package me.julie.chatlink.server.data;

import java.util.List;

public class UserInfo {
    private String username;
    private String password;
    private String displayName;
    private List<String> contacts; // list of display names ?

    public UserInfo(String username, String password, String displayName, List<String> contacts) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.contacts = contacts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }
}
