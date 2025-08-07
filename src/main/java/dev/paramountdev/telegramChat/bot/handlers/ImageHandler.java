package dev.paramountdev.telegramChat.bot.handlers;

import dev.paramountdev.telegramChat.TelegramChat;
import dev.paramountdev.telegramChat.bot.TelegramBot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class ImageHandler {

    private TelegramBot bot;
    private String token;

    public ImageHandler(TelegramChat plugin, TelegramBot bot, String token) {
        this.bot = bot;
        this.token = token;
    }


    public void handlePhotoMessage(Message msg, String username) {
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

    public org.telegram.telegrambots.meta.api.objects.File getTelegramFile(String fileId) throws TelegramApiException {
        return bot.execute(new org.telegram.telegrambots.meta.api.methods.GetFile(fileId));
    }

    public void sendImageMessageToMinecraft(Long chatId, String username, String imageName, String url, int width, int height) {
        String message = "§b[TG] §7" + username + ": §fSent a photo ";


        String command = "/showimage " + imageName + " " + url + " " + width + " " + height;

        TextComponent viewButton = new TextComponent("§b[WATCH]");
        viewButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        viewButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Click to view the image").create()));

        String playerName = bot.getLinkedUsers().get(chatId);
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            bot.stopChat(chatId);
            return;
        }

        player.sendMessage("");
        player.spigot().sendMessage(new ComponentBuilder(message).append(viewButton).create());
        player.sendMessage("");

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1f);
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 1f);
    }

}
