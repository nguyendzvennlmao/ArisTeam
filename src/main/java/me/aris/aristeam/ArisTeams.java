package me.aris.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ArisTeams extends JavaPlugin {
    private static ArisTeams instance;
    private TeamManager teamManager;
    private ConfigManager configManager;
    private TeamGUI teamGUI;
    private Map<UUID, Long> teleportCooldown = new ConcurrentHashMap<>();
    private Map<UUID, String> pendingInvites = new ConcurrentHashMap<>();
    private Map<UUID, String> pendingJoin = new ConcurrentHashMap<>();
    private ActionBarTask actionBarTask;

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
            actionBarTask = new ActionBarTask(this);
            getServer().getGlobalRegionScheduler().runAtFixedRate(this, task -> actionBarTask.run(), 1L, 20L);
        }
        
        getLogger().info("ArisTeams version 1.2 da duoc bat!");
    }

    @Override
    public void onDisable() {
        if (teamManager != null) {
            teamManager.saveAllTeams();
        }
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
