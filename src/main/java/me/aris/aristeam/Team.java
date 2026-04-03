package me.aris.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public class Team {
    private String name;
    private UUID owner;
    private Set<UUID> members;
    private Set<UUID> admins;
    private Location home;
    private boolean pvpEnabled;
    private long createdAt;

    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members = new HashSet<>();
        this.admins = new HashSet<>();
        this.members.add(owner);
        this.pvpEnabled = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getName() { return name; }
    public UUID getOwner() { return owner; }
    public Set<UUID> getMembers() { return members; }
    public Set<UUID> getAdmins() { return admins; }
    public Location getHome() { return home; }
    public boolean isPvpEnabled() { return pvpEnabled; }
    
    public void setHome(Location home) { this.home = home; }
    public void setPvpEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }
    public void setOwner(UUID owner) { this.owner = owner; }
    
    public boolean isMember(UUID uuid) { return members.contains(uuid); }
    public boolean isAdmin(UUID uuid) { return admins.contains(uuid) || owner.equals(uuid); }
    
    public void addMember(UUID uuid) { members.add(uuid); }
    public void removeMember(UUID uuid) { members.remove(uuid); admins.remove(uuid); }
    
    public void addAdmin(UUID uuid) { admins.add(uuid); }
    public void removeAdmin(UUID uuid) { admins.remove(uuid); }
    
    public String getRole(UUID uuid) {
        if (owner.equals(uuid)) return "OWNER";
        if (admins.contains(uuid)) return "ADMIN";
        if (members.contains(uuid)) return "MEMBER";
        return "NONE";
    }
    
    public String getPvpStatus() {
        return pvpEnabled ? "BAT" : "TAT";
    }
    
    public List<Player> getOnlineMembers() {
        List<Player> online = new ArrayList<>();
        for (UUID uuid : members) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) online.add(p);
        }
        return online;
    }
    
    public int getOnlineCount() { return getOnlineMembers().size(); }
    public int getMemberCount() { return members.size(); }
                                                               }
