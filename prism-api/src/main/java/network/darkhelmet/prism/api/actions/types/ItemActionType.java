package network.darkhelmet.prism.api.actions.types;

public class ItemActionType extends ActionType {
    /**
     * Construct a new item action type.
     *
     * @param key The key
     * @param resultType The result type
     * @param reversible If action is reversible
     */
    public ItemActionType(String key, ActionResultType resultType, boolean reversible) {
        super(key, resultType, reversible);
    }
}