package network.darkhelmet.prism.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class PrismConfiguration {
    @Comment("Actions are in-game events/changes that Prism can record data for.\n"
            + "Some are purely informational, some can be reversed/restored.\n"
            + "Disabling any here will completely prevent prism from recording them.")
    private ActionsConfig actions = new ActionsConfig();

    @Comment("Enable plugin debug mode. Produces extra logging to help diagnose issues.")
    private boolean debug = false;

    @Comment("Sets the default radius to use when searching for nearby activity.")
    private int nearRadius = 5;

    @Comment("Configure in-game command outputs.")
    private OutputConfiguration outputs = new OutputConfiguration();

    /**
     * Get the actions config.
     *
     * @return The actions config
     */
    public ActionsConfig actions() {
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
     * Get the near radius.
     *
     * @return The near radius
     */
    public int nearRadius() {
        return nearRadius;
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