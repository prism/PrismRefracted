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
     * @param type The action type
     * @param blockState The block state
     */
    public BlockStateAction(ActionType type, BlockState blockState) {
        super(type);

        this.location = blockState.getLocation();
        this.material = blockState.getType();
        this.blockData = blockState.getBlockData();
    }

    /**
     * Construct a block state action.
     *
     * @param type The action type
     * @param material The material string
     * @param blockData The block data string
     */
    public BlockStateAction(ActionType type, Location location, Material material, BlockData blockData) {
        super(type);

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
        if (type().resultType().equals(ActionResultType.REMOVES)) {
            // If the action type removes a block, rollback means we re-set it
            setBlock();
        } else if (type().resultType().equals(ActionResultType.CREATES)) {
            // If the action type creates a block, rollback means we remove it
            removeBlock();
        }
    }

    @Override
    public void applyRestore() {
        if (type().resultType().equals(ActionResultType.CREATES)) {
            // If the action type creates a block, restore means we re-set it
            setBlock();
        } else if (type().resultType().equals(ActionResultType.REMOVES)) {
            // If the action type removes a block, restore means we remove it again
            removeBlock();
        }
    }

    /**
     * Sets an in-world block to air.
     */
    protected void removeBlock() {
        final Block block = location.getWorld().getBlockAt(location);
        block.setType(Material.AIR);
    }

    /**
     * Sets an in-world block to this block data.
     */
    protected void setBlock() {
        final Block block = location.getWorld().getBlockAt(location);
        block.setType(material);
        block.getState().setBlockData(blockData);
    }

    @Override
    public String toString() {
        return "BlockStateAction["
            + "location=" + location + ","
            + "material=" + material + ","
            + "blockData=" + blockData + ']';
    }
}
