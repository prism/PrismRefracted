package network.darkhelmet.prism.api.actions;

public interface IBlockAction extends IMaterialAction, ICustomData {
    /**
     * Serialize block data.
     *
     * @return The block data string
     */
    String serializeBlockData();
}
