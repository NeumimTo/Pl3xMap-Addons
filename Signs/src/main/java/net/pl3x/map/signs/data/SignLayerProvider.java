package net.pl3x.map.signs.data;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.LayerProvider;
import net.pl3x.map.api.marker.Icon;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.signs.configuration.WorldConfig;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SignLayerProvider implements LayerProvider {
    private final Map<Position, Data> data = new ConcurrentHashMap<>();
    private final WorldConfig worldConfig;

    public SignLayerProvider(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    @Override
    public @NonNull String getLabel() {
        return this.worldConfig.LAYER_LABEL;
    }

    @Override
    public boolean showControls() {
        return this.worldConfig.LAYER_CONTROLS;
    }

    @Override
    public boolean defaultHidden() {
        return this.worldConfig.LAYER_CONTROLS_HIDDEN;
    }

    @Override
    public int layerPriority() {
        return this.worldConfig.LAYER_PRIORITY;
    }

    @Override
    public int zIndex() {
        return this.worldConfig.LAYER_ZINDEX;
    }

    @Override
    public @NonNull Collection<Marker> getMarkers() {
        return this.data.values().stream()
                .map(Data::getMarker)
                .collect(Collectors.toSet());
    }

    public Set<Position> getPositions() {
        return this.data.keySet();
    }

    public Map<Position, Data> getData() {
        return this.data;
    }

    public Data getData(Position position) {
        return this.data.get(position);
    }

    public void add(Position position, Key key, String[] lines) {
        Icon icon = Marker.icon(position.point(), key, worldConfig.ICON_SIZE);
        icon.markerOptions(MarkerOptions.builder()
                .hoverTooltip(worldConfig.TOOLTIP
                        .replace("{line1}", lines[0])
                        .replace("{line2}", lines[1])
                        .replace("{line3}", lines[2])
                        .replace("{line4}", lines[3])));
        System.out.println("2");
        this.data.put(position, new Data(icon, key, lines));
    }

    public void remove(Position position) {
        this.data.remove(position);
    }
}
