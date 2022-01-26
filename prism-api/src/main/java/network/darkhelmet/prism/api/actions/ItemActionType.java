package network.darkhelmet.prism.api.actions;

public class ItemActionType extends ActionType {
    /**
     * Construct a new item action type.
     *
     * @param key The key
     * @param resultType The result type
     */
    public ItemActionType(String key, ActionResultType resultType) {
        super(key, resultType);
    }
}