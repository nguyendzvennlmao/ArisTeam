package me.aris.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ArisTeams extends JavaPlugin {
    private static ArisTeams instance;
    private TeamManager teamManager;
    private ConfigManager configManager;
    private TeamGUI teamGUI;
    private Map<UUID, Long> teleportCooldown = new HashMap<>();
    private Map<UUID, String> pendingInvites = new HashMap<>();
    private Map<UUID, String> pendingJoin = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("message.yml", false);
        saveResource("teamgui.yml", false);
        
        configManager = new ConfigManager(this);
        teamManager = new TeamManager(this);
        teamGUI = new TeamGUI(this);
        
        getCommand("team").setExecutor(new TeamCommand(this));
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
        
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook(this).register();
            getLogger().info("Da hook vao PlaceholderAPI!");
        } else {
            getLogger().warning("PlaceholderAPI khong tim thay! Placeholder se khong hoat dong!");
        }
        
        if (getConfig().getBoolean("settings.actionbar.enabled")) {
            new ActionBarTask(this).runTaskTimer(this, 0L, 20L);
        }
        
        getLogger().info("ArisTeams version 1.1 da duoc bat!");
    }

    @Override
    public void onDisable() {
        teamManager.saveAllTeams();
        getLogger().info("ArisTeams da duoc tat!");
    }

    public static ArisTeams getInstance() { return instance; }
    public TeamManager getTeamManager() { return teamManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public TeamGUI getTeamGUI() { return teamGUI; }
    public Map<UUID, Long> getTeleportCooldown() { return teleportCooldown; }
    public Map<UUID, String> getPendingInvites() { return pendingInvites; }
    public Map<UUID, String> getPendingJoin() { return pendingJoin; }
}
