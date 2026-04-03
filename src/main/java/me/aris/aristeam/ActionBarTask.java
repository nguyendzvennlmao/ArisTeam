package me.aris.aristeam;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarTask {
    private ArisTeams plugin;

    public ActionBarTask(ArisTeams plugin) {
        this.plugin = plugin;
    }

    public void run() {
        if (!plugin.getConfig().getBoolean("settings.actionbar.enabled")) return;
        
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            try {
                Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                if (team != null) {
                    String pvpStatus = plugin.getConfigManager().getPvpStatusText(team.isPvpEnabled());
                    String message = plugin.getConfigManager().getMessage("actionbar.team_info")
                        .replace("%team%", team.getName())
                        .replace("%online%", String.valueOf(team.getOnlineCount()))
                        .replace("%total%", String.valueOf(team.getMemberCount()))
                        .replace("%pvp_status%", pvpStatus);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getConfigManager().colorize(message)));
                } else {
                    String message = plugin.getConfigManager().getMessage("actionbar.no_team");
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getConfigManager().colorize(message)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
                }
