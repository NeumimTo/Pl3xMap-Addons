package net.pl3x.map.signs.data;

import net.pl3x.map.api.Point;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

public class Position {
    private final int x;
    private final int y;
    private final int z;

    public static Position of(Location loc) {
        return new Position(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Position of(int x, int y, int z) {
        return new Position(x, y, z);
    }

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Point point() {
        return Point.of(x, z);
    }

    public boolean isSign(World world) {
        return world.getBlockAt(x, y, z).getState() instanceof Sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position other = (Position) o;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "Position{x=" + x + ",y=" + y + ",z=" + z + "}";
    }
}
