package net.pl3x.map.worldguard.task;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.worldguard.configuration.Config;
import net.pl3x.map.worldguard.hook.WGHook;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Pl3xMapTask extends BukkitRunnable {
    private final MapWorld world;
    private final SimpleLayerProvider provider;

    private boolean stop;

    public Pl3xMapTask(MapWorld world, SimpleLayerProvider provider) {
        this.world = world;
        this.provider = provider;
    }

    @Override
    public void run() {
        if (stop) {
            cancel();
        }
        updateClaims();
    }

    void updateClaims() {
        provider.clearMarkers(); // TODO track markers instead of clearing them
        Map<String, ProtectedRegion> regions = WGHook.getRegions(world.uuid());
        if (regions == null) {
            return;
        }
        regions.forEach((id, region) -> handleClaim(region));
    }

    private void handleClaim(ProtectedRegion region) {
        Marker marker;

        if (region.getType() == RegionType.CUBOID) {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            marker = Marker.rectangle(
                    Point.of(min.getX(), min.getZ()),
                    Point.of(max.getX() + 1, max.getZ() + 1)
            );
        } else if (region.getType() == RegionType.POLYGON) {
            List<Point> points = region.getPoints().stream()
                    .map(point -> Point.of(point.getX(), point.getZ()))
                    .collect(Collectors.toList());
            marker = Marker.polygon(points);
        } else {
            // do not draw global region
            return;
        }

        ProfileCache pc = WorldGuard.getInstance().getProfileCache();
        Map<Flag<?>, Object> flags = region.getFlags();

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.STROKE_COLOR)
                .strokeWeight(Config.STROKE_WEIGHT)
                .strokeOpacity(Config.STROKE_OPACITY)
                .fillColor(Config.FILL_COLOR)
                .fillOpacity(Config.FILL_OPACITY)
                .clickTooltip(Config.CLAIM_TOOLTIP
                        .replace("{world}", world.name())
                        .replace("{id}", region.getId())
                        .replace("{owner}", region.getOwners().toPlayersString())
                        .replace("{regionname}", region.getId())
                        .replace("{playerowners}", region.getOwners().toPlayersString(pc))
                        .replace("{groupowners}", region.getOwners().toGroupsString())
                        .replace("{playermembers}", region.getMembers().toPlayersString(pc))
                        .replace("{groupmembers}", region.getMembers().toGroupsString())
                        .replace("{parent}", region.getParent() == null ? "" : region.getParent().getId())
                        .replace("{priority}", String.valueOf(region.getPriority()))
                        .replace("{flags}", flags.keySet().stream()
                                .map(flag -> flag.getName() + ": " + flags.get(flag) + "<br/>")
                                .collect(Collectors.joining()))
                );


        marker.markerOptions(options);

        String markerid = "worldguard_" + world.name() + "_region_" + region.getId().hashCode();
        this.provider.addMarker(Key.of(markerid), marker);
    }

    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}

