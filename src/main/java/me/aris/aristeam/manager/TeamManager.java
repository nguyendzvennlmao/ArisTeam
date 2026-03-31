package me.aris.aristeam.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.*;

public class TeamManager {
    private final Map<String, TeamData> teams = new HashMap<>();
    private final Map<UUID, String> playerToTeam = new HashMap<>();
    private final Map<UUID, List<String>> pendingInvites = new HashMap<>();

    public static class TeamData {
        public String name;
        public UUID owner;
        public List<UUID> members = new ArrayList<>();
        public Inventory ec;
        public Location home;
        public boolean pvp = false;

        public TeamData(String name, UUID owner) {
            this.name = name;
            this.owner = owner;
            this.members.add(owner);
            this.ec = Bukkit.createInventory(null, 54, "Team Chest: " + name);
        }
    }

    public void createTeam(Player p, String name) {
        TeamData td = new TeamData(name, p.getUniqueId());
        teams.put(name, td);
        playerToTeam.put(p.getUniqueId(), name);
    }

    public void joinTeam(Player p, String teamName) {
        TeamData td = teams.get(teamName);
        if (td != null) {
            td.members.add(p.getUniqueId());
            playerToTeam.put(p.getUniqueId(), teamName);
            getInvites(p.getUniqueId()).remove(teamName);
        }
    }

    public TeamData getTeam(Player p) {
        String n = playerToTeam.get(p.getUniqueId());
        return n != null ? teams.get(n) : null;
    }

    public boolean hasTeam(Player p) { return playerToTeam.containsKey(p.getUniqueId()); }

    public boolean isOwner(Player p) {
        TeamData td = getTeam(p);
        return td != null && td.owner.equals(p.getUniqueId());
    }

    public void addInvite(UUID u, String team) {
        pendingInvites.computeIfAbsent(u, k -> new ArrayList<>()).add(team);
    }

    public List<String> getInvites(UUID u) {
        return pendingInvites.getOrDefault(u, new ArrayList<>());
    }

    public void loadData() {}
    public void saveData() {}
                             }
