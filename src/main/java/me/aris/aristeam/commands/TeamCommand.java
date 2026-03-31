package me.aris.aristeam.commands;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.manager.MenuManager;
import me.aris.aristeam.manager.TeamManager;
import me.aris.aristeam.utils.MessageUtils;
import org.bukkit.Bukkit;
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
            case "reload":
                if (p.hasPermission("aristeams.admin")) {
                    ArisTeams.getInstance().reloadPlugin();
                    MessageUtils.sendMessage(p, "reload-success");
                }
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
                if (args.length < 2) { MessageUtils.sendMessage(p, "need-team-name"); return true; }
                String tName = args[1];
                if (tm.getInvites(p.getUniqueId()).contains(tName)) {
                    tm.joinTeam(p, tName);
                    MessageUtils.sendMessage(p, "player-joined");
                } else { MessageUtils.sendMessage(p, "invalid-team"); }
                break;
            case "disband":
                if (tm.isOwner(p)) new MenuManager().openConfirm(p, "disband", null);
                break;
            case "kick":
                if (args.length > 1 && tm.isOwner(p)) new MenuManager().openConfirm(p, "kick", args[1]);
                break;
            case "leave":
                if (tm.hasTeam(p)) new MenuManager().openConfirm(p, "leave", null);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if (a.length == 1) return Arrays.asList("create", "join", "invite", "disband", "leave", "kick", "home", "reload");
        if (a.length == 2 && a[0].equalsIgnoreCase("join")) return ArisTeams.getInstance().getTeamManager().getInvites(((Player)s).getUniqueId());
        return null;
    }
                  }
