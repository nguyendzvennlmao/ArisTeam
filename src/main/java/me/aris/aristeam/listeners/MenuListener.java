package me.aris.aristeam.listeners;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.manager.TeamManager;
import me.aris.aristeam.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.UUID;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().contains("Xác nhận")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            if (slot == 11) {
                String type = p.hasMetadata("aris_gui_type") ? p.getMetadata("aris_gui_type").get(0).asString() : "";
                TeamManager tm = ArisTeams.getInstance().getTeamManager();
                if (type.equalsIgnoreCase("disband") && tm.hasTeam(p)) {
                    tm.disbandTeam(tm.getTeam(p).name);
                    MessageUtils.sendMessage(p, "team-disbanded");
                } else if (type.equalsIgnoreCase("kick")) {
                    String targetName = p.hasMetadata("aris_gui_target") ? p.getMetadata("aris_gui_target").get(0).asString() : "";
                    Player target = Bukkit.getPlayer(targetName);
                    if (target != null) {
                        tm.leaveTeam(target);
                        MessageUtils.sendMessage(target, "kicked-from-team");
                        MessageUtils.sendMessage(p, "player-kicked", "%player%", targetName);
                    }
                } else if (type.equalsIgnoreCase("leave") && tm.hasTeam(p)) {
                    tm.leaveTeam(p);
                    MessageUtils.sendMessage(p, "player-left");
                }
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                p.removeMetadata("aris_gui_type", ArisTeams.getInstance());
                p.removeMetadata("aris_gui_target", ArisTeams.getInstance());
            } else if (slot == 15) {
                p.closeInventory();
                p.removeMetadata("aris_gui_type", ArisTeams.getInstance());
                p.removeMetadata("aris_gui_target", ArisTeams.getInstance());
            }
        }
    }
    }
