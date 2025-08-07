package dev.paramountdev.telegramChat.commands;

import dev.paramountdev.telegramChat.TelegramChat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TgChatCommand implements CommandExecutor, TabCompleter {

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


                TelegramChat.getInstance().getTelegramBot().getConnectionTimestamps().put(chatId, System.currentTimeMillis());
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

                if(TelegramChat.getInstance().getTelegramBot().getLinkedPlayers().containsKey(player.getName())) {
                    TelegramChat.getInstance().getTelegramBot().getLinkedPlayers().remove(player.getName());
                }
                if(TelegramChat.getInstance().getTelegramBot().getLinkedUsers().containsValue(player.getName())) {
                    for(Long id: TelegramChat.getInstance().getTelegramBot().getLinkedUsers().keySet()) {
                        if(TelegramChat.getInstance().getTelegramBot().getLinkedUsers().get(id).equals(player.getName())) {
                            TelegramChat.getInstance().getTelegramBot().getLinkedUsers().remove(id);
                        }
                    }
                }
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
                Long chatId = TelegramChat.getInstance().getTelegramBot().getLinkedPlayers().get(player.getName());

                player.sendMessage(" ");
                player.sendMessage("§8§m----------------------------------------");
                player.sendMessage("§b§lTelegramChat §7» §fConnection Info");
                player.sendMessage(" ");

                if (chatId == null) {
                    player.sendMessage("§cYou are not connected to any Telegram chat.");
                } else {
                    String tgUsername = TelegramChat.getInstance().getTelegramBot().getTelegramUsernameByChatId(chatId);
                    long connectionTime = TelegramChat.getInstance().getTelegramBot().getConnectionTime(chatId);
                    boolean online = TelegramChat.getInstance().getTelegramBot().getLinkedUsers().containsKey(chatId);

                    player.sendMessage("§7» §fTelegram Username: §b@" + (tgUsername != null ? tgUsername : "unknown"));
                    player.sendMessage("§7» §fChat ID: §e" + chatId);
                    player.sendMessage("§7» §fStatus: " + (online ? "§aConnected" : "§cDisconnected"));
                    player.sendMessage("§7» §fConnected Since: §d" + TelegramChat.getInstance().getTelegramBot().formatTimestamp(connectionTime));
                    player.sendMessage(" ");
                    player.sendMessage("§7To disconnect: §c/tgchat stop");
                }

                player.sendMessage("§8§m----------------------------------------");
                player.sendMessage(" ");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1f);
                return true;
            }


            if (args.length == 1 && args[0].equalsIgnoreCase("author")) {
                sendAuthorMessage(player);
                return true;
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        if (command.getName().equalsIgnoreCase("tgchat")) {
            if (args.length == 1) {
                return Arrays.asList("stop", "info", "author");
            }
        }

        return Collections.emptyList();
    }


    private void sendAuthorMessage(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_GREEN + "§m                                                      ");
        player.sendMessage(ChatColor.DARK_GREEN + "   §6✦ §eTelegramChatPDev §6by §aParamountDev §6✦");
        player.sendMessage(ChatColor.DARK_GREEN + "§m                                                      ");
        player.sendMessage("");

        player.sendMessage("§7Contact for help or order plugin:");
        TextComponent funpay = new TextComponent("§6• §bFunPay Profile");
        funpay.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://funpay.com/uk/users/14397429/"));
        funpay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Open FunPay Profile").create()));
        player.spigot().sendMessage(funpay);
        TextComponent telegram = new TextComponent("§6• §bTelegram: @paramount1_dev");
        telegram.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://t.me/paramount1_dev"));
        telegram.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Open Telegram").create()));
        player.spigot().sendMessage(telegram);

        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_GREEN + "§m                                                      ");
        player.sendMessage("");
    }
}
