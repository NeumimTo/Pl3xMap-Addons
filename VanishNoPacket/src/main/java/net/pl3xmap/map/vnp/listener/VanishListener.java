package net.pl3xmap.map.vnp.listener;

import net.pl3x.map.api.Pl3xMapProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

public class VanishListener implements Listener {

    @EventHandler
    public void onVanishChange(VanishStatusChangeEvent event) {
        Pl3xMapProvider.get().playerManager().hidden(event.getPlayer().getUniqueId(), event.isVanishing());
    }

}