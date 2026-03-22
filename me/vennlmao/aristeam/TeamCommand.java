package me.vennlmao.aristeam;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class TeamCommand implements CommandExecutor, TabCompleter {
    private final ArisTeam plugin;
    public TeamCommand(ArisTeam plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        Team team = plugin.getTeamManager().getTeamByPlayer(p.getUniqueId());

        if (args.length == 0) {
            if (team == null) {
                p.sendMessage(" ");
                p.sendMessage(plugin.getMsgManager().translate("&#facc15&lARIS TEAM &7- &fBạn chưa có đội!"));
                p.sendMessage(plugin.getMsgManager().translate(" &8• &f/team create <tên> &7- Tạo đội mới"));
                p.sendMessage(plugin.getMsgManager().translate(" &8• &f/team join <tên> &7- Vào đội được mời"));
                p.sendMessage(" ");
            } else {
                p.sendMessage(" ");
                p.sendMessage(plugin.getMsgManager().translate("&#facc15&lARIS TEAM &7- &fĐội: &e" + team.getName()));
                p.sendMessage(plugin.getMsgManager().translate(" &8• &f/team home &7- Về căn cứ"));
                p.sendMessage(plugin.getMsgManager().translate(" &8• &f/team ec &7- Kho đồ chung"));
                p.sendMessage(plugin.getMsgManager().translate(" &8• &f/team chat <nội dung> &7- Chat đội"));
                p.sendMessage(plugin.getMsgManager().translate(" &8• &f/team leave &7- Rời khỏi đội"));
                p.sendMessage(" ");
                plugin.getTeamManager().openTeamMenu(p);
            }
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create" -> { if (args.length > 1) plugin.getTeamManager().createTeam(p, args[1]); }
            case "join" -> { if (args.length > 1) plugin.getTeamManager().joinTeam(p, args[1]); }
            case "home" -> {
                if (team != null && team.getHome() != null) plugin.getTeleportManager().startTeleport(p, team.getHome());
                else plugin.getMsgManager().send(p, "no-home-set", null);
            }
            case "sethome" -> {
                if (team != null && team.getOwner().equals(p.getUniqueId())) {
                    team.setHome(p.getLocation());
                    p.sendMessage("§aĐã đặt căn cứ tại đây!");
                }
            }
            case "ec" -> { if (team != null) p.openInventory(team.getInventory()); }
            case "chat" -> {
                if (team != null && args.length > 1) {
                    String m = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    String f = plugin.getMsgManager().translate("&#facc15[TEAM] " + p.getName() + ": &f" + m);
                    team.getMembers().forEach(u -> { if (Bukkit.getPlayer(u) != null) Bukkit.getPlayer(u).sendMessage(f); });
                }
            }
            case "leave" -> { if (team != null) plugin.getTeamManager().leaveTeam(p); }
            case "invite" -> {
                if (team != null && args.length > 1) {
                    Player t = Bukkit.getPlayer(args[1]);
                    if (t != null) plugin.getTeamManager().sendInvite(p, t);
                    else plugin.getMsgManager().send(p, "player-offline", Map.of("%player%", args[1]));
                }
            }
            case "disband" -> { if (team != null) plugin.getTeamManager().disbandTeam(p); }
            case "gui" -> { if (team != null) plugin.getTeamManager().openTeamMenu(p); }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "join", "home", "sethome", "ec", "chat", "leave", "invite", "disband", "gui");
        return null;
    }
                              }
