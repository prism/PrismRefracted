package network.darkhelmet.prism.api.actions.types;

public abstract class ActionType implements IActionType {
    /**
     * The key.
     */
    protected String key;

    /**
     * The action result type.
     */
    protected ActionResultType resultType;

    /**
     * Indicates whether this action type is usually reversible.
     */
    protected boolean reversible;

    /**
     * Construct a new action type.
     *
     * @param key The key
     * @param resultType The result type
     * @param reversible If action is reversible
     */
    public ActionType(String key, ActionResultType resultType, boolean reversible) {
        this.key = key;
        this.resultType = resultType;
        this.reversible = reversible;
    }

    /**
     * Get the key.
     *
     * @return The key
     */
    public String key() {
        return key;
    }

    /**
     * Get the action result type.
     *
     * @return The result type
     */
    public ActionResultType resultType() {
        return resultType;
    }

    /**
     * Get if this action type if usually reversible.
     *
     * @return True if reversible
     */
    public boolean reversible() {
        return reversible;
    }
}
