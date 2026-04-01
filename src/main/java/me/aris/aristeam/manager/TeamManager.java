package me.aris.aristeam.manager;

import me.aris.aristeam.ArisTeams;
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
            for (String m : config.getStringList("members")) {
                UUID mUUID = UUID.fromString(m);
                team.members.add(mUUID);
                playerTeam.put(mUUID, name);
            }
            teams.put(name, team);
        }
    }

    public void saveData() {
        File folder = new File(ArisTeams.getInstance().getDataFolder(), "teams");
        if (!folder.exists()) folder.mkdirs();
        for (TeamData team : teams.values()) {
            File file = new File(folder, team.name + ".yml");
            FileConfiguration config = new YamlConfiguration();
            config.set("name", team.name);
            config.set("owner", team.owner.toString());
            config.set("pvp", team.pvp);
            List<String> mList = new ArrayList<>();
            for (UUID uuid : team.members) mList.add(uuid.toString());
            config.set("members", mList);
            try { config.save(file); } catch (Exception ignored) {}
        }
    }

    public void createTeam(Player p, String name) {
        TeamData team = new TeamData(name, p.getUniqueId());
        teams.put(name, team);
        playerTeam.put(p.getUniqueId(), name);
    }

    public void joinTeam(Player p, String teamName) {
        TeamData team = teams.get(teamName);
        if (team != null) {
            team.members.add(p.getUniqueId());
            playerTeam.put(p.getUniqueId(), teamName);
            if (invites.containsKey(p.getUniqueId())) {
                invites.get(p.getUniqueId()).remove(teamName);
            }
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

    public void disbandTeam(String name) {
        TeamData team = teams.remove(name);
        if (team != null) {
            for (UUID uuid : team.members) playerTeam.remove(uuid);
            new File(ArisTeams.getInstance().getDataFolder(), "teams/" + name + ".yml").delete();
        }
    }

    public TeamData getTeam(Player p) {
        String name = playerTeam.get(p.getUniqueId());
        return name != null ? teams.get(name) : null;
    }

    public boolean hasTeam(Player p) {
        return playerTeam.containsKey(p.getUniqueId());
    }

    public boolean isOwner(Player p) {
        TeamData team = getTeam(p);
        return team != null && team.owner.equals(p.getUniqueId());
    }

    public void addInvite(UUID uuid, String teamName) {
        invites.computeIfAbsent(uuid, k -> new HashSet<>()).add(teamName);
    }

    public List<String> getInvites(UUID uuid) {
        return new ArrayList<>(invites.getOrDefault(uuid, new HashSet<>()));
    }
            }
