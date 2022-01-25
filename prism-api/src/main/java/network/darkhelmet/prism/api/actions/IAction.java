package network.darkhelmet.prism.api.actions;

public interface IAction {
    /**
     * Get whether this action will have custom data (tile entities, etc).
     *
     * @return True if has custom data
     */
    boolean hasCustomData();

    /**
     * Serialize the custom data.
     *
     * @return The serialized data, or null
     */
    String serializeCustomData();

    /**
     * Get the action type.
     *
     * @return The action type
     */
    ActionType type();
}
