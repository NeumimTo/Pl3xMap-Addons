package net.pl3x.map.griefprevention.listener;


import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimModifiedEvent;
import me.ryanhamshire.GriefPrevention.events.TrustChangedEvent;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.api.marker.Rectangle;
import net.pl3x.map.griefprevention.configuration.Config;
import net.pl3x.map.griefprevention.hook.GPHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GPListener implements Listener {

    private final Map<UUID, SimpleLayerProvider> worldCache;

    public GPListener(Map<UUID, SimpleLayerProvider> cache) {
        this.worldCache = cache;
    }

    public void addOrEditClaim(Claim claim) {
        Location min = claim.getLesserBoundaryCorner();
        Location max = claim.getGreaterBoundaryCorner();
        if (min == null) {
            return;
        }

        if (!worldCache.containsKey(min.getWorld().getUID())) {
            return;
        }

        SimpleLayerProvider layerProvider = worldCache.get(min.getWorld().getUID());

        Key key = GPHook.key(claim);
        if (layerProvider.hasMarker(key)) {
            layerProvider.removeMarker(key);
        }

        Rectangle rect = Marker.rectangle(Point.of(min.getBlockX(), min.getBlockZ()), Point.of(max.getBlockX() + 1, max.getBlockZ() + 1));

        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);

        String worldName = min.getWorld().getName();

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.STROKE_COLOR)
                .strokeWeight(Config.STROKE_WEIGHT)
                .strokeOpacity(Config.STROKE_OPACITY)
                .fillColor(Config.FILL_COLOR)
                .fillOpacity(Config.FILL_OPACITY)
                .clickTooltip((claim.isAdminClaim() ? Config.ADMIN_CLAIM_TOOLTIP : Config.CLAIM_TOOLTIP)
                        .replace("{world}", worldName)
                        .replace("{id}", Long.toString(claim.getID()))
                        .replace("{owner}", claim.getOwnerName())
                        .replace("{ownerUUID}", String.valueOf(claim.getOwnerID()))
                        .replace("{managers}", getNames(managers))
                        .replace("{builders}", getNames(builders))
                        .replace("{containers}", getNames(containers))
                        .replace("{accessors}", getNames(accessors))
                        .replace("{area}", Integer.toString(claim.getArea()))
                        .replace("{width}", Integer.toString(claim.getWidth()))
                        .replace("{height}", Integer.toString(claim.getHeight()))
                );

        if (claim.isAdminClaim()) {
            options.strokeColor(Color.BLUE).fillColor(Color.BLUE);
        }

        rect.markerOptions(options);

        layerProvider.addMarker(key, rect);
    }

    private void removeClaim(Claim claim) {
        Key key = GPHook.key(claim);
        SimpleLayerProvider layerProvider = worldCache.get(claim.getLesserBoundaryCorner().getWorld().getUID());
        if (layerProvider.hasMarker(key)) {
            layerProvider.removeMarker(key);
        }
    }

    private static String getNames(java.util.List<String> list) {
        List<String> names = new ArrayList<>();
        for (String str : list) {
            try {
                UUID uuid = UUID.fromString(str);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                names.add(offlinePlayer.getName());
            } catch (Exception e) {
                names.add(str);
            }
        }
        return String.join(", ", names);
    }

    @EventHandler
    public void onClaimCreate(ClaimCreatedEvent event) {
        addOrEditClaim(event.getClaim());
    }

    @EventHandler
    public void onClaimDeleted(ClaimDeletedEvent event) {
        removeClaim(event.getClaim());
    }

    @EventHandler
    public void onClaimModified(ClaimModifiedEvent event) {
        removeClaim(event.getFrom());
        addOrEditClaim(event.getTo());
    }

    @EventHandler
    public void onTrustChange(TrustChangedEvent event) {
        for (Claim claim : event.getClaims()) {
            addOrEditClaim(claim);
        }
    }
}
