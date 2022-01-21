package network.darkhelmet.prism.api;

import org.bukkit.block.BlockState;

public interface BlockStateChange {

    /**
     * The Original BlockState.
     * @return BlockState
     */
    BlockState getOriginalBlock();

    /**
     * The new blockState.
     * @return BlockState
     */
    BlockState getNewBlock();
}
