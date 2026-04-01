package me.aris.aristeam.manager;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class MenuManager {
    public void openMain(Player p) {
        ConfigurationSection config = ArisTeams.getInstance().getConfig().getConfigurationSection("main-gui");
        TeamData team = ArisTeams.getInstance().getTeamManager().getTeam(p);
        Inventory inv = Bukkit.createInventory(null, config.getInt("rows") * 9, ColorUtils.colorize(config.getString("title")));
        ConfigurationSection items = config.getConfigurationSection("items");
        for (String key : items.getKeys(false)) {
            String matName = items.getString(key + ".material");
            Material mat = Material.matchMaterial(matName != null ? matName : "BARRIER");
            String name = items.getString(key + ".name");
            if (key.equalsIgnoreCase("pvp") && team != null) {
                name = name.replace("%status%", team.pvp ? "&aBật" : "&cTắt");
            }
            inv.setItem(items.getInt(key + ".slot"), createItem(mat, name));
        }
        p.openInventory(inv);
    }

    public void openConfirm(Player p, String type, String target) {
        ConfigurationSection config = ArisTeams.getInstance().getConfig().getConfigurationSection(type + "-gui");
        String title = config.getString("title").replace("%player%", target != null ? target : "");
        Inventory inv = Bukkit.createInventory(null, config.getInt("rows") * 9, ColorUtils.colorize(title));
        ConfigurationSection items = config.getConfigurationSection("items");
        for (String key : items.getKeys(false)) {
            String matName = items.getString(key + ".material");
            Material mat = Material.matchMaterial(matName != null ? matName : "BARRIER");
            String name = items.getString(key + ".name").replace("%player%", target != null ? target : "");
            inv.setItem(items.getInt(key + ".slot"), createItem(mat, name));
        }
        p.setMetadata("aris_gui_type", new FixedMetadataValue(ArisTeams.getInstance(), type));
        if (target != null) p.setMetadata("aris_gui_target", new FixedMetadataValue(ArisTeams.getInstance(), target));
        p.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack i = new ItemStack(mat == null ? Material.BARRIER : mat);
        ItemMeta m = i.getItemMeta();
        if (m != null) {
            m.setDisplayName(ColorUtils.colorize(name));
            i.setItemMeta(m);
        }
        return i;
    }
        }
