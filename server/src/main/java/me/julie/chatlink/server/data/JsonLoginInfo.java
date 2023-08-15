package me.julie.chatlink.server.data;

import java.util.Map;

public class JsonLoginInfo {
    private Map<String, UserInfo> logins;

    // <username, UserInfo>
    public JsonLoginInfo(Map<String, UserInfo> logins) {
        this.logins = logins;
    }

    public Map<String, UserInfo> getLogins() {
        return logins;
    }

    public void setLogins(Map<String, UserInfo> logins) {
        this.logins = logins;
    }
}