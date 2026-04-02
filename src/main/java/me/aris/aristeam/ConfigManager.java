package me.aris.aristeam;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ConfigManager {
    private Aristeam plugin;
    private FileConfiguration messages;
    private FileConfiguration gui;
    private File messageFile;
    private File guiFile;

    public ConfigManager(Aristeam plugin) {
        this.plugin = plugin;
        this.messageFile = new File(plugin.getDataFolder(), "message.yml");
        this.guiFile = new File(plugin.getDataFolder(), "teamgui.yml");
        reloadMessages();
        reloadGUI();
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messageFile);
    }
    
    public void reloadGUI() {
        gui = YamlConfiguration.loadConfiguration(guiFile);
    }

    public String getMessage(String path) {
        String msg = messages.getString("messages." + path);
        if (msg == null) return colorize("&cKhong tim thay message: " + path);
        return colorize(msg);
    }
    
    public FileConfiguration getGUI() { return gui; }
    
    public String colorize(String msg) { return ChatColor.translateAlternateColorCodes('&', msg); }
    
    public String getPvpStatusText(boolean enabled) {
        String path = enabled ? "settings.pvp.status.enabled" : "settings.pvp.status.disabled";
        return colorize(plugin.getConfig().getString(path));
    }
    
    public String getRoleColor(String role) {
        String path = "settings.display.colors." + role.toLowerCase();
        return colorize(plugin.getConfig().getString(path, "&7"));
    }
    
    public String getStatusColor(boolean online) {
        String path = online ? "settings.display.colors.online" : "settings.display.colors.offline";
        return colorize(plugin.getConfig().getString(path, online ? "&a" : "&c"));
    }
          }
