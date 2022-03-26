package net.pl3x.map.mobs.task;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Icon;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.mobs.configuration.WorldConfig;
import net.pl3x.map.mobs.data.Icons;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;

public class Pl3xMapTask extends BukkitRunnable {
    private final MapWorld world;
    private final SimpleLayerProvider provider;
    private final WorldConfig worldConfig;

    private boolean stop;

    public Pl3xMapTask(MapWorld world, WorldConfig worldConfig, SimpleLayerProvider provider) {
        this.world = world;
        this.provider = provider;
        this.worldConfig = worldConfig;
    }

    @Override
    public void run() {
        if (stop) {
            cancel();
        }

        provider.clearMarkers();

        World bukkit = Bukkit.getWorld(world.uuid());
        if (bukkit == null) {
            return;
        }

        bukkit.getEntities().forEach(mob -> {
            if (!(mob instanceof Mob)) {
                return;
            }
            EntityType type = mob.getType();
            if (!worldConfig.ALLOWED_TYPES.contains(type)) {
                return;
            }
            Location loc = mob.getLocation();
            if (loc.getY() < worldConfig.MINIMUM_Y) {
                return;
            }
            if (worldConfig.SURFACE_ONLY && loc.getY() < loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ(), HeightMap.WORLD_SURFACE)) {
                return;
            }
            handleMob(type, mob.getEntityId(), loc);
        });
    }

    private void handleMob(EntityType type, int id, Location loc) {
        Icon icon = Marker.icon(Point.fromLocation(loc), Icons.getIcon(type), worldConfig.ICON_SIZE);

        String key = type.getKey().getKey();
        String name = WordUtils.capitalizeFully(key.replace("_", " "));

        icon.markerOptions(MarkerOptions.builder()
                .hoverTooltip(worldConfig.ICON_TOOLTIP
                        .replace("{id}", Integer.toString(id))
                        .replace("{key}", key)
                        .replace("{name}", name)
                )
        );

        String markerid = world.name() + "_mob_" + type + "_id_" + id;
        this.provider.addMarker(Key.of(markerid), icon);
    }

    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}
