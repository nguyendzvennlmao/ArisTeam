package me.aris.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Aristeam extends JavaPlugin {
    private static Aristeam instance;
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
        
        if (getConfig().getBoolean("settings.actionbar.enabled")) {
            new ActionBarTask(this).runTaskTimer(this, 0L, getConfig().getLong("settings.actionbar.update_interval"));
        }
        
        getLogger().info("Aristeam da duoc bat!");
    }

    @Override
    public void onDisable() {
        teamManager.saveAllTeams();
        getLogger().info("Aristeam da duoc tat!");
    }

    public static Aristeam getInstance() { return instance; }
    public TeamManager getTeamManager() { return teamManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public TeamGUI getTeamGUI() { return teamGUI; }
    public Map<UUID, Long> getTeleportCooldown() { return teleportCooldown; }
    public Map<UUID, String> getPendingInvites() { return pendingInvites; }
    public Map<UUID, String> getPendingJoin() { return pendingJoin; }
      }
