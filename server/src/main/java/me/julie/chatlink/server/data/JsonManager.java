package me.julie.chatlink.server.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class JsonManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
    private final File directory = new File("testing");

    private JsonLoginInfo loginInfo;

    public void load() throws IOException {
        loginInfo = loadFromFile("logins.json", JsonLoginInfo.class, new JsonLoginInfo(new HashMap<>()));
    }

    public void save() throws IOException {
        saveToFile("logins.json", loginInfo);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private <T> T loadFromFile(String fileName, Class<T> type, T defaultValue) throws IOException {
        File file = new File(directory, fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (FileReader fileReader = new FileReader(file)) {
            T result = gson.fromJson(fileReader, type);
            return result == null ? defaultValue : result;
        }
    }

    private void saveToFile(String fileName, Object data) throws IOException {
        try (FileWriter fileWriter = new FileWriter(new File(directory, fileName))) {
            gson.toJson(data, fileWriter);
        }
    }

    public JsonLoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(JsonLoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }
}
