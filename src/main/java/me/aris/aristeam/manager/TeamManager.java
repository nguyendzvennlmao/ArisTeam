package me.aris.aristeam.manager;

import me.aris.aristeam.ArisTeams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;

public class TeamManager {
    private final Map<String, TeamData> teams = new HashMap<>();
    private final Map<UUID, String> playerTeam = new HashMap<>();

    public void loadData() {
        File folder = new File(ArisTeams.getInstance().getDataFolder(), "teams");
        if (!folder.exists()) folder.mkdirs();
    }

    public void saveData() {
        for (TeamData team : teams.values()) {
            File file = new File(ArisTeams.getInstance().getDataFolder(), "teams/" + team.name + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("name", team.name);
            config.set("owner", team.owner.toString());
            config.set("pvp", team.pvp);
            try { config.save(file); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public TeamData getTeam(Player p) {
        String name = playerTeam.get(p.getUniqueId());
        return name != null ? teams.get(name) : null;
    }
}
