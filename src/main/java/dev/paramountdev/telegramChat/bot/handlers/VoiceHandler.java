package dev.paramountdev.telegramChat.bot.handlers;

import dev.paramountdev.telegramChat.TelegramChat;
import dev.paramountdev.telegramChat.bot.TelegramBot;
import dev.paramountdev.telegramChat.bot.utils.OggToWavConvertor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class VoiceHandler {

    private TelegramChat plugin;
    private TelegramBot bot;
    private String token;
    private String voksModelPath;

    public VoiceHandler(TelegramChat plugin, TelegramBot bot, String token, String voksModelPath) {
        this.plugin = plugin;
        this.bot = bot;
        this.token = token;
        this.voksModelPath = voksModelPath;
    }

    public void handleVoiceMessage(Message msg, String username) {
        if (!msg.hasVoice()) return;

        try {
            File voiceDir = new File(plugin.getDataFolder(), "voice_messages");
            if (!voiceDir.exists()) voiceDir.mkdirs();

            String fileId = msg.getVoice().getFileId();
            String fileUniqueId = msg.getVoice().getFileUniqueId();
            String fileName = "voice_" + fileUniqueId + ".ogg";

            File savedFile = new File(voiceDir, fileName);
            downloadFileToDisk(fileId, savedFile);

            File wavFile;
            try {
                wavFile = OggToWavConvertor.convertOggToWav(savedFile);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }


            String command = "/listenvoice " + fileUniqueId;

            String text = recognizeSpeech(wavFile);
            bot.getVoiceTranscripts().put(fileUniqueId, text);

            String message = "§b[TG] §7" + username + ": §fSent a voice message ";

            TextComponent viewButton = new TextComponent("§b[LISTEN]");
            viewButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            viewButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§7Click to view text from voice message").create()));

            if (!bot.getLinkedUsers().containsKey(msg.getChatId())) {
                bot.sendMessage(msg.getChatId(), "<b>❌ You are not connected to a chat.</b>");
                return;
            }

            String playerName = bot.getLinkedUsers().get(msg.getChatId());
            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) {
                bot.sendMessage(msg.getChatId(), "<b>❌ Player is offline. Disconnected. </b>");
                bot.stopChat(msg.getChatId());
                return;
            }

            player.sendMessage("");
            player.spigot().sendMessage(new ComponentBuilder(message).append(viewButton).create());
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1f);
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 1f);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String recognizeSpeech(File audioFile) {
        File modelDir = new File(plugin.getDataFolder(), "models/" + voksModelPath);
        try (Model model = new Model(modelDir.getAbsolutePath());
             AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile)) {

            AudioFormat baseFormat = ais.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    16000,
                    16,
                    1,
                    2,
                    16000,
                    false
            );
            AudioInputStream din = AudioSystem.getAudioInputStream(targetFormat, ais);

            Recognizer recognizer = new Recognizer(model, 16000);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = din.read(buffer)) >= 0) {
                recognizer.acceptWaveForm(buffer, bytesRead);
            }

            String resultJson = recognizer.getFinalResult();
            int textStart = resultJson.indexOf("\"text\"") + 8;
            int textEnd = resultJson.lastIndexOf("\"");
            return resultJson.substring(textStart, textEnd);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void downloadFileToDisk(String fileId, File destination) throws TelegramApiException, IOException {
        org.telegram.telegrambots.meta.api.objects.File telegramFile = bot.getTelegramFile(fileId);
        String filePath = telegramFile.getFilePath();

        String fileUrl = "https://api.telegram.org/file/bot" + token + "/" + filePath;

        try (InputStream in = new URL(fileUrl).openStream();
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }


}
