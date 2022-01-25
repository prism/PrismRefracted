package network.darkhelmet.prism.api.actions;

public final class ActionType {
    /**
     * The key.
     */
    private String key;

    /**
     * The action result type.
     */
    private ActionResultType resultType;

    /**
     * Construct a new action type.
     *
     * @param key The key
     * @param resultType The result type
     */
    public ActionType(String key, ActionResultType resultType) {
        this.key = key;
        this.resultType = resultType;
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
}
