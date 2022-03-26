package net.pl3x.map.mobs;

import net.pl3x.map.api.Key;
import net.pl3x.map.mobs.configuration.Config;
import net.pl3x.map.mobs.data.Icons;
import net.pl3x.map.mobs.hook.Pl3xMapHook;
import org.bukkit.plugin.java.JavaPlugin;

public final class Pl3xMapMobs extends JavaPlugin {
    private static Pl3xMapMobs instance;

    public Pl3xMapMobs() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Config.reload();

        if (!getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            Logger.severe("Pl3xMap not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //noinspection unused
        Key loadme = Icons.BAT;

        Pl3xMapHook.load(this);
    }

    @Override
    public void onDisable() {
        Pl3xMapHook.disable();
    }

    public static Pl3xMapMobs getInstance() {
        return instance;
    }
}
