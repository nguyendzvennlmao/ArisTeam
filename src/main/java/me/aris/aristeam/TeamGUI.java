package me.aris.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

public class TeamGUI {
    private ArisTeams plugin;
    private FileConfiguration guiConfig;

    public TeamGUI(ArisTeams plugin) {
        this.plugin = plugin;
        this.guiConfig = plugin.getConfigManager().getGUI();
    }

    private void playSound(Player p, String soundPath) {
        if (!plugin.getConfig().getBoolean("settings.sounds.enabled")) return;
        String soundName = plugin.getConfig().getString(soundPath);
        if (soundName != null) {
            try {
                Sound sound = Sound.valueOf(soundName);
                p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {}
        }
    }

    private void playGuiSound(Player p, String type) {
        playSound(p, "settings.sounds.gui." + type);
    }

    private String color(String msg) {
        return plugin.getConfigManager().colorize(msg);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        if (lore != null) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) coloredLore.add(color(line));
            meta.setLore(coloredLore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public void openMainGUI(Player p) {
        Inventory gui = Bukkit.createInventory(null, 54, color(guiConfig.getString("gui.main.title")));
        playGuiSound(p, "open");
        
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        String pvpStatus = plugin.getConfigManager().getPvpStatusText(team != null && team.isPvpEnabled());
        
        for (String key : guiConfig.getConfigurationSection("gui.main.items").getKeys(false)) {
            String path = "gui.main.items." + key;
            int slot = guiConfig.getInt(path + ".slot");
            Material mat = Material.getMaterial(guiConfig.getString(path + ".material"));
            String name = guiConfig.getString(path + ".name");
            List<String> lore = new ArrayList<>();
            for (String line : guiConfig.getStringList(path + ".lore")) {
                lore.add(line.replace("%pvp_status%", pvpStatus));
            }
            gui.setItem(slot, createItem(mat, name, lore));
        }
        
        p.openInventory(gui);
    }
    
    public void openMemberListGUI(Player p) {
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team == null) return;
        
        Inventory gui = Bukkit.createInventory(null, 54, color(guiConfig.getString("gui.members.title")));
        playGuiSound(p, "open");
        
        int slot = 0;
       
