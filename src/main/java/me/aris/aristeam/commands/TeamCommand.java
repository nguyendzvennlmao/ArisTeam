package me.aris.aristeam.commands;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.manager.MenuManager;
import me.aris.aristeam.manager.TeamManager;
import me.aris.aristeam.utils.MessageUtils;
import me.aris.aristeam.utils.ColorUtils;
import me.aris.aristeam.utils.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class TeamCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        TeamManager tm = ArisTeams.getInstance().getTeamManager();
        if (args.length == 0) {
            if (tm.hasTeam(p)) new MenuManager().openMain(p);
            else MessageUtils.sendMessage(p, "not-in-team");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) return true;
                if (tm.hasTeam(p)) { MessageUtils.sendMessage(p, "already-in-team"); return true; }
                tm.createTeam(p, args[1]);
                MessageUtils.sendMessage(p, "team-created", "%team%", args[1]);
                p.playSound(p.getLocation(), Sound.valueOf(ArisTeams.getInstance().getConfig().getString("sounds.team-create")), 1f, 1f);
                break;
            case "sethome":
                if (!tm.hasTeam(p)) { MessageUtils.sendMessage(p, "not-in-team"); return true; }
                if (!tm.isOwner(p)) { MessageUtils.sendMessage(p, "no-permission"); return true; }
                tm.getTeam(p).home = p.getLocation();
                MessageUtils.sendMessage(p, "home-set");
                break;
            case "home":
                if (tm.hasTeam(p) && tm.getTeam(p).home != null) {
                    TeleportHandler.startTeleport(p, tm.getTeam(p).home);
                } else if (tm.hasTeam(p)) {
                    MessageUtils.sendMessage(p, "home-not-set");
                }
                break;
            case "ec":
                if (tm.hasTeam(p)) p.openInventory(tm.getTeam(p).ec);
                else MessageUtils.sendMessage(p, "not-in-team");
                break;
            case "kick":
                if (args.length < 2) return true;
                if (tm.isOwner(p)) new MenuManager().openConfirm(p, "kick", args[1]);
                break;
            case "invite":
                if (args.length < 2) return true;
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null && tm.hasTeam(p)) {
                    tm.addInvite(target.getUniqueId(), tm.getTeam(p).name);
                    MessageUtils.sendMessage(target, "invite-received", "%team%", tm.getTeam(p).name);
                    MessageUtils.sendMessage(p, "invite-send", "%player%", target.getName());
                }
                break;
            case "join":
                if (args.length < 2) return true;
                if (tm.getInvites(p.getUniqueId()).contains(args[1])) {
                    tm.joinTeam(p, args[1]);
                    MessageUtils.sendMessage(p, "player-joined");
                } else { MessageUtils.sendMessage(p, "invalid-team"); }
                break;
            case "disband":
                if (tm.isOwner(p)) new MenuManager().openConfirm(p, "disband", null);
                break;
            case "leave":
                if (tm.hasTeam(p)) new MenuManager().openConfirm(p, "leave", null);
                break;
            case "reload":
                if (p.hasPermission("aristeams.admin")) {
                    ArisTeams.getInstance().reloadPlugin();
                    MessageUtils.sendMessage(p, "reload-success");
                }
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if (a.length == 1) return Arrays.asList("create", "join", "invite", "disband", "leave", "home", "sethome", "ec", "kick", "reload");
        if (a.length == 2) {
            if (a[0].equalsIgnoreCase("join")) return ArisTeams.getInstance().getTeamManager().getInvites(((Player)s).getUniqueId());
            if (a[0].equalsIgnoreCase("kick") || a[0].equalsIgnoreCase("invite")) return null;
        }
        return new ArrayList<>();
    }
                                                               }
