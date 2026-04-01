package me.aris.aristeam.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamData {
    public String name;
    public UUID owner;
    public Set<UUID> members = new HashSet<>();
    public Location home;
    public Inventory ec;
    public boolean pvp = false;

    public TeamData(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members.add(owner);
        this.ec = Bukkit.createInventory(null, 27, "Team EnderChest");
    }
}
