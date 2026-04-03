package me.aris.aristeam;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {
    private final ArisTeams plugin;

    public PlaceholderHook(ArisTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "aristeams";
    }

    @Override
    public @NotNull String getAuthor() {
        return "VennLMAO";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        
        if (identifier.equals("name")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                return plugin.getConfigManager().colorize(team.getName());
            } else {
                return plugin.getConfigManager().colorize(plugin.getConfig().getString("settings.placeholder.no_team", "&7Khong co team"));
            }
        }
        
        if (identifier.equals("online")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                return String.valueOf(team.getOnlineCount());
            }
            return "0";
        }
        
        if (identifier.equals("total")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                return String.valueOf(team.getMemberCount());
            }
            return "0";
        }
        
        if (identifier.equals("role")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                String role = team.getRole(player.getUniqueId());
                switch (role) {
                    case "OWNER": return plugin.getConfigManager().colorize("&6Chu Team");
                    case "ADMIN": return plugin.getConfigManager().colorize("&bAdmin");
                    default: return plugin.getConfigManager().colorize("&7Thanh Vien");
                }
            }
            return "";
        }
        
        if (identifier.equals("pvp_status")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                return plugin.getConfigManager().getPvpStatusText(team.isPvpEnabled());
            }
            return plugin.getConfigManager().getPvpStatusText(false);
        }
        
        return null;
    }
  }
