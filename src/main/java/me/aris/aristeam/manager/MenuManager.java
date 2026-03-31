package me.aris.aristeam.manager;

import me.aris.aristeam.ArisTeams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class MenuManager {
    public void openMain(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Team Menu");
        inv.setItem(13, new ItemStack(Material.RED_BED)); [cite: 5]
        p.openInventory(inv);
    }

    public void openConfirm(Player p, String type, String target) {
        Inventory inv = Bukkit.createInventory(null, 27, "Xác nhận " + type);
        inv.setItem(11, new ItemStack(Material.GREEN_WOOL));
        inv.setItem(15, new ItemStack(Material.RED_WOOL));
        p.setMetadata("aris_gui_type", new FixedMetadataValue(ArisTeams.getInstance(), type));
        if (target != null) p.setMetadata("aris_gui_target", new FixedMetadataValue(ArisTeams.getInstance(), target));
        p.openInventory(inv);
    }
}
