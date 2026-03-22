package me.vennlmao.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.File;
import java.util.*;

public class TeamManager {
    private final ArisTeam plugin;
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<UUID, String> playerTeamMap = new HashMap<>();

    public TeamManager(ArisTeam plugin) { this.plugin = plugin; }

    public Team getTeamByPlayer(UUID uuid) { return teams.get(playerTeamMap.get(uuid)); }

    public void openMenu(Player p) {
        Team team = getTeamByPlayer(p.getUniqueId());
        if (team == null) return;

        FileConfiguration gui = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "gui.yml"));
        Inventory inv = Bukkit.createInventory(null, gui.getInt("team-menu.rows") * 9, plugin.getMsgManager().translate(gui.getString("team-menu.title")));

        ConfigurationSection slots = gui.getConfigurationSection("team-menu.slots");
        if (slots != null) {
            for (String key : slots.getKeys(false)) {
                String path = "team-menu.slots." + key;
                ItemStack item = new ItemStack(Material.valueOf(gui.getString(path + ".material")));
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(plugin.getMsgManager().translate(gui.getString(path + ".display_name")));
                List<String> lore = new ArrayList<>();
                for (String line : gui.getStringList(path + ".lore")) {
                    lore.add(plugin.getMsgManager().translate(line.replace("%status%", team.isPvp() ? "&aBật" : "&cTắt")));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(gui.getInt(path + ".slot"), item);
            }
        }
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.open-menu")), 1f, 1f);
    }

    public void createTeam(Player p, String name) {
        if (playerTeamMap.containsKey(p.getUniqueId())) return;
        teams.put(name, new Team(name, p.getUniqueId()));
        playerTeamMap.put(p.getUniqueId(), name);
        plugin.getMsgManager().send(p, "team-created", Map.of("%nameteam%", name));
        p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.team-create")), 1f, 1f);
    }
}
