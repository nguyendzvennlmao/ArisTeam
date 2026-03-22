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
    private final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public MessageManager(ArisTeam plugin) { this.plugin = plugin; }

    public String translate(String message) {
        if (message == null) return "";
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String color = matcher.group(1);
            matcher.appendReplacement(sb, ChatColor.of("#" + color).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(sb).toString());
    }

    public void send(Player p, String path, Map<String, String> placeholders) {
        FileConfiguration msgCfg = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
        String msg = msgCfg.getString(path + ".text", "");
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace(entry.getKey(), entry.getValue());
            }
        }
        
        String formatted = translate(msg);
        String prefix = translate(msgCfg.getString("prefix", ""));

        if (msgCfg.getBoolean(path + ".chat", true)) p.sendMessage(prefix + formatted);
        if (msgCfg.getBoolean(path + ".actionbar", false)) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatted));
        if (msgCfg.getBoolean(path + ".title", false)) p.sendTitle(formatted, "", 10, 40, 10);
    }
                 }
