package network.darkhelmet.prism.bridge;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import org.bukkit.Bukkit;

public class PrismBlockEditHandler {

    private final boolean usingFawe;

    public PrismBlockEditHandler(boolean usingFawe) {
        this.usingFawe = usingFawe;
    }

    /**
     * Wrap and edit session so it can be logged.
     *
     * @param event EditSessionEvent
     */
    @Subscribe
    public void wrapForLogging(EditSessionEvent event) {
        if (usingFawe || event.getStage().equals(EditSession.Stage.BEFORE_CHANGE)) {
            Actor actor = event.getActor();
            org.bukkit.World world = Bukkit.getWorld(event.getWorld().getName());
            if (actor != null && actor.isPlayer() && world != null) {
                event.setExtent(new PrismWorldEditLogger(actor, event.getExtent(), world));
            }
        }
    }
}