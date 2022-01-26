package network.darkhelmet.prism.listeners;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.Activity;
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
        // Ignore if this event is disabled
        if (!Prism.getInstance().config().actions().blockBreak()) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = BlockUtils.getRootBlock(event.getBlock());

        // Build the action
        final IAction action = Prism.getInstance().actionRegistry()
            .createBlockAction(ActionRegistry.BLOCK_BREAK, block);

        // Build the block break by player activity
        final IActivity activity = Activity.builder()
            .action(action).location(block.getLocation()).cause(player).build();

        RecordingQueue.addToQueue(activity);
    }
}
