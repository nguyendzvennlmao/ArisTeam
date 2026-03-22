package me.vennlmao.aristeam;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportManager {
    private final ArisTeam plugin;
    public TeleportManager(ArisTeam plugin) { this.plugin = plugin; }

    public void startTeleport(Player p, Location loc) {
        Location startPos = p.getLocation();
        AtomicInteger seconds = new AtomicInteger(plugin.getConfig().getInt("settings.teleport-delay"));

        plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, (task) -> {
            if (!p.isOnline() || p.getLocation().distance(startPos) > plugin.getConfig().getDouble("settings.teleport-cancel-distance")) {
                plugin.getMsgManager().send(p, "teleport-cancelled", null);
                p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.teleport-fail")), 1f, 1f);
                task.cancel();
                return;
            }

            int current = seconds.getAndDecrement();
            if (current > 0) {
                plugin.getMsgManager().send(p, "teleporting", Map.of("%time%", String.valueOf(current)));
                p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.teleport-tick")), 1f, 1f);
            } else {
                p.teleportAsync(loc).thenAccept(success -> {
                    if (success) {
                        plugin.getMsgManager().send(p, "teleport-success", null);
                        p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.teleport-success")), 1f, 1f);
                    }
                });
                task.cancel();
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }
        }
