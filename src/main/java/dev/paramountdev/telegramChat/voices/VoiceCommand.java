package dev.paramountdev.telegramChat.voices;

import dev.paramountdev.telegramChat.TelegramChat;
import dev.paramountdev.telegramChat.bot.TelegramBot;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class VoiceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("listenvoice")) {
            if (!(sender instanceof Player)) return true;

            Player player = (Player) sender;

            if (args.length != 1) {
                return true;
            }

            String voiceId = args[0];
            TelegramBot bot = TelegramChat.getInstance().getTelegramBot();

            File voiceFile = new File(TelegramChat.getInstance().getDataFolder(), "voice_messages/voice_" + voiceId + ".ogg");
            if (!voiceFile.exists()) {
                player.sendMessage("§Voice message not found.");
                return true;
            }

            String transcript = bot.voiceTranscripts.get(voiceId);
            if (transcript == null) {
                player.sendMessage("§7🔄 Recognize the voice...");

                Bukkit.getScheduler().runTaskAsynchronously(TelegramChat.getInstance(), () -> {
                    String result = bot.recognizeSpeech(voiceFile);
                    if (result == null || result.trim().isEmpty()) result = "Unable to recognize voice.";

                    bot.voiceTranscripts.put(voiceId, result);

                    String finalResult = result;
                    Bukkit.getScheduler().runTask(TelegramChat.getInstance(), () -> {
                        player.sendMessage("§6🎤 Recognized voice: §f");
                        player.sendMessage("");
                        player.sendMessage("     " + finalResult + "     ");
                        player.sendMessage("");
                    });
                });

            } else {
                player.sendMessage("§6🎤 Recognized text: §f");
                player.sendMessage("");
                player.sendMessage("     " + transcript + "     ");
                player.sendMessage("");

                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 1, 1f);
            }
            return true;
        }

        return false;
    }

}
