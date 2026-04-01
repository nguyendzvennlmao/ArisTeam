package me.aris.aristeam;

import me.aris.aristeam.manager.TeamManager;
import me.aris.aristeam.manager.MenuManager;
import me.aris.aristeam.listeners.MenuListener;
import me.aris.aristeam.listeners.DamageListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisTeams extends JavaPlugin {
    private static ArisTeams instance;
    private TeamManager teamManager;
    private MenuManager menuManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.teamManager = new TeamManager();
        this.menuManager = new MenuManager();
        this.teamManager.loadData();
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        if (getCommand("team") != null) {
            getCommand("team").setExecutor(new me.aris.aristeam.commands.TeamCommand());
        }
    }

    @Override
    public void onDisable() {
        if (teamManager != null) {
            teamManager.saveData();
        }
    }

    public static ArisTeams getInstance() {
        return instance;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
}
