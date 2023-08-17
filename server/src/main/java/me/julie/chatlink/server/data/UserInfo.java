package me.julie.chatlink.server.data;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private String username;
    private String password;
    private String displayName;
    private List<ContactInfo> contacts;

    public UserInfo(String username, String password, String displayName, List<ContactInfo> contacts) {
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

    public ArrayList<String> getContactUsernames() {
        ArrayList<String> contactUsernames = new ArrayList<>();
        for (ContactInfo contact : contacts) {
            contactUsernames.add(contact.getUsername());
        }
        return contactUsernames;
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

    public List<ContactInfo> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactInfo> contacts) {
        this.contacts = contacts;
    }
}
