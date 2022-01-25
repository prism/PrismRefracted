package network.darkhelmet.prism.listeners;

import network.darkhelmet.prism.actions.Action;
import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.activities.Activity;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.recording.RecordingQueue;
import network.darkhelmet.prism.utils.BlockUtils;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    /**
     * Listens for block break events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = BlockUtils.getRootBlock(event.getBlock());

        // Build the block break action
        Action action = Action.builder().type(ActionRegistry.BLOCK_BREAK).block(block).build();

        // Build the block break by player activity
        final IActivity activity = Activity.builder()
            .action(action).location(block.getLocation()).cause(player).build();

        RecordingQueue.addToQueue(activity);
    }
}
