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
        for (UUID uuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            String name = member != null ? member.getName() : Bukkit.getOfflinePlayer(uuid).getName();
            boolean isOnline = member != null && member.isOnline();
            
            String role = team.getRole(uuid);
            String roleColor = "";
            String roleName = "";
            switch (role) {
                case "OWNER": 
                    roleColor = plugin.getConfigManager().getRoleColor("owner");
                    roleName = roleColor + "OWNER";
                    break;
                case "ADMIN": 
                    roleColor = plugin.getConfigManager().getRoleColor("admin");
                    roleName = roleColor + "ADMIN";
                    break;
                default: 
                    roleColor = plugin.getConfigManager().getRoleColor("member");
                    roleName = roleColor + "MEMBER";
            }
            
            String statusColor = plugin.getConfigManager().getStatusColor(isOnline);
            String status = statusColor + (isOnline ? "ONLINE" : "OFFLINE");
            
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            meta.setDisplayName(color(roleColor + name));
            List<String> lore = new ArrayList<>();
            lore.add(status);
            lore.add(roleName);
            meta.setLore(lore);
            head.setItemMeta(meta);
            gui.setItem(slot++, head);
        }
        
        p.openInventory(gui);
    }
    
    public void openConfirmCreateGUI(Player p, String teamName) {
        Inventory gui = Bukkit.createInventory(null, 27, color(guiConfig.getString("gui.confirm_create.title")));
        playGuiSound(p, "open");
        
        String confirmPath = "gui.confirm_create.items.confirm";
        Material confirmMat = Material.getMaterial(guiConfig.getString(confirmPath + ".material"));
        String confirmName = guiConfig.getString(confirmPath + ".name").replace("%teamname%", teamName);
        List<String> confirmLore = new ArrayList<>();
        for (String line : guiConfig.getStringList(confirmPath + ".lore")) {
            confirmLore.add(line.replace("%teamname%", teamName));
        }
        gui.setItem(guiConfig.getInt(confirmPath + ".slot"), createItem(confirmMat, confirmName, confirmLore));
        
        String cancelPath = "gui.confirm_create.items.cancel";
        Material cancelMat = Material.getMaterial(guiConfig.getString(cancelPath + ".material"));
        String cancelName = guiConfig.getString(cancelPath + ".name");
        List<String> cancelLore = guiConfig.getStringList(cancelPath + ".lore");
        gui.setItem(guiConfig.getInt(cancelPath + ".slot"), createItem(cancelMat, cancelName, cancelLore));
        
        p.openInventory(gui);
        plugin.getPendingJoin().put(p.getUniqueId(), teamName);
    }
    
    public void openConfirmLeaveGUI(Player p) {
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team == null) return;
        
        Inventory gui = Bukkit.createInventory(null, 27, color(guiConfig.getString("gui.confirm_leave.title")));
        playGuiSound(p, "open");
        
        String confirmPath = "gui.confirm_leave.items.confirm";
        Material confirmMat = Material.getMaterial(guiConfig.getString(confirmPath + ".material"));
        String confirmName = guiConfig.getString(confirmPath + ".name");
        List<String> confirmLore = new ArrayList<>();
        for (String line : guiConfig.getStringList(confirmPath + ".lore")) {
            confirmLore.add(line.replace("%team%", team.getName()));
        }
        gui.setItem(guiConfig.getInt(confirmPath + ".slot"), createItem(confirmMat, confirmName, confirmLore));
        
        String cancelPath = "gui.confirm_leave.items.cancel";
        Material cancelMat = Material.getMaterial(guiConfig.getString(cancelPath + ".material"));
        String cancelName = guiConfig.getString(cancelPath + ".name");
        List<String> cancelLore = guiConfig.getStringList(cancelPath + ".lore");
        gui.setItem(guiConfig.getInt(cancelPath + ".slot"), createItem(cancelMat, cancelName, cancelLore));
        
        p.openInventory(gui);
    }
    
    public void openConfirmDisbandGUI(Player p) {
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team == null) return;
        
        Inventory gui = Bukkit.createInventory(null, 27, color(guiConfig.getString("gui.confirm_disband.title")));
        playGuiSound(p, "open");
        
        String confirmPath = "gui.confirm_disband.items.confirm";
        Material confirmMat = Material.getMaterial(guiConfig.getString(confirmPath + ".material"));
        String confirmName = guiConfig.getString(confirmPath + ".name").replace("%team%", team.getName());
        List<String> confirmLore = new ArrayList<>();
        for (String line : guiConfig.getStringList(confirmPath + ".lore")) {
            confirmLore.add(line.replace("%team%", team.getName()));
        }
        gui.setItem(guiConfig.getInt(confirmPath + ".slot"), createItem(confirmMat, confirmName, confirmLore));
        
        String cancelPath = "gui.confirm_disband.items.cancel";
        Material cancelMat = Material.getMaterial(guiConfig.getString(cancelPath + ".material"));
        String cancelName = guiConfig.getString(cancelPath + ".name");
        List<String> cancelLore = guiConfig.getStringList(cancelPath + ".lore");
        gui.setItem(guiConfig.getInt(cancelPath + ".slot"), createItem(cancelMat, cancelName, cancelLore));
        
        p.openInventory(gui);
    }
    
    public void openConfirmKickGUI(Player p, Player target) {
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team == null) return;
        
        Inventory gui = Bukkit.createInventory(null, 27, color(guiConfig.getString("gui.confirm_kick.title")));
        playGuiSound(p, "open");
        
        String confirmPath = "gui.confirm_kick.items.confirm";
        Material confirmMat = Material.getMaterial(guiConfig.getString(confirmPath + ".material"));
        String confirmName = guiConfig.getString(confirmPath + ".name").replace("%player%", target.getName());
        List<String> confirmLore = new ArrayList<>();
        for (String line : guiConfig.getStringList(confirmPath + ".lore")) {
            confirmLore.add(line.replace("%player%", target.getName()));
        }
        gui.setItem(guiConfig.getInt(confirmPath + ".slot"), createItem(confirmMat, confirmName, confirmLore));
        
        String cancelPath = "gui.confirm_kick.items.cancel";
        Material cancelMat = Material.getMaterial(guiConfig.getString(cancelPath + ".material"));
        String cancelName = guiConfig.getString(cancelPath + ".name");
        List<String> cancelLore = guiConfig.getStringList(cancelPath + ".lore");
        gui.setItem(guiConfig.getInt(cancelPath + ".slot"), createItem(cancelMat, cancelName, cancelLore));
        
        String headPath = "gui.confirm_kick.items.player_head";
        String headName = guiConfig.getString(headPath + ".name").replace("%player%", target.getName());
        List<String> headLore = new ArrayList<>();
        for (String line : guiConfig.getStringList(headPath + ".lore")) {
            headLore.add(line.replace("%player%", target.getName()));
        }
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.setDisplayName(color(headName));
        List<String> coloredLore = new ArrayList<>();
        for (String line : headLore) coloredLore.add(color(line));
        meta.setLore(coloredLore);
        head.setItemMeta(meta);
        gui.setItem(guiConfig.getInt(headPath + ".slot"), head);
        
        p.openInventory(gui);
    }
    
    public void openTeamEC(Player p) {
        Team team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
        if (team == null) {
            p.sendMessage(plugin.getConfigManager().getMessage("ec.no_team"));
            return;
        }
        
        Inventory ec = Bukkit.createInventory(null, 27, color("&8&lTEAM ENDER CHEST"));
        p.openInventory(ec);
    }
                    }
