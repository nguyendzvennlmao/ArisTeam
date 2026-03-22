package me.vennlmao.aristeam;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {
    private final ArisTeam plugin;
    public InventoryListener(ArisTeam plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("ᴛᴇᴀᴍ-ᴍᴀɪɴ")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();
            Team t = plugin.getTeamManager().getTeamByPlayer(p.getUniqueId());
            if (t == null) return;

            if (slot == 11) {
                t.setPvp(!t.isPvp());
                p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.confirm")), 1f, 1f);
                plugin.getTeamManager().openMenu(p);
            } else if (slot == 12) {
                p.closeInventory();
                p.performCommand("team home");
            } else if (slot == 14) {
                p.closeInventory();
                p.openInventory(t.getInventory());
            }
        }
    }
                            }
