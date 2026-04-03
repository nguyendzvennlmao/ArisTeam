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

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("message.yml", false);
        saveResource("teamgui.yml", false);
        
        configManager = new ConfigManager(this);
        teamManager = new TeamManager(this);
        teamGUI = new TeamGUI(this);
        
        TeamCommand teamCommand = new TeamCommand(this);
        getCommand("team").setExecutor(teamCommand);
        getCommand("team").setTabCompleter(teamCommand);
        
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
        
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook(this).register();
            getLogger().info("Da hook vao PlaceholderAPI!");
        } else {
            getLogger().warning("PlaceholderAPI khong tim thay! Placeholder se khong hoat dong!");
        }
        
        if (getConfig().getBoolean("settings.actionbar.enabled")) {
            getServer().getGlobalRegionScheduler().runAtFixedRate(this, task -> {
                if (!getConfig().getBoolean("settings.actionbar.enabled")) return;
                
                for (Player p : getServer().getOnlinePlayers()) {
                    try {
                        Team team = teamManager.getPlayerTeam(p.getUniqueId());
                        if (team != null) {
                            String pvpStatus = configManager.getPvpStatusText(team.isPvpEnabled());
                            String message = "&b" + team.getName() + " &7| &a" + team.getOnlineCount() + "&7/&c" + team.getMemberCount() + " &7| PVP: " + pvpStatus;
                            String coloredMessage = configManager.colorize(message);
                            p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                                new net.md_5.bungee.api.chat.TextComponent(coloredMessage));
                        } else {
                            String message = "&cBan chua o trong team nao!";
                            String coloredMessage = configManager.colorize(message);
                            p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                                new net.md_5.bungee.api.chat.TextComponent(coloredMessage));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1L, 20L);
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
