package me.aris.aristeam;

import me.aris.aristeam.manager.TeamManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisTeams extends JavaPlugin {
    private static ArisTeams instance;
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        teamManager = new TeamManager();
        teamManager.loadData();
    }

    public static ArisTeams getInstance() { return instance; }
    public TeamManager getTeamManager() { return teamManager; }
}
