package dev.paramountdev.telegramChat.bot;

import dev.paramountdev.telegramChat.TelegramChat;
import dev.paramountdev.telegramChat.bot.handlers.VoiceHandler;
import dev.paramountdev.telegramChat.bot.utils.EmojiConvertor;
import dev.paramountdev.telegramChat.bot.handlers.ImageHandler;
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
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
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

    private ImageHandler imageHandler;
    private VoiceHandler voiceHandler;

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

        this.voiceHandler = new VoiceHandler(plugin, this, token, voksModelPath);
        this.imageHandler = new ImageHandler(plugin, this, token);

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
                imageHandler.handlePhotoMessage(msg, username);
                return;
            }

            if (messageType.equalsIgnoreCase("Sent a voice message")) {
                voiceHandler.handleVoiceMessage(msg, username);
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

    public void stopChat(long chatId) {
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

    public String formatTimestamp(long timestamp) {
        if (timestamp <= 0) return "Unknown";

        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        return formatter.format(instant);
    }




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

    public org.telegram.telegrambots.meta.api.objects.File getTelegramFile(String fileId) throws TelegramApiException {
        return execute(new org.telegram.telegrambots.meta.api.methods.GetFile(fileId));
    }

    public long getConnectionTime(Long chatId) {
        return connectionTimestamps.getOrDefault(chatId, -1L);
    }

    public String getTelegramUsernameByChatId(long chatId) {
        return telegramUsernames.getOrDefault(chatId, "Unknown");
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

    public Map<String, String> getVoiceTranscripts() {
        return voiceTranscripts;
    }

    public VoiceHandler getVoiceHandler() {
        return voiceHandler;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void stop() {}

    public void setPlugin(TelegramChat plugin) {
        this.plugin = plugin;
    }


}


