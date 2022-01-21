package network.darkhelmet.prism.appliers;

import network.darkhelmet.prism.api.BlockStateChange;
import network.darkhelmet.prism.api.ChangeResult;
import network.darkhelmet.prism.api.ChangeResultType;

public class ChangeResultImpl implements ChangeResult {

    protected final BlockStateChange blockStateChange;
    protected final ChangeResultType changeResultType;

    /**
     * Constructor.
     * @param changeResultType ChangeResultType
     */
    public ChangeResultImpl(ChangeResultType changeResultType) {
        this(changeResultType, null);
    }

    /**
     * Constructor.
     * @param changeResultType ChangeResultType
     * @param blockStateChange BlockStateChange
     */
    public ChangeResultImpl(ChangeResultType changeResultType, BlockStateChange blockStateChange) {
        this.blockStateChange = blockStateChange;
        this.changeResultType = changeResultType;
    }

    /**
     * Get BlockStateChange.
     * @return BlockStateChange
     */
    public BlockStateChange getBlockStateChange() {
        return blockStateChange;
    }

    /**
     * Return ChangeResultType.
     * @return ChangeResultType
     */
    public ChangeResultType getType() {
        return changeResultType;
    }
}