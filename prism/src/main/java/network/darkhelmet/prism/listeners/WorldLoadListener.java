package network.darkhelmet.prism.listeners;

import java.util.Optional;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.storage.models.WorldModel;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadListener implements Listener {
    /**
     * Listens to world load events.
     *
     * @param event The world load event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(final WorldLoadEvent event) {
        Optional<WorldModel> optionalWorldModel = Prism.getInstance()
            .storageAdapter().getOrRegisterWorld(event.getWorld());
        optionalWorldModel.ifPresent(worldModel -> Prism.getInstance().storageCache().cacheWorldModel(worldModel));
    }
}
