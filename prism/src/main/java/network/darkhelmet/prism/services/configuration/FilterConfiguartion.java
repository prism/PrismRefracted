package network.darkhelmet.prism.services.configuration;

import java.util.ArrayList;
import java.util.List;

import network.darkhelmet.prism.api.services.filters.FilterBehavior;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class FilterConfiguartion {
    /**
     * Worlds.
     */
    private List<String> worlds = new ArrayList<>();

    /**
     * The filter behavior.
     */
    private FilterBehavior behavior;

    /**
     * Get the behavior.
     *
     * @return The behavior
     */
    public FilterBehavior behavior() {
        return behavior;
    }

    /**
     * Get the worlds.
     *
     * @return The worlds.
     */
    public List<String> worlds() {
        return worlds;
    }
}
