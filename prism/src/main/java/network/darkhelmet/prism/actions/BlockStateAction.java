package network.darkhelmet.prism.actions;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTTileEntity;

import java.util.Locale;

import network.darkhelmet.prism.api.actions.ActionResultType;
import network.darkhelmet.prism.api.actions.ActionType;
import network.darkhelmet.prism.api.actions.IBlockAction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public class BlockStateAction extends Action implements IBlockAction {
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
     * The nbt container.
     */
    private NBTContainer nbtContainer;

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

        if (blockState instanceof TileState) {
            NBTTileEntity nbtTe = new NBTTileEntity(blockState);
            this.nbtContainer = new NBTContainer(nbtTe.getCompound());
        }
    }

    /**
     * Construct a block state action.
     *
     * @param type The action type
     * @param material The material
     * @param blockData The block data
     * @param teData The tile entity data
     */
    public BlockStateAction(
        ActionType type, Location location, Material material, BlockData blockData, NBTContainer teData) {
        super(type);

        this.location = location;
        this.material = material;
        this.blockData = blockData;
        this.nbtContainer = teData;
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
    public @Nullable String serializeBlockData() {
        return this.blockData.getAsString().replaceAll("^[^\\[]+", "");
    }

    @Override
    public boolean hasCustomData() {
        return this.nbtContainer != null;
    }

    @Override
    public @Nullable String serializeCustomData() {
        if (this.nbtContainer != null) {
            return this.nbtContainer.toString();
        }

        return null;
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

        if (blockData != null) {
            block.setBlockData(blockData, true);
        }

        if (this.nbtContainer != null) {
            new NBTTileEntity(block.getState()).mergeCompound(this.nbtContainer);
        }
    }

    @Override
    public String toString() {
        return "BlockStateAction["
            + "location=" + location + ","
            + "material=" + material + ","
            + "nbtContainer=" + nbtContainer + ']';
    }
}
