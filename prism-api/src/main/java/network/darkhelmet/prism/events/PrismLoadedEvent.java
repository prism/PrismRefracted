package network.darkhelmet.prism.events;

import network.darkhelmet.prism.api.PrismApi;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * The Prism Load Event is called once Prism has finished loading.
 *
 * @author Narimm
 */
public class PrismLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final PrismApi api;

    protected PrismLoadedEvent(PrismApi api) {
        super(true);
        this.api = api;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PrismApi getApi() {
        return api;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
