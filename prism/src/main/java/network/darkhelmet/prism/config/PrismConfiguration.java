package network.darkhelmet.prism.config;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class PrismConfiguration {
    @Comment("Actions are in-game events/changes that Prism can record data for.\n"
            + "Some are purely informational, some can be reversed/restored.")
    private Map<String, Boolean> actions = new HashMap<>();

    @Comment("Enable plugin debug mode. Produces extra logging to help diagnose issues.")
    private boolean debug = false;

    @Comment("Configure in-game command outputs.")
    private OutputConfiguration outputs = new OutputConfiguration();

    /**
     * Construct a new instance.
     */
    public PrismConfiguration() {
        actions.put("block-break", true);
    }

    /**
     * Get the actions/enabled map.
     *
     * @return Map of actions and their enabled state
     */
    public Map<String, Boolean> actions() {
        return actions;
    }

    /**
     * Get the debug setting.
     *
     * @return True if debug enabled.
     */
    public boolean debug() {
        return debug;
    }

    /**
     * Get the outout configuration.
     *
     * @return The outputs
     */
    public OutputConfiguration outputs() {
        return outputs;
    }
}