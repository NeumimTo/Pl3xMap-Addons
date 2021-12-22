package net.pl3x.map.signs.configuration;

import org.bukkit.ChatColor;

public class Lang {
    public static String colorize(String str) {
        if (str == null) {
            return "";
        }
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (ChatColor.stripColor(str).isEmpty()) {
            return "";
        }
        return str;
    }

    public static String[] split(String msg) {
        return colorize(msg).split("\n");
    }
}
