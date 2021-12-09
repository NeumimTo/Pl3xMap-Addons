package net.pl3x.map.worldguard.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Map;
import java.util.UUID;

public class WGHook {
    public static Map<String, ProtectedRegion> getRegions(UUID uuid) {
        World bukkitWorld = Bukkit.getWorld(uuid);
        if (bukkitWorld == null) {
            return null;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(bukkitWorld));
        if (manager == null) {
            return null;
        }
        return manager.getRegions();
    }
}
