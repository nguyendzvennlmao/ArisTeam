package me.vennlmao.aristeam;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
    private final ArisTeam plugin;
    private FileConfiguration cfg;
    public MessageManager(ArisTeam plugin) { this.plugin = plugin; reload(); }
    public void reload() { cfg = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")); }
    public String translate(String s) {
        if (s == null) return "";
        Pattern p = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher m = p.matcher(s);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String h = m.group(1);
            StringBuilder r = new StringBuilder("§x");
            for (char c : h.toCharArray()) r.append('§').append(c);
            m.appendReplacement(sb, r.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', m.appendTail(sb).toString());
    }
    public void send(Player p, String path, Map<String, String> h) {
        String t = cfg.getString(path + ".text", "");
        if (h != null) for (Map.Entry<String, String> e : h.entrySet()) t = t.replace(e.getKey(), e.getValue());
        String f = translate(t);
        if (cfg.getBoolean(path + ".chat")) p.sendMessage(translate(cfg.getString("prefix")) + f);
        if (cfg.getBoolean(path + ".actionbar")) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(f));
        if (cfg.getBoolean(path + ".title")) p.sendTitle(f, "", 10, 40, 10);
    }
              }
