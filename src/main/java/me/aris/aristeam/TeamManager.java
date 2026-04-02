package me.aris.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamManager {
    private Aristeam plugin;
    private Map<String, Team> teams;
    private Map<UUID, String> playerTeam;
    private File teamsFile;
    private FileConfiguration teamsConfig;

    public TeamManager(Aristeam plugin) {
        this.plugin = plugin;
        this.teams = new HashMap<>();
        this.playerTeam = new HashMap<>();
        this.teamsFile = new File(plugin.getDataFolder(), "teams.yml");
        loadTeams();
    }

    public void loadTeams() {
        if (!teamsFile.exists()) {
            try {
                teamsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
        
        if (teamsConfig.contains("teams")) {
            for (String teamName : teamsConfig.getConfigurationSection("teams").getKeys(false)) {
                String path = "teams." + teamName;
                UUID owner = UUID.fromString(teamsConfig.getString(path + ".owner"));
                Team team = new Team(teamName, owner);
                
                if (teamsConfig.contains(path + ".members")) {
                    for (String uuidStr : teamsConfig.getStringList(path + ".members")) {
                        team.addMember(UUID.fromString(uuidStr));
                    }
                }
                
                if (teamsConfig.contains(path + ".admins")) {
                    for (String uuidStr : teamsConfig.getStringList(path + ".admins")) {
                        team.addAdmin(UUID.fromString(uuidStr));
                    }
                }
                
                if (teamsConfig.contains(path + ".home")) {
                    String[] homeData = teamsConfig.getString(path + ".home").split(",");
                    team.setHome(new Location(
                        Bukkit.getWorld(homeData[0]),
                        Double.parseDouble(homeData[1]),
                        Double.parseDouble(homeData[2]),
                        Double.parseDouble(homeData[3]),
                        Float.parseFloat(homeData[4]),
                        Float.parseFloat(homeData[5])
                    ));
                }
                
                team.setPvpEnabled(teamsConfig.getBoolean(path + ".pvp", false));
                teams.put(teamName, team);
                
                for (UUID uuid : team.getMembers()) {
                    playerTeam.put(uuid, teamName);
                }
            }
        }
        plugin.getLogger().info("Da tai " + teams.size() + " team!");
    }

    public void saveAllTeams() {
        for (Team team : teams.values()) {
            saveTeam(team);
        }
        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTeam(Team team) {
        String path = "teams." + team.getName();
        teamsConfig.set(path + ".owner", team.getOwner().toString());
        
        List<String> memberList = new ArrayList<>();
        for (UUID uuid : team.getMembers()) memberList.add(uuid.toString());
        teamsConfig.set(path + ".members", memberList);
        
        List<String> adminList = new ArrayList<>();
        for (UUID uuid : team.getAdmins()) adminList.add(uuid.toString());
        teamsConfig.set(path + ".admins", adminList);
        
        if (team.getHome() != null) {
            teamsConfig.set(path + ".home", team.getHome().getWorld().getName() + "," +
                team.getHome().getX() + "," + team.getHome().getY() + "," +
                team.getHome().getZ() + "," + team.getHome().getYaw() + "," + team.getHome().getPitch());
        }
        
        teamsConfig.set(path + ".pvp", team.isPvpEnabled());
    }

    public boolean createTeam(Player player, String teamName) {
        if (teams.containsKey(teamName)) return false;
        if (hasTeam(player.getUniqueId())) return false;
        
        Team team = new Team(teamName, player.getUniqueId());
        teams.put(teamName, team);
        playerTeam.put(player.getUniqueId(), teamName);
        saveTeam(team);
        return true;
    }

    public boolean deleteTeam(String teamName) {
        Team team = teams.get(teamName);
        if (team == null) return false;
        
        for (UUID uuid : team.getMembers()) {
            playerTeam.remove(uuid);
        }
        teams.remove(teamName);
        teamsConfig.set("teams." + teamName, null);
        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean addMember(String teamName, UUID playerUUID) {
        Team team = teams.get(teamName);
        if (team == null) return false;
        if (team.isMember(playerUUID)) return false;
        
        team.addMember(playerUUID);
        playerTeam.put(playerUUID, teamName);
        saveTeam(team);
        return true;
    }

    public boolean removeMember(String teamName, UUID playerUUID) {
        Team team = teams.get(teamName);
        if (team == null) return false;
        if (!team.isMember(playerUUID)) return false;
        
        team.removeMember(playerUUID);
        playerTeam.remove(playerUUID);
        
        if (team.getOwner().equals(playerUUID) && team.getMemberCount() > 0) {
            UUID newOwner = team.getMembers().iterator().next();
            team.setOwner(newOwner);
        }
        
        saveTeam(team);
        return true;
    }

    public Team getTeam(String teamName) { return teams.get(teamName); }
    public Team getPlayerTeam(UUID uuid) { 
        String teamName = playerTeam.get(uuid);
        return teamName != null ? teams.get(teamName) : null;
    }
    
    public boolean hasTeam(UUID uuid) { return playerTeam.containsKey(uuid); }
    public String getTeamName(UUID uuid) { return playerTeam.get(uuid); }
    
    public List<String> getAllTeamNames() { return new ArrayList<>(teams.keySet()); }
            }
