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
import java.io.File;

public class MenuManager {
    private final FileConfiguration gui;

    public MenuManager() {
        this.gui = YamlConfiguration.loadConfiguration(new File(ArisTeams.getInstance().getDataFolder(), "teamgui.yml"));
    }

    public void openConfirm(Player p, String type, String target) {
        String path = type + "-gui";
        String title = ColorUtils.colorize(gui.getString(path + ".title").replace("%player%", target != null ? target : ""));
        Inventory inv = Bukkit.createInventory(null, gui.getInt(path + ".rows", 3) * 9, title);

        inv.setItem(gui.getInt(path + ".items.confirm.slot"), createItem(path + ".items.confirm", null));
        inv.setItem(gui.getInt(path + ".items.cancel.slot"), createItem(path + ".items.cancel", null));
        if (gui.contains(path + ".items.info")) {
            inv.setItem(gui.getInt(path + ".items.info.slot"), createItem(path + ".items.info", target));
        }
        p.openInventory(inv);
    }

    public void openMain(Player p) {
        TeamManager.TeamData td = ArisTeams.getInstance().getTeamManager().getTeam(p);
        Inventory inv = Bukkit.createInventory(null, 27, ColorUtils.colorize(gui.getString("main-gui.title")));
        inv.setItem(gui.getInt("main-gui.items.members.slot"), createItem("main-gui.items.members", null));
        inv.setItem(gui.getInt("main-gui.items.enderchest.slot"), createItem("main-gui.items.enderchest", null));
        inv.setItem(gui.getInt("main-gui.items.pvp.slot"), createItem("main-gui.items.pvp", td.pvp ? "&aBật" : "&cTắt"));
        inv.setItem(gui.getInt("main-gui.items.home.slot"), createItem("main-gui.items.home", null));
        p.openInventory(inv);
    }

    private ItemStack createItem(String path, String rep) {
        ItemStack i = new ItemStack(Material.valueOf(gui.getString(path + ".material")));
        ItemMeta m = i.getItemMeta();
        String n = gui.getString(path + ".name");
        if (rep != null) n = n.replace("%player%", rep).replace("%status%", rep);
        m.setDisplayName(ColorUtils.colorize(n));
        i.setItemMeta(m);
        return i;
    }
                                   }
