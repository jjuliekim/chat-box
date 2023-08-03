package me.julie.chatlink.server.data;

import java.util.Map;

public class JsonLoginInfo {
    private Map<String, String> logins;

    public JsonLoginInfo(Map<String, String> logins) {
        this.logins = logins;
    }

    public Map<String, String> getLogins() {
        return logins;
    }

    public void setLogins(Map<String, String> logins) {
        this.logins = logins;
    }
}