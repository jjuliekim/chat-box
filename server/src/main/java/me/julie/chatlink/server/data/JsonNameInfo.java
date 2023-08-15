package me.julie.chatlink.server.data;

import java.util.Map;

public class JsonNameInfo {
    private Map<String, String> names;

    // <username, display name>
    public JsonNameInfo(Map<String, String> names) {
        this.names = names;
    }

    public Map<String, String> getDisplayNames() {
        return names;
    }

    public void setDisplayNames(Map<String, String> names) {
        this.names = names;
    }
}
