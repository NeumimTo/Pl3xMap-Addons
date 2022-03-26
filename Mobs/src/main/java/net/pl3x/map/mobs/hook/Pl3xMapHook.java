package net.pl3x.map.mobs.hook;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.Pl3xMapProvider;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.mobs.configuration.Config;
import net.pl3x.map.mobs.configuration.WorldConfig;
import net.pl3x.map.mobs.task.Pl3xMapTask;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pl3xMapHook {
    private static final Map<UUID, Pl3xMapTask> providers = new HashMap<>();

    public static void load(Plugin plugin) {
        api().mapWorlds().forEach(mapWorld -> {
            WorldConfig worldConfig = WorldConfig.get(mapWorld);
            if (worldConfig.ENABLED) {
                SimpleLayerProvider provider = SimpleLayerProvider.builder(worldConfig.LAYER_LABEL)
                        .showControls(worldConfig.LAYER_SHOW_CONTROLS)
                        .defaultHidden(worldConfig.LAYER_CONTROLS_HIDDEN)
                        .build();
                mapWorld.layerRegistry().register(Key.of(mapWorld.uuid() + "_mobs"), provider);
                Pl3xMapTask task = new Pl3xMapTask(mapWorld, worldConfig, provider);
                task.runTaskTimerAsynchronously(plugin, 0, 20L * Config.UPDATE_INTERVAL);
                providers.put(mapWorld.uuid(), task);
            }
        });
    }

    public static void disable() {
        providers.values().forEach(Pl3xMapTask::disable);
        providers.clear();
    }

    public static Pl3xMap api() {
        return Pl3xMapProvider.get();
    }
}
