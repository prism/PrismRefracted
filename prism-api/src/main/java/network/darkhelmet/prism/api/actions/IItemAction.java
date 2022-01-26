package network.darkhelmet.prism.api.actions;

public interface IItemAction extends IMaterialAction {
    /**
     * Serialize item.
     *
     * @return The item string
     */
    String serializeItem();
}
