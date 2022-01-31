/*
 * Prism (Refracted)
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.prism.actions;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTTileEntity;

import network.darkhelmet.prism.api.actions.IBlockAction;
import network.darkhelmet.prism.api.actions.types.ActionResultType;
import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.modifications.ModificationResult;

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
    public ModificationResult applyRollback(IActivity activityContext, boolean isPreview) {
        if (!type().reversible()) {
            return ModificationResult.SKIPPED;
        }

        if (type().resultType().equals(ActionResultType.REMOVES)) {
            // If the action type removes a block, rollback means we re-set it
            setBlock(activityContext.location());
        } else if (type().resultType().equals(ActionResultType.CREATES)) {
            // If the action type creates a block, rollback means we remove it
            removeBlock(activityContext.location());
        }

        return ModificationResult.APPLIED;
    }

    @Override
    public ModificationResult applyRestore(IActivity activityContext, boolean isPreview) {
        if (!type().reversible()) {
            return ModificationResult.SKIPPED;
        }

        if (type().resultType().equals(ActionResultType.CREATES)) {
            // If the action type creates a block, restore means we re-set it
            setBlock(activityContext.location());
        } else if (type().resultType().equals(ActionResultType.REMOVES)) {
            // If the action type removes a block, restore means we remove it again
            removeBlock(activityContext.location());
        }

        return ModificationResult.APPLIED;
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
