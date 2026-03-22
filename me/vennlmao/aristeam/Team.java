package me.vennlmao.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import java.util.*;

public class Team {
    private String name;
    private UUID owner;
    private Set<UUID> members;
    private Location home;
    private Inventory inventory;
    private boolean pvp = false;

    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner);
        this.inventory = Bukkit.createInventory(null, 54, "§8ᴇɴᴅᴇʀᴄʜᴇsᴛ - " + name);
    }
    public String getName() { return name; }
    public UUID getOwner() { return owner; }
    public Set<UUID> getMembers() { return members; }
    public Location getHome() { return home; }
    public void setHome(Location home) { this.home = home; }
    public Inventory getInventory() { return inventory; }
    public boolean isPvp() { return pvp; }
    public void setPvp(boolean pvp) { this.pvp = pvp; }
}
