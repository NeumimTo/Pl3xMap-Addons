package net.pl3x.map.mobs.configuration;

import net.pl3x.map.api.MapWorld;
import net.pl3x.map.mobs.Logger;
import net.pl3x.map.mobs.data.Icons;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class WorldConfig {
    private static final Map<UUID, WorldConfig> configs = new HashMap<>();

    public static void reload() {
        configs.clear();
    }

    public static WorldConfig get(MapWorld world) {
        WorldConfig config = configs.get(world.uuid());
        if (config == null) {
            config = new WorldConfig(world);
            configs.put(world.uuid(), config);
        }
        return config;
    }

    private final String worldName;

    public WorldConfig(MapWorld world) {
        this.worldName = world.name();
        init();
    }

    public void init() {
        Config.readConfig(WorldConfig.class, this);
    }

    private void set(String path, Object val) {
        Config.CONFIG.addDefault("world-settings.default." + path, val);
        Config.CONFIG.set("world-settings.default." + path, val);
        if (Config.CONFIG.get("world-settings." + worldName + "." + path) != null) {
            Config.CONFIG.addDefault("world-settings." + worldName + "." + path, val);
            Config.CONFIG.set("world-settings." + worldName + "." + path, val);
        }
    }

    private boolean getBoolean(String path, boolean def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getBoolean("world-settings." + worldName + "." + path, Config.CONFIG.getBoolean("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getInt("world-settings." + worldName + "." + path, Config.CONFIG.getInt("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getString("world-settings." + worldName + "." + path, Config.CONFIG.getString("world-settings.default." + path));
    }

    <T> List<?> getList(String path, T def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getList("world-settings." + worldName + "." + path,
                Config.CONFIG.getList("world-settings.default." + path));
    }

    public boolean ENABLED = true;

    private void worldSettings() {
        ENABLED = getBoolean("enabled", ENABLED);
    }

    public String LAYER_LABEL = "Mobs";
    public boolean LAYER_SHOW_CONTROLS = true;
    public boolean LAYER_CONTROLS_HIDDEN = false;
    public int LAYER_PRIORITY = 999;
    public int LAYER_ZINDEX = 999;

    private void layerSettings() {
        LAYER_LABEL = getString("layer.label", LAYER_LABEL);
        LAYER_SHOW_CONTROLS = getBoolean("layer.controls.enabled", LAYER_SHOW_CONTROLS);
        LAYER_CONTROLS_HIDDEN = getBoolean("layer.controls.hide-by-default", LAYER_CONTROLS_HIDDEN);
        LAYER_PRIORITY = getInt("layer.priority", LAYER_PRIORITY);
        LAYER_ZINDEX = getInt("layer.z-index", LAYER_ZINDEX);
    }

    public int MINIMUM_Y = 64;
    public boolean SURFACE_ONLY = true;

    public int ICON_SIZE = 16;
    public String ICON_TOOLTIP = "{name}";

    private void iconSettings() {
        ICON_SIZE = getInt("icon.size", ICON_SIZE);
        ICON_TOOLTIP = getString("icon.tooltip", ICON_TOOLTIP);
    }

    public final Set<EntityType> ALLOWED_TYPES = new HashSet<>();

    private void allowedTypes() {
        ALLOWED_TYPES.clear();
        getList("allowed-mobs", List.of(
                "cat",
                "chicken",
                "cod",
                "cow",
                "dolphin",
                "fox",
                "horse",
                "iron_golem",
                "llama",
                "mooshroom",
                "mule",
                "ocelot",
                "panda",
                "parrot",
                "pig",
                "polar_bear",
                "pufferfish",
                "rabbit",
                "salmon",
                "sheep",
                "snow_golem",
                "squid",
                "strider",
                "trader_llama",
                "tropical_fish",
                "turtle",
                "villager",
                "wandering_trader",
                "wolf"
        )).forEach(key -> {
            if (key.toString().equals("*")) {
                ALLOWED_TYPES.addAll(Icons.BY_TYPE.keySet());
                return;
            }
            //noinspection deprecation
            EntityType type = EntityType.fromName(key.toString());
            if (type != null) {
                ALLOWED_TYPES.add(type);
            } else {
                Logger.warn("Unknown entity type: " + key);
            }
        });
    }
}
