package me.aris.aristeam;

import org.bukkit.plugin.java.JavaPlugin;
import me.aris.aristeam.commands.TeamCommand;
import me.aris.aristeam.manager.TeamManager;
import me.aris.aristeam.listeners.MenuListener;
import me.aris.aristeam.utils.ColorUtils;

public class ArisTeams extends JavaPlugin {
    private static ArisTeams instance;
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfigs();
        teamManager = new TeamManager();
        teamManager.loadData();
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("team").setTabCompleter(new TeamCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getLogger().info(ColorUtils.colorize("&#facc15ArisTeams đã sẵn sàng trên Folia!"));
    }

    private void saveDefaultConfigs() {
        saveDefaultConfig();
        saveResource("teamgui.yml", false);
        saveResource("messages.yml", false);
    }

    public void reloadPlugin() {
        reloadConfig();
        teamManager.loadData();
    }

    public static ArisTeams getInstance() { return instance; }
    public TeamManager getTeamManager() { return teamManager; }
}
