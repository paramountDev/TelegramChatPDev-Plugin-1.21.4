package dev.paramountdev.telegramChat.pictures;

import com.loohp.imageframe.ImageFrame;
import com.loohp.imageframe.objectholders.ImageMap;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageCommand implements CommandExecutor {

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

            List<ItemFrame> spawnedFrames = new ArrayList<>();


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

            String createCommand = "imageframe create " + fullName + " " + url + " " + width + " " + height + " combined";
            Bukkit.dispatchCommand(player, createCommand);

            Rotation rotation = getCorrectRotation(face);

            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {

                ImageMap imageMap = ImageFrame.imageMapManager.getFromCreator(player.getUniqueId(), fullName);
                if (imageMap == null) {
                    player.sendMessage(ChatColor.RED + "Unable to load image map.");
                    return;
                }

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
                        (frame, item) -> {},
                        fullName
                );

                player.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1, 1f);

            }, 40L);


            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                for (ItemFrame frame : spawnedFrames) {
                    if (!frame.isDead()) {
                        frame.remove();
                    }
                }
            }, 20L * 60);

            return true;
        }

        return false;
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


}
