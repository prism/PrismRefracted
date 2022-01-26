package network.darkhelmet.prism.actions;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTTileEntity;

import network.darkhelmet.prism.api.actions.IBlockAction;
import network.darkhelmet.prism.api.actions.types.ActionResultType;
import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.IActivity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public class BlockStateAction extends MaterialAction implements IBlockAction {
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
        super(type, blockState.getType());

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
    public BlockStateAction(ActionType type, Material material, BlockData blockData, NBTContainer teData) {
        super(type, material);

        this.blockData = blockData;
        this.nbtContainer = teData;
    }

    @Override
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
    public void applyRollback(IActivity activityContext) {
        if (!type().reversible()) {
            return;
        }

        if (type().resultType().equals(ActionResultType.REMOVES)) {
            // If the action type removes a block, rollback means we re-set it
            setBlock(activityContext.location());
        } else if (type().resultType().equals(ActionResultType.CREATES)) {
            // If the action type creates a block, rollback means we remove it
            removeBlock(activityContext.location());
        }
    }

    @Override
    public void applyRestore(IActivity activityContext) {
        if (!type().reversible()) {
            return;
        }

        if (type().resultType().equals(ActionResultType.CREATES)) {
            // If the action type creates a block, restore means we re-set it
            setBlock(activityContext.location());
        } else if (type().resultType().equals(ActionResultType.REMOVES)) {
            // If the action type removes a block, restore means we remove it again
            removeBlock(activityContext.location());
        }
    }

    /**
     * Sets an in-world block to air.
     */
    protected void removeBlock(Location location) {
        final Block block = location.getWorld().getBlockAt(location);
        block.setType(Material.AIR);
    }

    /**
     * Sets an in-world block to this block data.
     */
    protected void setBlock(Location location) {
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
            + "material=" + material + ","
            + "nbtContainer=" + nbtContainer + ']';
    }
}
