package dev.paramountdev.telegramChat.bot;

import dev.paramountdev.telegramChat.TelegramChat;
import dev.paramountdev.telegramChat.bot.utils.EmojiConvertor;
import dev.paramountdev.telegramChat.bot.utils.OggToWavConvertor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot {

    private final String token;
    private final String username;
    private TelegramBotsApi api;
    private TelegramChat plugin;
    private FileConfiguration config;

    private final Map<Long, Long> connectionTimestamps = new HashMap<>(); // chatId -> timestamp
    private final Map<Long, String> telegramUsernames = new HashMap<>(); // chatId -> telegram username


    private final Map<String, Long> linkedPlayers = new HashMap<>(); // playerName -> telegramChatId
    private final Map<Long, String> linkedUsers = new HashMap<>();   // telegramChatId -> playerName
    private final Map<String, Long> pendingRequests = new HashMap<>(); // playerName -> telegramChatId


    public final Map<String, String> voiceTranscripts = new HashMap<>();


    private String voksModelPath;


    public TelegramBot(String token, String username, TelegramChat plugin) {
        this.token = token;
        this.username = username;
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.voksModelPath = config.getString("voice-messages.vosk_model");
    }

    public void start() {
        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            String text = msg.getText();
            String username = msg.getFrom().getUserName();
            if (username == null || username.isEmpty())  {
                username = msg.getFrom().getFirstName();
            }

            telegramUsernames.put(msg.getChatId(), username);

            if (text.equals("/start")) {

                SendMessage welcome = new SendMessage();
                welcome.setChatId(String.valueOf(msg.getChatId()));
                welcome.setText("👋 Hello!\n" +
                        "My name is ParamountDev and this is a bot for sending messages between Telegram and Minecraft.\n\n" +
                        "🧡 Thank you for using my plugin!\n\n" +
                        "👇 Select a player to contact:");


                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();

                InlineKeyboardButton selectButton1 = new InlineKeyboardButton();
                selectButton1.setText("🎮 Select Player");
                selectButton1.setCallbackData("select_players");

                InlineKeyboardButton authorButton = new InlineKeyboardButton();
                authorButton.setText("👤 Author");
                authorButton.setCallbackData("author");

                rows.add(List.of(selectButton1));
                rows.add(List.of(authorButton));
                markup.setKeyboard(rows);

                welcome.setReplyMarkup(markup);


                try {
                    execute(welcome);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }

        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("select_players")) {
                sendOnlinePlayersList(chatId);
                return;
            }
            if (callbackData.equals("author")) {
                sendAuthorInfo(chatId);
                return;
            }

            if (callbackData.equals("back_to_main")) {
                sendMainMenu(chatId);
                return;
            }

            if (callbackData.startsWith("link_to_")) {
                String playerName = callbackData.substring("link_to_".length());

                if (linkedPlayers.containsKey(playerName) || linkedUsers.containsKey(chatId)) {
                    sendMessage(chatId, """
                            <b>❌ Connection Error</b>
                            
                            One of the participants is already linked to another chat.
                            """);
                    return;
                }

                Player player = Bukkit.getPlayerExact(playerName);
                if (player == null) {
                    sendMessage(chatId, """
                            <b>❌ Connection unavailable</b>
                            
                            The player is <b>offline</b> right now. Please try again later.
                            """);

                    return;
                }

                pendingRequests.put(playerName, chatId);

                TextComponent accept = new TextComponent("§a[ACCEPT]");
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tgchat accept " + chatId));

                TextComponent deny = new TextComponent("§c[DECLINE]");
                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tgchat deny " + chatId));

                player.sendMessage("§8§m----------------------------------------");
                player.sendMessage(" ");
                player.sendMessage("§e📩 Telegram chat request! 📩");
                player.sendMessage(" ");
                player.sendMessage("§7User §b@" + update.getCallbackQuery().getFrom().getUserName() + " §7wants to connect with you.");
                player.sendMessage(" ");
                player.sendMessage("");
                player.sendMessage(" ");

                BaseComponent[] centeredButtons = new ComponentBuilder("")
                        .append("        ").append(accept).append("        ").append(deny).create();

                player.spigot().sendMessage(centeredButtons);
                player.sendMessage(" ");
                player.sendMessage("§8§m----------------------------------------");

                player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, 1, 1);


                sendMessage(chatId, """
                        <b>⏳ Request sent</b>
                        
                        Waiting for player <b>%s</b> to accept...
                        """.formatted(playerName));


                return;
            }

            if (callbackData.equals("stop")) {
                stopChat(chatId);
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            String text = msg.getText();
            long chatId = msg.getChatId();

            if (text.equals("/start")) {
                // Кнопка выбора
            }

            if (text.equals("/stop")) {
                stopChat(chatId);
                return;
            }



            if (!linkedUsers.containsKey(chatId)) {
                sendMessage(chatId, "<b>❌ You are not connected to a chat.</b>");
                return;
            }

            String playerName = linkedUsers.get(chatId);
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                String processed = EmojiConvertor.processEmojiMessage(text);
                plugin.sendTelegramMessageToMinecraft(player, msg.getFrom().getUserName(), processed);
            } else {
                sendMessage(chatId, "<b>❌ Player is offline. Disconnected. </b>");
                stopChat(chatId);
            }
        }

        if (update.hasMessage() && !update.getMessage().hasText()) {
            Message msg = update.getMessage();
            String username = msg.getFrom().getUserName();
            if (username == null) username = msg.getFrom().getFirstName();

            String messageType = getMessageType(msg);

            if (messageType.equalsIgnoreCase("Sent a photo")) {
                handlePhotoMessage(msg, username);
                return;
            }

            if (messageType.equalsIgnoreCase("Sent a voice message")) {
                handleVoiceMessage(msg, username);
                return;
            }

            if (plugin != null && linkedUsers.containsKey(msg.getChatId())) {
                String playerName = linkedUsers.get(msg.getChatId());
                Player player = Bukkit.getPlayerExact(playerName);

                if (player != null) {
                    plugin.sendTelegramMessageToMinecraft(player, username, messageType);
                } else {
                    sendMessage(msg.getChatId(), "<b>❌ Player is offline. Disconnected. </b>");
                    stopChat(msg.getChatId());
                }
            } else {
                sendMessage(msg.getChatId(), """
                        <b>❌ You are not connected</b>
                        
                        Use the /start command and select a player to chat with.
                        """);


            }

        }
    }

    private void sendMainMenu(long chatId) {
        SendMessage welcome = new SendMessage();
        welcome.setChatId(String.valueOf(chatId));
        welcome.setParseMode("HTML");
        welcome.setText("""
            👋 <b>Hello!</b>
            My name is <b>ParamountDev</b> and this is a bot for sending messages between <b>Telegram</b> and <b>Minecraft</b>.
            
            🧡 Thank you for using my plugin!
            
            👇 Select an action:
            """);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton selectPlayer = new InlineKeyboardButton("🎮 Select Player");
        selectPlayer.setCallbackData("select_players");

        InlineKeyboardButton author = new InlineKeyboardButton("👤 Author");
        author.setCallbackData("author");

        rows.add(List.of(selectPlayer));
        rows.add(List.of(author));

        markup.setKeyboard(rows);
        welcome.setReplyMarkup(markup);

        try {
            execute(welcome);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(text);
        msg.setParseMode("HTML");
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void stopChat(long chatId) {
        String player = linkedUsers.remove(chatId);
        if (player != null) {
            linkedPlayers.remove(player);
            Player p = Bukkit.getPlayerExact(player);
            if (p != null) {
                p.sendMessage(" ");
                p.sendMessage("§c❌ Telegram chat disconnected.");
                p.sendMessage(" ");
            }
        }
        sendMessage(chatId, """
                <b>❌ Connection ended</b>
                
                Your chat with the Minecraft player has been disconnected.
                """);


    }


    private void sendOnlinePlayersList(long chatId) {
        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);

        if (onlinePlayers.length == 0) {
            SendMessage msg = new SendMessage();
            msg.setChatId(String.valueOf(chatId));
            msg.setText("❌ No players are currently online.");
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("🎮 Online players:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Player player : onlinePlayers) {
            InlineKeyboardButton playerButton = new InlineKeyboardButton();
            playerButton.setText(player.getName());
            playerButton.setCallbackData("link_to_" + player.getName());

            rows.add(List.of(playerButton));
        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAuthorInfo(long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setParseMode("HTML");

        msg.setText("""
                <b>👋 Hello! I'm <u>ParamountDev</u></b>
                🇺🇦 Developer from <b>Ukraine</b>
                
                ⚡ <b>Creator of TelegramChat Plugin</b>
                💡 I make <i>custom plugins</i> for all Minecraft versions & server types.
                
                ✅ <b>I sell ready-to-use plugins</b>
                🛠 <b>I accept commissions too!</b>
                
                👉 <a href="https://t.me/paramount1_dev">Click here to contact or order</a>
                
                ❤️ Thank you for using my plugin!
                """);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("🔙 Back");
        backButton.setCallbackData("select_players");

        rows.add(List.of(backButton));
        markup.setKeyboard(rows);
        msg.setReplyMarkup(markup);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    // VOICE MESSAGES

    private void handleVoiceMessage(Message msg, String username) {
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
            voiceTranscripts.put(fileUniqueId, text);

            String message = "§b[TG] §7" + username + ": §fSent a voice message ";

            TextComponent viewButton = new TextComponent("§b[LISTEN]");
            viewButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            viewButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§7Click to view text from voice message").create()));

            if (!linkedUsers.containsKey(msg.getChatId())) {
                sendMessage(msg.getChatId(), "<b>❌ You are not connected to a chat.</b>");
                return;
            }

            String playerName = linkedUsers.get(msg.getChatId());
            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) {
                sendMessage(msg.getChatId(), "<b>❌ Player is offline. Disconnected. </b>");
                stopChat(msg.getChatId());
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
        org.telegram.telegrambots.meta.api.objects.File telegramFile = getTelegramFile(fileId);
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

    // VOICE MESSAGES


    // PHOTO MESSAGES

    private void handlePhotoMessage(Message msg, String username) {
        List<PhotoSize> photos = msg.getPhoto();
        if (photos == null || photos.isEmpty()) return;

        PhotoSize best = photos.get(photos.size() - 1);

        try {
            org.telegram.telegrambots.meta.api.objects.File telegramFile = getTelegramFile(best.getFileId());
            String filePath = telegramFile.getFilePath();

            String url = "https://api.telegram.org/file/bot" + token + "/" + filePath;

            String imageName = "telegramImage_" + best.getFileUniqueId();

            int width = 5;
            int height = 5;

            sendImageMessageToMinecraft(msg.getChatId(), username, imageName, url, width, height);


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private org.telegram.telegrambots.meta.api.objects.File getTelegramFile(String fileId) throws TelegramApiException {
        return execute(new org.telegram.telegrambots.meta.api.methods.GetFile(fileId));
    }

    public void sendImageMessageToMinecraft(Long chatId, String username, String imageName, String url, int width, int height) {
        String message = "§b[TG] §7" + username + ": §fSent a photo ";


        String command = "/showimage " + imageName + " " + url + " " + width + " " + height;

        TextComponent viewButton = new TextComponent("§b[WATCH]");
        viewButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        viewButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Click to view the image").create()));

        String playerName = linkedUsers.get(chatId);
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            stopChat(chatId);
            return;
        }

        player.sendMessage("");
        player.spigot().sendMessage(new ComponentBuilder(message).append(viewButton).create());
        player.sendMessage("");

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1f);
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 1f);
    }


    // PHOTO MESSAGES


    private String getMessageType(Message msg) {
        if (msg.hasSticker()) return "Sent a sticker";
        if (msg.hasVoice()) return "Sent a voice message";
        if (msg.hasAudio()) return "Sent an audio file";
        if (msg.hasVideo()) return "Sent a video file";
        if (msg.hasPhoto()) return "Sent a photo";
        if (msg.hasDocument()) return "Sent a document";
        if (msg.hasVideoNote()) return "Sent a video note";
        return "Sent an unsupported message type";
    }






    public String formatTimestamp(long timestamp) {
        if (timestamp <= 0) return "Unknown";

        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        return formatter.format(instant);
    }


    public long getConnectionTime(Long chatId) {
        return connectionTimestamps.getOrDefault(chatId, -1L);
    }

    public String getTelegramUsernameByChatId(long chatId) {
        return telegramUsernames.getOrDefault(chatId, "Unknown");
    }



    public void stop() {
    }


    public void setPlugin(TelegramChat plugin) {
        this.plugin = plugin;
    }

    public Map<Long, Long> getConnectionTimestamps() {
        return connectionTimestamps;
    }

    public Map<String, Long> getPendingRequests() {
        return pendingRequests;
    }

    public Map<Long, String> getLinkedUsers() {
        return linkedUsers;
    }

    public Map<String, Long> getLinkedPlayers() {
        return linkedPlayers;
    }


    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }


}


