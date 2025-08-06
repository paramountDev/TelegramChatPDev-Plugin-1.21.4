package dev.paramountdev.telegramChat.commands;

import dev.paramountdev.telegramChat.TelegramChat;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TgChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("tgchat")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                Long chatId = TelegramChat.getInstance().getTelegramBot().getLinkedPlayers().remove(player.getName());
                if (chatId != null) {
                    TelegramChat.getInstance().getTelegramBot().getLinkedUsers().remove(chatId);
                    TelegramChat.getInstance().getTelegramBot().sendMessage(chatId, "⛔ Player disconnected from Telegram chat.");
                }
                player.sendMessage(" ");
                player.sendMessage("§c⛔ Telegram connection disabled.");
                player.sendMessage(" ");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1f);
                return true;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
                long chatId = Long.parseLong(args[1]);
                if (TelegramChat.getInstance().getTelegramBot().getLinkedPlayers().containsKey(player.getName()) || TelegramChat.getInstance().getTelegramBot().getLinkedUsers().containsKey(chatId)) {
                    player.sendMessage(" ");
                    player.sendMessage("§c❌ Someone is already linked.");
                    player.sendMessage(" ");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1f);
                    return true;
                }
                TelegramChat.getInstance().getTelegramBot().getLinkedPlayers().put(player.getName(), chatId);
                TelegramChat.getInstance().getTelegramBot().getLinkedUsers().put(chatId, player.getName());
                TelegramChat.getInstance().getTelegramBot().getPendingRequests().remove(player.getName());

                TelegramChat.getInstance().getTelegramBot().sendMessage(chatId, "✅ Connection established with player '" + player.getName() + "'");
                player.sendMessage(" ");
                player.sendMessage("§a✅ Connected to Telegram successfully.");
                player.sendMessage(" ");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1f);
                return true;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("deny")) {
                long chatId = Long.parseLong(args[1]);
                TelegramChat.getInstance().getTelegramBot().getPendingRequests().remove(player.getName());
                TelegramChat.getInstance().getTelegramBot().sendMessage(chatId, "❌ Player declined the connection request.");
                player.sendMessage(" ");
                player.sendMessage("§e❌ Request denied.");
                player.sendMessage(" ");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1f);
                return true;
            }
        }

        return true;
    }


}
