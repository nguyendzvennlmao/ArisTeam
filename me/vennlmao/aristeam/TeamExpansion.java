package me.vennlmao.aristeam;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TeamExpansion extends PlaceholderExpansion {

    private final ArisTeam plugin;

    public TeamExpansion(ArisTeam plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "VennLMAO";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "aristeams";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "4.5";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        Team team = plugin.getTeamManager().getTeamByPlayer(player.getUniqueId());

        if (params.equalsIgnoreCase("name")) {
            return (team != null) ? team.getName() : "Không đội";
        }

        if (params.equalsIgnoreCase("owner")) {
            return (team != null) ? org.bukkit.Bukkit.getOfflinePlayer(team.getOwner()).getName() : "";
        }

        if (params.equalsIgnoreCase("count")) {
            return (team != null) ? String.valueOf(team.getMembers().size()) : "0";
        }

        return null;
    }
                }
