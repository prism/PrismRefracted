package network.darkhelmet.prism.listeners;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.recording.RecordingQueue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    /**
     * Listens to (player) item drop events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        // Build the action
        final IAction action = Prism.getInstance().actionRegistry()
            .createItemStackAction(ActionRegistry.ITEM_DROP, event.getItemDrop().getItemStack());

        final IActivity activity = Activity.builder()
            .action(action).cause(event.getPlayer()).location(event.getPlayer().getLocation()).build();

        RecordingQueue.addToQueue(activity);
    }
}
