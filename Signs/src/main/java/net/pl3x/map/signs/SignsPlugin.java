package net.pl3x.map.signs;

import net.pl3x.map.api.Key;
import net.pl3x.map.signs.configuration.Config;
import net.pl3x.map.signs.data.Icons;
import net.pl3x.map.signs.data.SignManager;
import net.pl3x.map.signs.hook.Pl3xMapHook;
import net.pl3x.map.signs.listener.SignListener;
import net.pl3x.map.signs.listener.WorldListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignsPlugin extends JavaPlugin {
    private static SignsPlugin instance;
    private Pl3xMapHook pl3xmapHook;
    private SignManager signManager;

    public SignsPlugin() {
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
        Key loadme = Icons.OAK;

        pl3xmapHook = new Pl3xMapHook();
        pl3xmapHook.load();

        signManager = new SignManager(this);
        signManager.load();

        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    }

    @Override
    public void onDisable() {
        if (signManager != null) {
            signManager.save();
            signManager = null;
        }

        if (pl3xmapHook != null) {
            pl3xmapHook.disable();
            signManager = null;
        }
    }

    public static SignsPlugin getInstance() {
        return instance;
    }

    public Pl3xMapHook getPl3xmapHook() {
        return pl3xmapHook;
    }

    public SignManager getSignManager() {
        return signManager;
    }
}
