package me.julie.chatlink.server.data;

import java.util.Map;

// different for each user
public class JsonContactInfo {
    private Map<String, String> contacts;

    // <username, display name>
    public JsonContactInfo(Map<String, String> contacts) {
        this.contacts = contacts;
    }

    public Map<String, String> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, String> contacts) {
        this.contacts = contacts;
    }
}
