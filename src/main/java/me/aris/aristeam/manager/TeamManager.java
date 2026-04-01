package me.aris.aristeam.manager;

import me.aris.aristeam.ArisTeams;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;

public class TeamManager {
    private final Map<String, TeamData> teams = new HashMap<>();
    private final Map<UUID, String> playerTeam = new HashMap<>();
    private final Map<UUID, Set<String>> invites = new HashMap<>();

    public void loadData() {
        teams.clear();
        playerTeam.clear();
        File folder = new File(ArisTeams.getInstance().getDataFolder(), "teams");
        if (!folder.exists()) folder.mkdirs();
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String name = config.getString("name");
            UUID owner = UUID.fromString(config.getString("owner"));
            TeamData team = new TeamData(name, owner);
            team.pvp = config.getBoolean("pvp", false);
            if (config.contains("home")) team.home = config.getLocation("home");
            for (String m : config.getStringList("members")) {
                UUID mUUID = UUID.fromString(m);
                team.members.add(mUUID);
                playerTeam.put(mUUID, name);
            }
            teams.put(name, team);
        }
    }

    public boolean hasTeam(Player p) {
        return playerTeam.containsKey(p.getUniqueId());
    }

    public TeamData getTeam(Player p) {
        String name = playerTeam.get(p.getUniqueId());
        return name != null ? teams.get(name) : null;
    }

    public void addInvite(UUID uuid, String teamName) {
        invites.computeIfAbsent(uuid, k -> new HashSet<>()).add(teamName);
    }

    public Set<String> getInvites(UUID uuid) {
        return invites.getOrDefault(uuid, new HashSet<>());
    }

    public void joinTeam(Player p, String teamName) {
        TeamData team = teams.get(teamName);
        if (team != null) {
            team.members.add(p.getUniqueId());
            playerTeam.put(p.getUniqueId(), teamName);
            invites.getOrDefault(p.getUniqueId(), new HashSet<>()).remove(teamName);
        }
    }

    public void leaveTeam(Player p) {
        TeamData team = getTeam(p);
        if (team != null) {
            team.members.remove(p.getUniqueId());
            playerTeam.remove(p.getUniqueId());
        }
    }

    public void kickMember(TeamData team, UUID target) {
        team.members.remove(target);
        playerTeam.remove(target);
    }

    public boolean isOwner(Player p) {
        TeamData team = getTeam(p);
        return team != null && team.owner.equals(p.getUniqueId());
    }
                                        }
