package network.darkhelmet.prism.listeners;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.ActionFactory;
import network.darkhelmet.prism.actionlibs.RecordingQueue;
import network.darkhelmet.prism.events.PrismCustomPlayerActionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class PrismCustomEvents implements Listener {

    private final Prism plugin;

    /**
     * Create a custom event listener.
     *
     * @param plugin Prism
     */
    public PrismCustomEvents(Prism plugin) {
        this.plugin = plugin;
    }

    /**
     * On Custom action.
     * @param event PrismCustomPlayerActionEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCustomPlayerAction(final PrismCustomPlayerActionEvent event) {
        final List<String> allowedPlugins = plugin.getConfig().getStringList("prism.tracking.api.allowed-plugins");
        if (allowedPlugins.contains(event.getPluginName())) {
            RecordingQueue.addToQueue(
                    ActionFactory.createPlayer(event.getActionTypeName(), event.getPlayer(), event.getMessage()));
        }
    }
}