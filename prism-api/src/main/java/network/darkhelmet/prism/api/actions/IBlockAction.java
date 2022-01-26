package network.darkhelmet.prism.api.actions;

public interface IBlockAction extends IMaterialAction, ICustomData, Reversible {
    /**
     * Serialize block data.
     *
     * @return The block data string
     */
    String serializeBlockData();
}
