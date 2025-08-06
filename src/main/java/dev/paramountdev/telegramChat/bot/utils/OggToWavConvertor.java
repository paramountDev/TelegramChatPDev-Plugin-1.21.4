package dev.paramountdev.telegramChat.bot.utils;

import dev.paramountdev.telegramChat.TelegramChat;

import java.io.File;
import java.io.IOException;

public class OggToWavConvertor {

    public static File convertOggToWav(File oggFile) throws IOException, InterruptedException {
        File wavFile = new File(oggFile.getParent(), oggFile.getName().replace(".ogg", ".wav"));
        File ffmpeg = new File(TelegramChat.getInstance().getDataFolder(), "ffmpeg/ffmpeg.exe");

        if (!ffmpeg.exists()) {
            throw new IllegalStateException("ffmpeg.exe not found: " + ffmpeg.getAbsolutePath());
        }

        ProcessBuilder pb = new ProcessBuilder(
                ffmpeg.getAbsolutePath(),
                "-i", oggFile.getAbsolutePath(),
                "-ar", "16000",
                "-ac", "1",
                "-sample_fmt", "s16",
                wavFile.getAbsolutePath()
        );

        pb.redirectErrorStream(true); // объединяет stderr и stdout
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0 || !wavFile.exists()) {
            throw new IOException("FFmpeg conversion failed with code " + exitCode);
        }

        return wavFile;
    }
}
