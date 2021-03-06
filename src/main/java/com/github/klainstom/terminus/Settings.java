package com.github.klainstom.terminus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;

public class Settings {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private static final File settingsFile = ExtensionMain.DATA_DIRECTORY.resolve("settings.json").toFile();

    private static SettingsState currentSettings = null;

    public static void read() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
            currentSettings = gson.fromJson(reader, SettingsState.class);
        } catch (FileNotFoundException e) {
            currentSettings = new SettingsState();
            try {
                write();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void write() throws IOException {
        String json = gson.toJson(currentSettings);
        Writer writer = new FileWriter(settingsFile);
        writer.write(json);
        writer.close();
    }

    public static List<String> getSettingsLines() {
        return List.of(
                "Address: " + getServerIp() + ":" + getServerPort()
        );
    }

    private static class SettingsState {
        private final String SSH_IP;
        private final int SSH_PORT;

        private SettingsState() {
            this.SSH_IP = "localhost";
            this.SSH_PORT = 2222;
        }

    }

    public static String getServerIp() { return currentSettings.SSH_IP; }
    public static int getServerPort() { return currentSettings.SSH_PORT; }
}
