package net.pl3x.map.griefprevention;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.Pl3xMapProvider;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.griefprevention.configuration.Config;
import net.pl3x.map.griefprevention.hook.GPHook;
import net.pl3x.map.griefprevention.listener.GPListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Pl3xMapGriefprevention extends JavaPlugin {

    private GPListener gpListener;

    @Override
    public void onEnable() {
        Config.reload(this);

        if (!getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
            getLogger().severe("GriefPrevention not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            getLogger().severe("Pl3xMap not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        Map<UUID, SimpleLayerProvider> cache = new HashMap<>();

        Pl3xMapProvider.get().mapWorlds().forEach(world -> {
            SimpleLayerProvider provider = SimpleLayerProvider
                    .builder(Config.CONTROL_LABEL)
                    .showControls(Config.CONTROL_SHOW)
                    .defaultHidden(Config.CONTROL_HIDE)
                    .build();
            world.layerRegistry().register(Key.of("griefprevention_" + world.uuid()), provider);
            cache.put(world.uuid(), provider);
        });

        GPListener gpListener = new GPListener(cache);

        GriefPrevention.instance.dataStore.getClaims().forEach(gpListener::addOrEditClaim);

        Bukkit.getPluginManager().registerEvents(gpListener, this);
    }

}
