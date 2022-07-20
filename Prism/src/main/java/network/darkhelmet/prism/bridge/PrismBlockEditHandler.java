package network.darkhelmet.prism.bridge;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import network.darkhelmet.prism.ApiHandler;
import org.bukkit.Bukkit;

public class PrismBlockEditHandler {

    private final ApiHandler.WEType weType;

    public PrismBlockEditHandler(ApiHandler.WEType weType) {
        this.weType = weType;
    }

    /**
     * Wrap and edit session so it can be logged.
     *
     * @param event EditSessionEvent
     */
    @Subscribe
    public void wrapForLogging(EditSessionEvent event) {
        switch (weType) {
            case WORLDEDIT, ASYNC_WORLDEDIT -> {
                if (event.getStage() != EditSession.Stage.BEFORE_REORDER) return;
            }
            case FAST_ASYNC_WORLDEDIT -> {
                if (event.getStage() != EditSession.Stage.BEFORE_HISTORY) return;
            }
        }

        Actor actor = event.getActor();
        org.bukkit.World world = Bukkit.getWorld(event.getWorld().getName());
        if (actor != null && world != null) {
            event.setExtent(new PrismWorldEditLogger(actor, event.getExtent(), world));
        }
    }
}