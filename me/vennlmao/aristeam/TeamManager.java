package me.vennlmao.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    private final Map<UUID, String> pendingInvites = new HashMap<>();

    public TeamManager(ArisTeam plugin) { this.plugin = plugin; }

    public Team getTeamByPlayer(UUID uuid) {
        String n = playerTeamMap.get(uuid);
        return (n != null) ? teams.get(n) : null;
    }

    public void createTeam(Player p, String name) {
        if (playerTeamMap.containsKey(p.getUniqueId())) {
            plugin.getMsgManager().send(p, "player-already-in-team", Map.of("%player%", p.getName()));
            return;
        }
        Team team = new Team(name, p.getUniqueId());
        teams.put(name, team);
        playerTeamMap.put(p.getUniqueId(), name);
        plugin.getMsgManager().send(p, "team-created", Map.of("%nameteam%", name));
    }

    public void openTeamMenu(Player p) {
        Team team = getTeamByPlayer(p.getUniqueId());
        if (team == null) return;
        FileConfiguration gui = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "gui.yml"));
        Inventory inv = Bukkit.createInventory(null, gui.getInt("team-menu.rows") * 9, plugin.getMsgManager().translate(gui.getString("team-menu.title")));
        inv.setItem(gui.getInt("team-menu.slots.pvp.slot"), createGuiItem(gui, "team-menu.slots.pvp", team));
        inv.setItem(gui.getInt("team-menu.slots.home.slot"), createGuiItem(gui, "team-menu.slots.home", team));
        inv.setItem(gui.getInt("team-menu.slots.enderchest.slot"), createGuiItem(gui, "team-menu.slots.enderchest", team));
        inv.setItem(gui.getInt("team-menu.slots.members.slot"), createGuiItem(gui, "team-menu.slots.members", team));
        p.openInventory(inv);
    }

    private ItemStack createGuiItem(FileConfiguration cfg, String path, Team team) {
        ItemStack i = new ItemStack(Material.valueOf(cfg.getString(path + ".material")));
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(plugin.getMsgManager().translate(cfg.getString(path + ".display_name")));
        List<String> lore = new ArrayList<>();
        for (String s : cfg.getStringList(path + ".lore")) lore.add(plugin.getMsgManager().translate(s.replace("%status%", team.isPvp() ? "&aBật" : "&cTắt")));
        m.setLore(lore);
        i.setItemMeta(m);
        return i;
    }

    public void sendInvite(Player s, Player t) {
        Team team = getTeamByPlayer(s.getUniqueId());
        if (team == null) return;
        pendingInvites.put(t.getUniqueId(), team.getName());
        plugin.getMsgManager().send(s, "invite-sent", Map.of("%player%", t.getName()));
        plugin.getMsgManager().send(t, "invite-received", Map.of("%player%", s.getName(), "%nameteam%", team.getName()));
    }

    public void joinTeam(Player p, String name) {
        if (!pendingInvites.getOrDefault(p.getUniqueId(), "").equalsIgnoreCase(name)) return;
        Team team = teams.get(name);
        if (team != null) {
            team.getMembers().add(p.getUniqueId());
            playerTeamMap.put(p.getUniqueId(), name);
            pendingInvites.remove(p.getUniqueId());
            p.sendMessage("§aGia nhập thành công!");
        }
    }

    public void leaveTeam(Player p) {
        Team team = getTeamByPlayer(p.getUniqueId());
        if (team == null || team.getOwner().equals(p.getUniqueId())) return;
        team.getMembers().remove(p.getUniqueId());
        playerTeamMap.remove(p.getUniqueId());
        p.sendMessage("§eBạn đã rời đội.");
    }

    public void disbandTeam(Player o) {
        Team team = getTeamByPlayer(o.getUniqueId());
        if (team == null || !team.getOwner().equals(o.getUniqueId())) return;
        team.getMembers().forEach(playerTeamMap::remove);
        teams.remove(team.getName());
        o.sendMessage("§cĐã giải tán đội.");
    }
          }
