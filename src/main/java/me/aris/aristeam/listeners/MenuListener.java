package me.aris.aristeam.listeners;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.manager.TeamData;
import me.aris.aristeam.manager.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        TeamData team = ArisTeams.getInstance().getTeamManager().getTeam(p);
        ConfigurationSection guiCfg = ArisTeams.getInstance().getConfigManager().getGuiConfig();

        if (e.getView().getTitle().contains("ᴛᴇᴀᴍ ᴍᴀɪɴ")) {
            e.setCancelled(true);
            if (team == null) return;
            int slot = e.getRawSlot();
            ConfigurationSection items = guiCfg.getConfigurationSection("main-gui.items");

            if (slot == items.getInt("enderchest.slot")) p.openInventory(team.ec);
            else if (slot == items.getInt("pvp.slot") && ArisTeams.getInstance().getTeamManager().isOwner(p)) {
                team.pvp = !team.pvp;
                new MenuManager().openMain(p);
            } else if (slot == items.getInt("home.slot") && team.home != null) p.teleport(team.home);

        } else if (e.getView().getTitle().contains("Xác nhận")) {
            e.setCancelled(true);
            String type = p.hasMetadata("aris_gui_type") ? p.getMetadata("aris_gui_type").get(0).asString() : "";
            ConfigurationSection items = guiCfg.getConfigurationSection(type + "-gui.items");

            if (e.getRawSlot() == items.getInt("confirm.slot")) {
                if (type.equalsIgnoreCase("kick")) {
                    String target = p.getMetadata("aris_gui_target").get(0).asString();
                    ArisTeams.getInstance().getTeamManager().kickMember(team, Bukkit.getOfflinePlayer(target).getUniqueId());
                } else if (type.equalsIgnoreCase("disband")) {
                    ArisTeams.getInstance().getTeamManager().disbandTeam(team.name);
                } else if (type.equalsIgnoreCase("leave")) {
                    ArisTeams.getInstance().getTeamManager().leaveTeam(p);
                }
                p.closeInventory();
            } else if (e.getRawSlot() == items.getInt("cancel.slot")) p.closeInventory();
        }
    }
                }
