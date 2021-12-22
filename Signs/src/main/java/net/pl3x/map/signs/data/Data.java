package net.pl3x.map.signs.data;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.marker.Marker;

public class Data {
    private final Marker marker;
    private final Key key;
    private final String[] lines;

    public Data(Marker marker, Key key, String[] lines) {
        this.marker = marker;
        this.key = key;
        this.lines = lines;
    }

    public Marker getMarker() {
        return marker;
    }

    public Key getKey() {
        return this.key;
    }

    public String[] getLines() {
        return lines;
    }
}
