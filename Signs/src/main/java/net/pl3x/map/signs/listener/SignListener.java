package net.pl3x.map.signs.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import net.pl3x.map.signs.SignsPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
    private final SignsPlugin plugin;

    public SignListener(SignsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignEdit(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("pl3xmap.signs.admin")) {
            return;
        }
        BlockState state = event.getBlock().getState();
        if (!plugin.getSignManager().isTracked(state)) {
            return;
        }
        plugin.getSignManager().putSign(state, event.getLines());
    }

    @EventHandler
    public void onClickSign(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        BlockState state = block.getState();
        if (!(state instanceof Sign)) {
            return;
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            return;
        }
        if (!event.getPlayer().hasPermission("pl3xmap.signs.admin")) {
            return;
        }
        event.setCancelled(true);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            plugin.getSignManager().removeSign(state);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            plugin.getSignManager().putSign(state, ((Sign) state).getLines());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockDropItemEvent event) {
        remove(event.getBlockState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockDestroyEvent event) {
        remove(event.getBlock().getState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockBurnEvent event) {
        remove(event.getBlock().getState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockExplodeEvent event) {
        event.blockList().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(EntityExplodeEvent event) {
        event.blockList().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockPistonExtendEvent event) {
        event.getBlocks().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockPistonRetractEvent event) {
        event.getBlocks().forEach(block -> remove(block.getState()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockFromToEvent event) {
        remove(event.getToBlock().getState());
    }

    private void remove(BlockState state) {
        if (state instanceof Sign) {
            plugin.getSignManager().removeSign(state);
        }
    }
}
