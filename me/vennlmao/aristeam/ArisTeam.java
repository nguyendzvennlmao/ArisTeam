package me.vennlmao.aristeam;

import org.bukkit.plugin.java.JavaPlugin;

public class ArisTeam extends JavaPlugin {
    private MessageManager msgManager;
    private TeamManager teamManager;
    private TeleportManager tpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("gui.yml", false);
        this.msgManager = new MessageManager(this);
        this.teamManager = new TeamManager(this);
        this.tpManager = new TeleportManager(this);
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TeamExpansion(this).register();
        }
        TeamCommand cmd = new TeamCommand(this);
        getCommand("team").setExecutor(cmd);
        getCommand("team").setTabCompleter(cmd);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }
    public MessageManager getMsgManager() { return msgManager; }
    public TeamManager getTeamManager() { return teamManager; }
    public TeleportManager getTeleportManager() { return tpManager; }
  }
