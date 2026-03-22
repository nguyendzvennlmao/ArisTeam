package me.vennlmao.aristeam;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TeamExpansion extends PlaceholderExpansion {
    private final ArisTeam plugin;
    public TeamExpansion(ArisTeam plugin) { this.plugin = plugin; }
    @Override public @NotNull String getIdentifier() { return "aristeams"; }
    @Override public @NotNull String getAuthor() { return "VennLMAO"; }
    @Override public @NotNull String getVersion() { return "4.2"; }
    @Override public boolean persist() { return true; }
    @Override public String onRequest(OfflinePlayer p, @NotNull String params) {
        if (p == null) return "";
        Team t = plugin.getTeamManager().getTeamByPlayer(p.getUniqueId());
        if (params.equalsIgnoreCase("name")) return (t != null) ? t.getName() : "No Team";
        return null;
    }
}
