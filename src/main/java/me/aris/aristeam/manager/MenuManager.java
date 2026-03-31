package me.aris.aristeam.manager;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class MenuManager {
    public void openConfirm(Player p, String type, String target) {
        Inventory inv = Bukkit.createInventory(null, 27, "Xác nhận " + type);
        inv.setItem(11, createItem(Material.LIME_DYE, "&aXác nhận"));
        inv.setItem(15, createItem(Material.ROSE_BUSH, "&cHủy bỏ"));
        p.setMetadata("aris_gui_type", new FixedMetadataValue(ArisTeams.getInstance(), type));
        if (target != null) p.setMetadata("aris_gui_target", new FixedMetadataValue(ArisTeams.getInstance(), target));
        p.openInventory(inv);
    }

    public void openMain(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Team Menu");
        inv.setItem(13, createItem(Material.PAPER, "&eTeam Home"));
        inv.setItem(10, createItem(Material.CHEST, "&6EnderChest"));
        inv.setItem(16, createItem(Material.BARRIER, "&cGiải tán"));
        p.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack i = new ItemStack(mat);
        ItemMeta m = i.getItemMeta();
        if (m != null) {
            m.setDisplayName(ColorUtils.colorize(name));
            i.setItemMeta(m);
        }
        return i;
    }
                }
