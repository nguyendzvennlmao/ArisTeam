package me.aris.aristeam.utils;

import me.aris.aristeam.ArisTeams;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportHandler {
    public static void startTeleport(Player player, Location target) {
        int delay = ArisTeams.getInstance().getConfig().getInt("settings.teleport.delay");
        Location startLoc = player.getLocation();
        player.playSound(player.getLocation(), Sound.valueOf(ArisTeams.getInstance().getConfig().getString("sounds.teleport-start")), 1f, 1f);

        new BukkitRunnable() {
            int count = delay;
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                if (player.getLocation().distance(startLoc) > 0.1) {
                    MessageUtils.sendMessage(player, "teleport-cancelled");
                    player.playSound(player.getLocation(), Sound.valueOf(ArisTeams.getInstance().getConfig().getString("sounds.teleport-cancel")), 1f, 1f);
                    cancel();
                    return;
                }
                if (count <= 0) {
                    player.teleport(target);
                    player.playSound(player.getLocation(), Sound.valueOf(ArisTeams.getInstance().getConfig().getString("sounds.teleport-success")), 1f, 1f);
                    cancel();
                    return;
                }
                player.playSound(player.getLocation(), Sound.valueOf(ArisTeams.getInstance().getConfig().getString("sounds.teleport-countdown")), 1f, 1f);
                MessageUtils.sendActionBar(player, "teleporting", "%time%", String.valueOf(count));
                count--;
            }
        }.runTaskTimer(ArisTeams.getInstance(), 0L, 20L);
    }
}
