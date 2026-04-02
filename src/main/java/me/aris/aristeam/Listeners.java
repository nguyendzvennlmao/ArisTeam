package me.aris.aristeam;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Listeners implements Listener {
    private Aristeam plugin;

    public Listeners(Aristeam plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        
        if (title.equals(plugin.getConfigManager().getMessage("gui.main_title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            
            switch (e.getSlot()) {
                case 11:
                    plugin.getTeamGUI().openMainGUI(p);
                    break;
                case 12:
                    plugin.getTeamGUI().openMemberListGUI(p);
                    break;
                case 13:
                    p.performCommand("team pvp");
                    p.closeInventory();
                    break;
                case 14:
                    p.performCommand("team sethome");
                    p.closeInventory();
                    break;
                case 15:
                    p.performCommand("team home");
                    p.closeInventory();
                    break;
            }
        }
        
        else if (title.contains("XAC NHAN")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            
            if (e.getSlot() == 15) {
                String pendingTeam = plugin.getPendingJoin().get(p.getUniqueId());
                if (pendingTeam != null) {
                    plugin.getTeamManager().createTeam(p, pendingTeam);
                    p.sendMessage(plugin.getConfigManager().getMessage("create.success").replace("%team%", pendingTeam));
                    plugin.getPendingJoin().remove(p.getUniqueId());
                } else {
                    Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                    if (team != null) {
                        if (title.contains("GIAI TAN")) {
                            String teamName = team.getName();
                            for (Player member : team.getOnlineMembers()) {
                                member.sendMessage(plugin.getConfigManager().getMessage("disband.announce").replace("%player%", p.getName()).replace("%team%", teamName));
                            }
                            plugin.getTeamManager().deleteTeam(teamName);
                            p.sendMessage(plugin.getConfigManager().getMessage("disband.success"));
                        } else if (title.contains("KICK")) {
                            p.performCommand("team kick confirm");
                        } else {
                            String teamName = team.getName();
                            plugin.getTeamManager().removeMember(teamName, p.getUniqueId());
                            p.sendMessage(plugin.getConfigManager().getMessage("leave.success"));
                            for (Player member : team.getOnlineMembers()) {
                                member.sendMessage(plugin.getConfigManager().getMessage("leave.announce").replace("%player%", p.getName()));
                            }
                        }
                    }
                }
                p.closeInventory();
            } 
            else if (e.getSlot() == 11) {
                plugin.getPendingJoin().remove(p.getUniqueId());
                p.sendMessage(plugin.getConfigManager().getMessage("action_cancelled"));
                p.closeInventory();
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team != null) {
            for (Player member : team.getOnlineMembers()) {
                member.sendMessage(plugin.getConfigManager().getMessage("join_online").replace("%player%", p.getName()));
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team != null) {
            for (Player member : team.getOnlineMembers()) {
                member.sendMessage(plugin.getConfigManager().getMessage("leave_offline").replace("%player%", p.getName()));
            }
        }
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Team team = plugin.getTeamManager().getPlayerTeam(e.getPlayer().getUniqueId());
            if (team != null && !team.isPvpEnabled() && plugin.getConfig().getBoolean("settings.pvp.disable_enderpearl")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(plugin.getConfigManager().getMessage("pvp.disabled_pearl"));
            }
        }
    }
  }
