package me.aris.aristeam.listeners;

import me.aris.aristeam.ArisTeams;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        if (title.contains("Xác nhận")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            if (slot == 15) {
                p.playSound(p.getLocation(), Sound.valueOf(ArisTeams.getInstance().getConfig().getString("sounds.gui-click")), 1f, 1f);
                p.closeInventory();
            } else if (slot == 11) {
                p.closeInventory();
            }
        }
    }
  }
