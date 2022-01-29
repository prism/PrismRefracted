package network.darkhelmet.prism.api.actions;

public interface IEntityAction extends IAction, ICustomData {
    /**
     * Serialize the entity type.
     *
     * @return The serialized entity type
     */
    String serializeEntityType();
}
