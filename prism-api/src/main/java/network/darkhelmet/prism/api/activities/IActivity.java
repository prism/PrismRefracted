package network.darkhelmet.prism.api.activities;

import network.darkhelmet.prism.api.actions.IAction;

import org.bukkit.Location;

public interface IActivity {
    /**
     * Get the action.
     *
     * @return The action
     */
    IAction action();

    /**
     * Get the cause.
     *
     * @return The cause
     */
    Object cause();

    /**
     * Get the location.
     *
     * @return The location
     */
    Location location();

    /**
     * Get the timestamp.
     *
     * @return The timestamp
     */
    long timestamp();
}
