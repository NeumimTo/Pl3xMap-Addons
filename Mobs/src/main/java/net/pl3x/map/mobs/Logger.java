package net.pl3x.map.mobs;

import net.pl3x.map.mobs.configuration.Config;
import net.pl3x.map.mobs.configuration.Lang;

public class Logger {
    public static java.util.logging.Logger log() {
        return Pl3xMapMobs.getInstance().getLogger();
    }

    public static void debug(String msg) {
        if (Config.DEBUG_MODE) {
            for (String part : Lang.split(msg)) {
                log().info(part);
            }
        }
    }

    public static void info(String msg) {
        for (String part : Lang.split(msg)) {
            log().info(part);
        }
    }

    public static void warn(String msg) {
        for (String part : Lang.split(msg)) {
            log().warning(part);
        }
    }

    public static void severe(String msg) {
        for (String part : Lang.split(msg)) {
            log().severe(part);
        }
    }
}
