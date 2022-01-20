package net.pl3xmap.map.vnp;

import net.pl3xmap.map.vnp.listener.VanishListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xMapVanishNoPacket extends JavaPlugin {

    public void onEnable() {
        if (!this.getServer().getPluginManager().isPluginEnabled("VanishNoPacket")) {
            this.setEnabled(false);
        } else if (!this.getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            this.setEnabled(false);
        } else {
            this.getServer().getPluginManager().registerEvents(new VanishListener(), this);
        }
    }
}

