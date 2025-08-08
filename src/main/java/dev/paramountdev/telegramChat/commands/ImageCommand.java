package dev.paramountdev.telegramChat.commands;

import com.loohp.imageframe.ImageFrame;
import com.loohp.imageframe.objectholders.DitheringType;
import com.loohp.imageframe.objectholders.ImageMap;
import com.loohp.imageframe.objectholders.ImageMapCreationTask;
import com.loohp.imageframe.objectholders.URLAnimatedImageMap;
import com.loohp.imageframe.objectholders.URLStaticImageMap;
import com.loohp.imageframe.utils.HTTPRequestUtils;
import com.loohp.interactivechat.libs.com.loohp.platformscheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ImageCommand implements CommandExecutor {

    private static final List<ItemFrame> spawnedFrames = new ArrayList<>();;
    private int lifetimeSeconds = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getInt("image-messages.lifetime", 60);


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (command.getName().equalsIgnoreCase("showimage") && args.length == 4) {
            String imageName = args[0];
            String url = args[1];
            int width = Integer.parseInt(args[2]);
            int height = Integer.parseInt(args[3]);

            Location base = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(3));
            base.setY(player.getLocation().getY() + 5);

            BlockFace face = player.getFacing().getOppositeFace();
            Vector right = getRightDirection(face);
            Vector up = new Vector(0, 1, 0);
            Vector forward = face.getDirection().multiply(-0.01);



            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Location frameLoc = base.clone()
                            .add(right.clone().multiply(x))
                            .add(up.clone().multiply(-y))
                            .add(0.5, 0.5, 0)
                            .add(forward);

                    ItemFrame frame = player.getWorld().spawn(frameLoc, ItemFrame.class);
                    frame.setFacingDirection(face, true);
                    frame.setInvulnerable(true);
                    frame.setGravity(false);
                    frame.setVisible(true);
                    frame.setFixed(true);

                    spawnedFrames.add(frame);
                }
            }


            String fullName = imageName + new Random().nextInt(15000);
            Rotation rotation = getCorrectRotation(face);

            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {

                createImageMap(player, fullName, url, width, height, true, imageMap -> {
                    if (imageMap != null) {
                        player.sendMessage(ChatColor.GREEN + "Image created!");
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                        List<ItemFrame> rearrangedFrames = new ArrayList<>();
                        for (int y = 0; y < height; y++) {
                            for (int x = width - 1; x >= 0; x--) {
                                rearrangedFrames.add(spawnedFrames.get(y * width + x));
                            }
                        }


                        imageMap.fillItemFrames(
                                rearrangedFrames,
                                rotation,
                                (frame, item) -> true,
                                (frame, item) -> {
                                },
                                fullName
                        );

                        player.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1, 1f);

                    } else {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.RED + "Failed to create image map.");
                        player.sendMessage(" ");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    }
                });
        },40L);


        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
            for (ItemFrame frame : spawnedFrames) {
                if (!frame.isDead()) {
                    frame.remove();
                }
            }
        }, 20L * lifetimeSeconds);

        return true;
    }

        return false;
}


public static void createImageMap(Player player, String fullName, String url, int width, int height, boolean combined, Consumer<ImageMap> onComplete) {
    UUID owner = player.getUniqueId();

    Set<ImageMap> existingMaps = ImageFrame.imageMapManager.getFromCreator(owner);

    if (existingMaps.stream().anyMatch(each -> each.getName().equalsIgnoreCase(fullName))) {
        player.sendMessage(" ");
        player.sendMessage(ChatColor.RED + "You already saw this picture!");
        player.sendMessage(" ");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        return;
    }

    if (!ImageFrame.isURLAllowed(url)) {
        player.sendMessage(" ");
        player.sendMessage(ChatColor.RED + "This URL is not allowed! Contact an admin.");
        player.sendMessage(" ");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        return;
    }


    Scheduler.runTaskAsynchronously(ImageFrame.plugin, () -> {
        ImageMapCreationTask<ImageMap> creationTask = null;

        try {
            String imageType = HTTPRequestUtils.getContentType(url);
            if (imageType == null) {
                imageType = URLConnection.guessContentTypeFromName(url);
            }
            if (imageType == null) imageType = "";

            DitheringType ditheringType = DitheringType.FLOYD_STEINBERG;

            String finalImageType = imageType;
            creationTask = ImageFrame.imageMapCreationTaskManager.enqueue(
                    owner, fullName, width, height,
                    () -> finalImageType.equals("image/gif") ?
                            URLAnimatedImageMap.create(ImageFrame.imageMapManager, fullName, url, width, height, ditheringType, owner).get() :
                            URLStaticImageMap.create(ImageFrame.imageMapManager, fullName, url, width, height, ditheringType, owner).get()
            );

            ImageMap imageMap = creationTask.get();
            ImageFrame.imageMapManager.addMap(imageMap);
            creationTask.complete(ImageFrame.messageImageMapCreated);

            Scheduler.runTask(ImageFrame.plugin, () -> onComplete.accept(imageMap));

        } catch (Exception e) {
            if (creationTask != null) {
                creationTask.complete(ImageFrame.messageUnableToLoadMap);
            }
            e.printStackTrace();
            Scheduler.runTask(ImageFrame.plugin, () -> onComplete.accept(null));
        }
    });
}


private Rotation getCorrectRotation(BlockFace face) {
    return Rotation.FLIPPED;
}

private Vector getRightDirection(BlockFace face) {
    switch (face) {
        case NORTH:
            return BlockFace.EAST.getDirection(); // вправо от NORTH
        case SOUTH:
            return BlockFace.WEST.getDirection(); // вправо от SOUTH
        case EAST:
            return BlockFace.SOUTH.getDirection(); // вправо от EAST
        case WEST:
            return BlockFace.NORTH.getDirection(); // вправо от WEST
        default:
            return new Vector(1, 0, 0); // fallback
    }
}

    public static List<ItemFrame> getSpawnedFrames() {
        return spawnedFrames;
    }

}
