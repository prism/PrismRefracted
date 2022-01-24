package network.darkhelmet.prism.api.actions;

import java.util.Locale;

import org.bukkit.block.BlockState;

public class BlockStateAction extends Action {
    /**
     * The material.
     */
    private String material;

    /**
     * The block data.
     */
    private String blockData;

    /**
     * Construct a block state action.
     *
     * @param key The action key
     * @param blockState The block state
     */
    public BlockStateAction(String key, BlockState blockState) {
        super(key);

        this.material = blockState.getType().toString().toLowerCase(Locale.ENGLISH);
        this.blockData = blockState.getBlockData().getAsString().replaceAll("^[^\\[]+", "");
    }

    /**
     * Construct a block state action.
     *
     * @param key The action key
     * @param material The material string
     * @param blockData The block data string
     */
    public BlockStateAction(String key, String material, String blockData) {
        super(key);

        this.material = material;
        this.blockData = blockData;
    }

    /**
     * Get the block data.
     *
     * @return The block data
     */
    public String blockData() {
        return blockData;
    }

    /**
     * Get the material.
     *
     * @return The material
     */
    public String material() {
        return material;
    }

    @Override
    public String toString() {
        return "BlockStateAction["
            + "material=" + material + ","
            + "blockData=" + blockData + ']';
    }
}
