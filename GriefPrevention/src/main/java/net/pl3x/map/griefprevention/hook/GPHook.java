package net.pl3x.map.griefprevention.hook;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.pl3x.map.api.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Collection;
import java.util.UUID;

public class GPHook {

    public static Key key(Claim claim) {
        String worldName = claim.getLesserBoundaryCorner().getWorld().getName();
        String markerid = "griefprevention_" + worldName + "_region_" + Long.toHexString(claim.getID());
        return Key.key(markerid);
    }


}
