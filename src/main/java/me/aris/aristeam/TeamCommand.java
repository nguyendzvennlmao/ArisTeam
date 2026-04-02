package me.aris.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamCommand implements CommandExecutor {
    private Aristeam plugin;
    private java.util.Map<Player, Integer> teleportTasks = new java.util.HashMap<>();

    public TeamCommand(Aristeam plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Chi player moi co the dung lenh nay!");
            return true;
        }
        
        Player p = (Player) sender;
        
        if (args.length == 0) {
            plugin.getTeamGUI().openMainGUI(p);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    p.sendMessage(plugin.getConfigManager().getMessage("create.usage"));
                    return true;
                }
                String teamName = args[1];
                if (plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("create.already_in_team"));
                    return true;
                }
                if (plugin.getTeamManager().getTeam(teamName) != null) {
                    p.sendMessage(plugin.getConfigManager().getMessage("create.name_exists"));
                    return true;
                }
                plugin.getTeamGUI().openConfirmCreateGUI(p, teamName);
                break;
                
            case "join":
                if (args.length < 2) {
                    p.sendMessage(plugin.getConfigManager().getMessage("join.usage"));
                    return true;
                }
                String inviteTeam = args[1];
                Team team = plugin.getTeamManager().getTeam(inviteTeam);
                if (team == null) {
                    p.sendMessage(plugin.getConfigManager().getMessage("join.team_not_found"));
                    return true;
                }
                if (plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("join.already_in_team"));
                    return true;
                }
                String pending = plugin.getPendingInvites().get(p.getUniqueId());
                if (pending == null || !pending.equals(inviteTeam)) {
                    p.sendMessage(plugin.getConfigManager().getMessage("join.no_invite"));
                    return true;
                }
                plugin.getTeamManager().addMember(inviteTeam, p.getUniqueId());
                plugin.getPendingInvites().remove(p.getUniqueId());
                p.sendMessage(plugin.getConfigManager().getMessage("join.success").replace("%team%", inviteTeam));
                for (Player member : team.getOnlineMembers()) {
                    member.sendMessage(plugin.getConfigManager().getMessage("join.announce").replace("%player%", p.getName()).replace("%team%", inviteTeam));
                }
                break;
                
            case "deny":
                String pendingDeny = plugin.getPendingInvites().get(p.getUniqueId());
                if (pendingDeny == null) {
                    p.sendMessage(plugin.getConfigManager().getMessage("deny.no_invite"));
                    return true;
                }
                plugin.getPendingInvites().remove(p.getUniqueId());
                p.sendMessage(plugin.getConfigManager().getMessage("deny.success").replace("%team%", pendingDeny));
                break;
                
            case "ec":
                plugin.getTeamGUI().openMainGUI(p);
                break;
                
            case "leave":
                if (!plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("leave.no_team"));
                    return true;
                }
                plugin.getTeamGUI().openConfirmLeaveGUI(p);
                break;
                
            case "home":
                if (!plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("home.no_team"));
                    return true;
                }
                Team pTeam = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                if (pTeam.getHome() == null) {
                    p.sendMessage(plugin.getConfigManager().getMessage("home.not_set"));
                    return true;
                }
                startTeleport(p, pTeam.getHome());
                break;
                
            case "sethome":
                if (!plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("sethome.no_team"));
                    return true;
                }
                Team homeTeam = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                if (!homeTeam.isAdmin(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("sethome.no_permission"));
                    return true;
                }
                homeTeam.setHome(p.getLocation());
                plugin.getTeamManager().saveTeam(homeTeam);
                p.sendMessage(plugin.getConfigManager().getMessage("sethome.success"));
                break;
                
            case "chat":
            case "c":
                if (!plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("chat.no_team"));
                    return true;
                }
                if (args.length < 2) {
                    p.sendMessage(plugin.getConfigManager().getMessage("chat.usage"));
                    return true;
                }
                StringBuilder msg = new StringBuilder();
                for (int i = 1; i < args.length; i++) msg.append(args[i]).append(" ");
                Team chatTeam = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                String format = plugin.getConfigManager().getMessage("chat.format").replace("%player%", p.getName()).replace("%message%", msg.toString());
                for (Player member : chatTeam.getOnlineMembers()) {
                    member.sendMessage(format);
                }
                break;
                
            case "kick":
                if (!plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("kick.no_team"));
                    return true;
                }
                if (args.length < 2) {
                    p.sendMessage(plugin.getConfigManager().getMessage("kick.usage"));
                    return true;
                }
                Team kickTeam = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                if (!kickTeam.isAdmin(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("kick.no_permission"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !kickTeam.isMember(target.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("kick.not_found"));
                    return true;
                }
                if (target.getUniqueId().equals(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("kick.cannot_kick_yourself"));
                    return true;
                }
                plugin.getTeamGUI().openConfirmKickGUI(p, target);
                break;
                
            case "disband":
                if (!plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("disband.no_team"));
                    return true;
                }
                Team disbandTeam = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                if (!disbandTeam.getOwner().equals(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("disband.not_owner"));
                    return true;
                }
                plugin.getTeamGUI().openConfirmDisbandGUI(p);
                break;
                
            case "pvp":
                if (!plugin.getTeamManager().hasTeam(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("pvp.no_team"));
                    return true;
                }
                Team pvpTeam = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
                if (!pvpTeam.isAdmin(p.getUniqueId())) {
                    p.sendMessage(plugin.getConfigManager().getMessage("pvp.no_permission"));
                    return true;
                }
                boolean newState = !pvpTeam.isPvpEnabled();
                pvpTeam.setPvpEnabled(newState);
                plugin.getTeamManager().saveTeam(pvpTeam);
                String state = newState ? plugin.getConfigManager().getPvpStatusText(true) : plugin.getConfigManager().getPvpStatusText(false);
                p.sendMessage(plugin.getConfigManager().getMessage("pvp.success").replace("%state%", state));
                for (Player member : pvpTeam.getOnlineMembers()) {
                    member.sendMessage(plugin.getConfigManager().getMessage("pvp.announce").replace("%player%", p.getName()).replace("%state%", state));
                }
                break;
                
            case "reload":
                if (!p.hasPermission("aristeam.reload")) {
                    p.sendMessage(plugin.getConfigManager().getMessage("reload.no_permission"));
                    return true;
                }
                plugin.reloadConfig();
                plugin.getConfigManager().reloadMessages();
                plugin.getConfigManager().reloadGUI();
                p.sendMessage(plugin.getConfigManager().getMessage("reload.success"));
                break;
                
            default:
                p.sendMessage(plugin.getConfigManager().getMessage("unknown_command"));
                break;
        }
        return true;
    }
    
    private void startTeleport(Player p, org.bukkit.Location loc) {
        if (plugin.getTeleportCooldown().containsKey(p.getUniqueId())) {
            long remaining = (plugin.getTeleportCooldown().get(p.getUniqueId()) + 5000) - System.currentTimeMillis();
            if (remaining > 0) {
                p.sendMessage(plugin.getConfigManager().getMessage("teleport.cooldown").replace("%time%", String.valueOf(remaining / 1000)));
                return;
            }
        }
        
        org.bukkit.Location startLoc = p.getLocation().clone();
        plugin.getTeleportCooldown().put(p.getUniqueId(), System.currentTimeMillis());
        
        int[] count = {5};
        p.sendMessage(plugin.getConfigManager().getMessage("teleport.start").replace("%time%", "5"));
        
        if (plugin.getConfig().getBoolean("settings.sounds.enabled")) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }
        
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) {
                    cancel();
                    return;
                }
                
                if (p.getLocation().distance(startLoc) > plugin.getConfig().getDouble("settings.teleport.move_tolerance")) {
                    p.sendMessage(plugin.getConfigManager().getMessage("teleport.cancelled"));
                    if (plugin.getConfig().getBoolean("settings.sounds.enabled")) {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    }
                    plugin.getTeleportCooldown().remove(p.getUniqueId());
                    cancel();
                    return;
                }
                
                if (count[0] > 0) {
                    p.sendMessage(plugin.getConfigManager().getMessage("teleport.countdown").replace("%time%", String.valueOf(count[0])));
                    if (plugin.getConfig().getBoolean("settings.sounds.enabled")) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
                    }
                    count[0]--;
                } else {
                    p.teleport(loc);
                    p.sendMessage(plugin.getConfigManager().getMessage("teleport.success"));
                    if (plugin.getConfig().getBoolean("settings.sounds.enabled")) {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    }
                    cancel();
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
        teleportTasks.put(p, task.getTaskId());
    }
                             }
