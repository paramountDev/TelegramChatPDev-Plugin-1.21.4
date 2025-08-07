package dev.paramountdev.telegramChat.utils;

import dev.paramountdev.telegramChat.pictures.ImageCommand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

public class FramesListener implements Listener {

    @EventHandler
    public void onFrameBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            if(ImageCommand.getSpawnedFrames().contains(itemFrame)) {
                event.setCancelled(true);
            }
        }
    }

}
