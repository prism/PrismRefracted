package network.darkhelmet.prism.api.actions;

public interface IMaterialAction extends IAction {
    /**
     * Serialize the material.
     *
     * @return The material string
     */
    String serializeMaterial();
}
