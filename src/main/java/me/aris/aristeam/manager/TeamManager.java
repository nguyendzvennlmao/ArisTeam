package me.aris.aristeam.manager;

import org.bukkit.entity.Player;
import java.util.*;

public class TeamManager {
    private final Map<String, TeamData> teams = new HashMap<>();
    private final Map<UUID, List<String>> invites = new HashMap<>();

    public void addInvite(UUID uuid, String teamName) {
        invites.computeIfAbsent(uuid, k -> new ArrayList<>()).add(teamName);
    }

    public List<String> getInvites(UUID uuid) {
        return invites.getOrDefault(uuid, new ArrayList<>());
    }

    public void joinTeam(Player p, String teamName) {
        TeamData team = teams.get(teamName);
        if (team != null) {
            team.members.add(p.getUniqueId());
            if (invites.containsKey(p.getUniqueId())) {
                invites.get(p.getUniqueId()).remove(teamName);
            }
        }
    }

    public void createTeam(Player owner, String name) {
        teams.put(name, new TeamData(name, owner.getUniqueId()));
    }

    public void disbandTeam(String teamName) {
        teams.remove(teamName);
    }

    public void kickMember(TeamData team, UUID targetUUID) {
        if (team != null) {
            team.members.remove(targetUUID);
        }
    }

    public void leaveTeam(Player p) {
        TeamData team = getTeam(p);
        if (team != null) team.members.remove(p.getUniqueId());
    }

    public TeamData getTeam(Player p) {
        return teams.values().stream().filter(t -> t.members.contains(p.getUniqueId())).findFirst().orElse(null);
    }

    public boolean hasTeam(Player p) { return getTeam(p) != null; }

    public boolean isOwner(Player p) {
        TeamData team = getTeam(p);
        return team != null && team.owner.equals(p.getUniqueId());
    }
        }
