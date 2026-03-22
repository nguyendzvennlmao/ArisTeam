package me.vennlmao.aristeam;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportManager {
    private final ArisTeam plugin;
    public TeleportManager(ArisTeam plugin) { this.plugin = plugin; }
    public void startTeleport(Player p, Location loc) {
        Location start = p.getLocation();
        AtomicInteger count = new AtomicInteger(5);
        plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, (task) -> {
            if (!p.isOnline() || p.getLocation().distance(start) > 0.1) {
                plugin.getMsgManager().send(p, "teleport-cancelled", null);
                task.cancel();
                return;
            }
            int cur = count.getAndDecrement();
            if (cur > 0) plugin.getMsgManager().send(p, "teleporting", Map.of("%time%", String.valueOf(cur)));
            else {
                p.teleportAsync(loc).thenAccept(s -> plugin.getMsgManager().send(p, "teleport-success", null));
                task.cancel();
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }
                                                }
