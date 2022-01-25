package network.darkhelmet.prism.api.actions;

import java.util.Locale;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public class BlockStateAction extends Action implements Reversible {
    /**
     * The location of this block action.
     */
    private Location location;

    /**
     * The material.
     */
    private Material material;

    /**
     * The block data.
     */
    private BlockData blockData;

    /**
     * Construct a block state action.
     *
     * @param key The action key
     * @param blockState The block state
     */
    public BlockStateAction(String key, BlockState blockState) {
        super(key);

        this.location = blockState.getLocation();
        this.material = blockState.getType();
        this.blockData = blockState.getBlockData();
    }

    /**
     * Construct a block state action.
     *
     * @param key The action key
     * @param material The material string
     * @param blockData The block data string
     */
    public BlockStateAction(String key, Location location, Material material, BlockData blockData) {
        super(key);

        this.location = location;
        this.material = material;
        this.blockData = blockData;
    }

    /**
     * Serialize the material.
     *
     * @return The material string
     */
    public String serializeMaterial() {
        return material.toString().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Serialize block data.
     *
     * @return The block data string
     */
    public String serializeBlockData() {
        return blockData.getAsString().replaceAll("^[^\\[]+", "");
    }

    @Override
    public void applyRollback() {
        final Block block = location.getWorld().getBlockAt(location);
        block.setType(material);
        block.getState().setBlockData(blockData);
    }

    @Override
    public void applyRestore() {

    }

    @Override
    public String toString() {
        return "BlockStateAction["
            + "material=" + material + ","
            + "blockData=" + blockData + ']';
    }
}
