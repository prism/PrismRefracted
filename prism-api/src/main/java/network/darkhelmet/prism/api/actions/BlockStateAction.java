package network.darkhelmet.prism.api.actions;

import org.bukkit.block.BlockState;

public class BlockStateAction extends Action {
    /**
     * The block state.
     */
    private BlockState blockState;

    /**
     * Construct a block state action.
     *
     * @param key The action key
     * @param blockState The block state
     */
    public BlockStateAction(String key, BlockState blockState) {
        super(key);

        this.blockState = blockState;
    }

    /**
     * Get the block state.
     *
     * @return The block state
     */
    public BlockState blockState() {
        return blockState;
    }
}
