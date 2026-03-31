package me.aris.aristeam.utils;

import me.aris.aristeam.ArisTeams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;

public class MessageUtils {
    public static void sendMessage(Player p, String path, String... rep) {
        File f = new File(ArisTeams.getInstance().getDataFolder(), "messages.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        String msg = cfg.getString(path, "");
        for (int i = 0; i < rep.length; i += 2) msg = msg.replace(rep[i], rep[i+1]);
        p.sendMessage(ColorUtils.colorize(cfg.getString("prefix") + msg));
    }

    public static void sendActionBar(Player p, String path, String... rep) {
        File f = new File(ArisTeams.getInstance().getDataFolder(), "messages.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        String msg = cfg.getString(path, "");
        for (int i = 0; i < rep.length; i += 2) msg = msg.replace(rep[i], rep[i+1]);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorUtils.colorize(msg)));
    }
}
