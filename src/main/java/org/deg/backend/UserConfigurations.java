package org.deg.backend;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class UserConfigurations {
    public static String USERNAME = null;
    public static File DEFAULT_SAFE_PATH = null;

    private static final String APP_NAME = "LocalDrop";
    private static final String CONFIG_FILE_NAME = "config.properties";

    private static File getDefaultDownloadPath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return new File(userHome, "Downloads/LocalDrop");
        } else if (os.contains("mac")) {
            return new File(userHome, "Downloads/LocalDrop");
        } else {
            return new File(userHome, "Downloads/LocalDrop");
        }
    }

    private static File getConfigFile() {
        String os = System.getProperty("os.name").toLowerCase();
        String configDir;

        if (os.contains("win")) {
            configDir = System.getenv("APPDATA");
        } else {
            String home = System.getProperty("user.home");
            configDir = home + "/.config";
        }

        Path appConfigDir = Path.of(configDir, APP_NAME);
        if (!Files.exists(appConfigDir)) {
            try {
                Files.createDirectories(appConfigDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return appConfigDir.resolve(CONFIG_FILE_NAME).toFile();
    }

    public static void saveConfigurations() {
        Properties props = new Properties();
        props.setProperty("username", USERNAME);
        props.setProperty("defaultSafePath", DEFAULT_SAFE_PATH.getAbsolutePath());

        try (FileOutputStream out = new FileOutputStream(getConfigFile())) {
            props.store(out, "User Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfigurations() {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            USERNAME = System.getProperty("user.name");
            DEFAULT_SAFE_PATH = getDefaultDownloadPath();
        } else {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
                USERNAME = props.getProperty("username", System.getProperty("user.name"));
                DEFAULT_SAFE_PATH = new File(props.getProperty("defaultSafePath", getDefaultDownloadPath().toString()));
            } catch (IOException e) {
                e.printStackTrace();
                // fallback to default
                DEFAULT_SAFE_PATH = getDefaultDownloadPath();
            }
        }

        if (!DEFAULT_SAFE_PATH.exists()) DEFAULT_SAFE_PATH.mkdirs();
    }
}
