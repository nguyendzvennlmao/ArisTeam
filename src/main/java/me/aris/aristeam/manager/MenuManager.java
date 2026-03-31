package me.aris.aristeam.manager;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import java.io.File;

public class MenuManager {
    private final FileConfiguration gui;

    public MenuManager() {
        this.gui = YamlConfiguration.loadConfiguration(new File(ArisTeams.getInstance().getDataFolder(), "teamgui.yml"));
    }

    public void openConfirm(Player p, String type, String target) {
        String path = type + "-gui";
        String title = ColorUtils.colorize(gui.getString(path + ".title", "Xác nhận"));
        Inventory inv = Bukkit.createInventory(null, 27, title);
        inv.setItem(11, createItem(path + ".items.confirm", null));
        inv.setItem(15, createItem(path + ".items.cancel", null));
        p.setMetadata("aris_gui_type", new FixedMetadataValue(ArisTeams.getInstance(), type));
        if (target != null) p.setMetadata("aris_gui_target", new FixedMetadataValue(ArisTeams.getInstance(), target));
        p.openInventory(inv);
    }

    public void openMain(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, ColorUtils.colorize(gui.getString("main-gui.title", "Team Menu")));
        inv.setItem(10, createItem("main-gui.items.members", null));
        inv.setItem(13, createItem("main-gui.items.home", null));
        inv.setItem(16, createItem("main-gui.items.disband", null));
        p.openInventory(inv);
    }

    private ItemStack createItem(String path, String rep) {
        String matName = gui.getString(path + ".material", "STONE").toUpperCase();
        if (matName.equals("BED")) matName = "RED_BED";
        Material mat;
        try {
            mat = Material.valueOf(matName);
        } catch (Exception e) {
            mat = Material.BARRIER;
        }
        ItemStack i = new ItemStack(mat);
        ItemMeta m = i.getItemMeta();
        if (m != null) {
            String name = gui.getString(path + ".name", "Item");
            if (rep != null) name = name.replace("%player%", rep);
            m.setDisplayName(ColorUtils.colorize(name));
            i.setItemMeta(m);
        }
        return i;
    }
                }
