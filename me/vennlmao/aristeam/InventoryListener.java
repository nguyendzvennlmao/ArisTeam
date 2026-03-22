package me.vennlmao.aristeam;

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
            if (slot == 11) {
                Team t = plugin.getTeamManager().getTeamByPlayer(p.getUniqueId());
                if (t != null) {
                    t.setPvp(!t.isPvp());
                    plugin.getTeamManager().openTeamMenu(p);
                }
            }
            if (slot == 12) p.performCommand("team home");
            if (slot == 14) p.performCommand("team ec");
            if (slot == 15) p.sendMessage("§eDanh sách thành viên đang phát triển!");
            if (slot != -999) p.closeInventory();
        }
    }
}
