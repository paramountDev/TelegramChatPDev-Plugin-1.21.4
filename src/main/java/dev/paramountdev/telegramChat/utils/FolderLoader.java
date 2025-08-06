package dev.paramountdev.telegramChat.utils;

import dev.paramountdev.telegramChat.TelegramChat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;

public class FolderLoader {

    public static void exportResource(String resourcePath) {
        File outFile = new File(TelegramChat.getInstance().getDataFolder(), resourcePath);

        try {
            if (TelegramChat.getInstance().getResource(resourcePath) == null && TelegramChat.getInstance().getResource(resourcePath + "/") == null) {
                TelegramChat.getInstance().getLogger().warning("Resource not found: " + resourcePath);
                return;
            }

            InputStream folderCheck = TelegramChat.getInstance().getResource(resourcePath + "/");
            if (folderCheck != null) {
                File folder = new File(TelegramChat.getInstance().getDataFolder(), resourcePath);
                folder.mkdirs();
                for (String file : Objects.requireNonNull(getResourceFolderFiles(resourcePath))) {
                    exportResource(resourcePath + "/" + file);
                }
                return;
            }

            InputStream in = TelegramChat.getInstance().getResource(resourcePath);
            if (in == null) {
                TelegramChat.getInstance().getLogger().warning("Unable to get resource: " + resourcePath);
                return;
            }

            outFile.getParentFile().mkdirs();
            if (!outFile.exists()) {
                try (OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] getResourceFolderFiles(String folderPath) {
        try {
            InputStream in = TelegramChat.getInstance().getResource(folderPath + "/");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            return br.lines().toArray(String[]::new);
        } catch (Exception e) {
            return null;
        }
    }

    public static void createPluginFolders() {
        File modelsFolder = new File(TelegramChat.getInstance().getDataFolder(), "models");
        File ffmpegFolder = new File(TelegramChat.getInstance().getDataFolder(), "ffmpeg");

        if (!modelsFolder.exists()) {
            boolean created = modelsFolder.mkdirs();
            if (created) TelegramChat.getInstance().getLogger().info("Folder created: models");
        }

        if (!ffmpegFolder.exists()) {
            boolean created = ffmpegFolder.mkdirs();
            if (created) TelegramChat.getInstance().getLogger().info("Folder created: ffmpeg");
        }
    }
}
