package dev.paramountdev.telegramChat;

import dev.paramountdev.telegramChat.bot.TelegramBot;
import dev.paramountdev.telegramChat.bot.utils.EmojiConvertor;
import dev.paramountdev.telegramChat.commands.TgChatCommand;
import dev.paramountdev.telegramChat.commands.ImageCommand;
import dev.paramountdev.telegramChat.utils.FolderLoader;
import dev.paramountdev.telegramChat.utils.FramesListener;
import dev.paramountdev.telegramChat.voices.VoiceCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class TelegramChat extends JavaPlugin implements Listener, CommandExecutor {

    private TelegramBot telegramBot;
    private static TelegramChat plugin;

    private String botToken;
    private String botUsername;

    private String author;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        this.botToken = getConfig().getString("telegram.bot-token");
        this.botUsername = getConfig().getString("telegram.bot-username");

        this.author = getConfig().getString("author");

        if(!author.equals("ParamountDev")) {
            sendAuthorErrorMessage();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        FolderLoader.exportResource("guide.txt");
        FolderLoader.createPluginFolders();

        setUpBot();
        setUpPlugin();

        sendSignatureToConsole("enabled");
    }

    @Override
    public void onDisable() {
        if (telegramBot != null) {
            telegramBot.stop();
        }


        sendSignatureToConsole("disabled");
    }




    private void setUpBot(){
        telegramBot = new TelegramBot(botToken, botUsername, this);
        telegramBot.start();
        telegramBot.setPlugin(this);
    }

    private void setUpPlugin() {
        getCommand("tgchat").setExecutor(new TgChatCommand());
        getCommand("showimage").setExecutor(new ImageCommand());
        getCommand("listenvoice").setExecutor(new VoiceCommand());

        getServer().getPluginManager().registerEvents(new FramesListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }






    public void sendTelegramMessageToMinecraft(Player player, String from, String message) {
        String formatted = "§b[TG] §7" + from + ": §f" + message;
        player.sendMessage(formatted);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1f);
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 1f);
    }



    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage();

        if (msg.startsWith("/")) return;

        if (msg.startsWith("@")) {
            msg = msg.substring(1).trim();
            msg = EmojiConvertor.replaceTextCodesWithEmojis(msg);

            Long chatId = telegramBot.getLinkedPlayers().get(player.getName());
            if (chatId != null) {
                telegramBot.sendMessage(chatId, "💬 " + player.getName() + ": " + msg);
                player.sendMessage(" ");
                player.sendMessage("§a✅ Message sent to Telegram.");
                player.sendMessage("§7" + msg);
                player.sendMessage(" ");

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1f);

            } else {
                player.sendMessage(" ");
                player.sendMessage("§c❌ You are not connected to Telegram.");
                player.sendMessage(" ");

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1f);
            }

            event.setCancelled(true);
        }
    }








    public static TelegramChat getInstance() {
        return plugin;
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }



    private void sendAuthorErrorMessage() {
        final String reset = "\u001B[0m";
        final int width = 58;
        final String borderColor = "\u001B[95m";

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        sendEmptyLinesToConsole(8, console);

        console.sendMessage(borderColor + "╔" + "═".repeat(width) + "╗" + reset);
        console.sendMessage(colorizeCenteredText("Do not change Author value in config.", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        console.sendMessage(borderColor + "╚" + "═".repeat(width) + "╝" + reset);

        sendEmptyLinesToConsole(8, console);
    }

    private void sendSignatureToConsole(String pluginStatus) {
        final String reset = "\u001B[0m";
        final int width = 58;
        final String borderColor = "\u001B[95m";

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        sendEmptyLinesToConsole(3, console);
        console.sendMessage(colorizeCenteredText("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        sendEmptyLinesToConsole(1, console);
        console.sendMessage(colorizeCenteredText("Don't be afraid of exceptions.", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        console.sendMessage(colorizeCenteredText("They may occur if you are running the plugin for the first time.", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        console.sendMessage(colorizeCenteredText("Follow the guide.txt and and set up the config.", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        sendEmptyLinesToConsole(8, console);

        console.sendMessage(borderColor + "╔" + "═".repeat(width) + "╗" + reset);

        console.sendMessage(colorizeCenteredText("⚡️ TelegramChat Plugin " + pluginStatus + " ⚡️", width, "\u001B[35m", "\u001B[32m", "\u001B[35m") + reset);
        console.sendMessage(colorizeCenteredText("Made by ParamountDev", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        sendEmptyLinesToConsole(1, console);
        console.sendMessage(colorizeCenteredText("✉️  Telegram: https://t.me/paramount1_dev ✉️", width, "\u001B[96m", "\u001B[97m", "\u001B[96m") + reset);
        console.sendMessage(colorizeCenteredText("💰 FunPay: https://funpay.com/uk/users/14397429/ 💰", width, "\u001B[32m", "\u001B[97m", "\u001B[32m") + reset);

        console.sendMessage(borderColor + "╚" + "═".repeat(width) + "╝" + reset);

        sendEmptyLinesToConsole(8, console);
    }

    private void sendEmptyLinesToConsole(int count, ConsoleCommandSender console) {
        for (int i = 0; i < count; i++) {
            console.sendMessage("");
        }
    }

    private String colorizeCenteredText(String text, int width, String colorStart, String colorMiddle, String colorEnd) {
        String cleanText = text.replaceAll("\u001B\\[[;\\d]*m", "");
        int textLength = cleanText.length();

        int totalPadding = width - textLength;
        int paddingLeft = totalPadding / 2;
        int paddingRight = totalPadding - paddingLeft;

        StringBuilder colored = new StringBuilder();
        colored.append(" ".repeat(Math.max(0, paddingLeft)));

        if (textLength == 0) {
            colored.append(" ".repeat(textLength));
        } else if (textLength == 1) {
            colored.append(colorStart).append(text);
        } else {
            String firstChar = text.substring(0, 1);
            String middleChars = text.substring(1, text.length() - 1);
            String lastChar = text.substring(text.length() - 1);

            colored.append(colorStart).append(firstChar);
            colored.append(colorMiddle != null ? colorMiddle : colorStart).append(middleChars);
            if (colorEnd != null) {
                colored.append(colorEnd).append(lastChar);
            } else {
                colored.append(colorStart).append(lastChar);
            }
        }

        colored.append(" ".repeat(Math.max(0, paddingRight)));
        return colored.toString();
    }
}
