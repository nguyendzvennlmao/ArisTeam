package me.aris.aristeam.listeners;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.manager.TeamData;
import me.aris.aristeam.manager.TeamManager;
import me.aris.aristeam.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().contains("Xác nhận")) {
            e.setCancelled(true);
            if (e.getRawSlot() == 11) {
                String type = p.hasMetadata("aris_gui_type") ? p.getMetadata("aris_gui_type").get(0).asString() : "";
                TeamManager tm = ArisTeams.getInstance().getTeamManager();
                TeamData team = tm.getTeam(p);
                if (type.equalsIgnoreCase("kick") && p.hasMetadata("aris_gui_target")) {
                    String targetName = p.getMetadata("aris_gui_target").get(0).asString();
                    OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                    if (team != null && team.members.contains(target.getUniqueId())) {
                        tm.kickMember(team, target.getUniqueId());
                        MessageUtils.sendMessage(p, "&aĐã kick " + targetName);
                    }
                } else if (type.equalsIgnoreCase("disband") && tm.hasTeam(p)) {
                    tm.disbandTeam(team.name);
                    MessageUtils.sendMessage(p, "team-disbanded");
                } else if (type.equalsIgnoreCase("leave") && tm.hasTeam(p)) {
                    tm.leaveTeam(p);
                }
                p.closeInventory();
                p.removeMetadata("aris_gui_type", ArisTeams.getInstance());
                p.removeMetadata("aris_gui_target", ArisTeams.getInstance());
            } else if (e.getRawSlot() == 15) p.closeInventory();
        }
    }
    }
