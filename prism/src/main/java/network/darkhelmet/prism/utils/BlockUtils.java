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

package network.darkhelmet.prism.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;

public class BlockUtils {
    /**
     * Prevent instantiation.
     */
    private BlockUtils() {}

    /**
     * List all *side* block faces.
     */
    private static final BlockFace[] attachmentFacesSides = {
        BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };

    /**
     * Gets the "root" block of connected block. If not a
     * double block, the passed block is returned.
     *
     * @param block Block
     */
    public static Block rootBlock(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Bed bed) {
            if (bed.getPart() == Bed.Part.HEAD) {
                return block.getRelative(bed.getFacing().getOppositeFace());
            }
        } else if (data instanceof Bisected bisected && !(data instanceof Stairs) && !(data instanceof TrapDoor)) {
            if (bisected.getHalf() == Bisected.Half.TOP) {
                return block.getRelative(BlockFace.DOWN);
            }
        }

        return block;
    }

    /**
     * Query all blocks that can detach from a given start block.
     *
     * @param startBlock The start block
     * @return A list of any detachable blocks
     */
    public static List<Block> detachables(Block startBlock) {
        List<Block> detachables = new ArrayList<>();
        detachables.addAll(sideDetachables(startBlock));
        detachables.addAll(topDetachables(startBlock));
        detachables.addAll(bottomDetachables(startBlock));

        return detachables;
    }

    /**
     * Query all "detachable" blocks on the bottom of a given block.
     *
     * @param startBlock The start block
     * @return A list of any blocks that are considered "detachable"
     */
    protected static List<Block> bottomDetachables(Block startBlock) {
        List<Block> detachables = new ArrayList<>();

        Block neighbor = startBlock.getRelative(BlockFace.DOWN);
        if (TagLib.BOTTOM_DETACHABLES.isTagged(neighbor.getType())) {
            detachables.add(neighbor);
        }

        return detachables;
    }

    /**
     * Query all "detachable" blocks on top of a given block.
     *
     * @param startBlock The start block
     * @return A list of any blocks that are considered "detachable"
     */
    protected static List<Block> topDetachables(Block startBlock) {
        List<Block> detachables = new ArrayList<>();

        Block neighbor = startBlock.getRelative(BlockFace.UP);
        if (TagLib.TOP_DETACHABLES.isTagged(neighbor.getType())) {
            detachables.add(neighbor);
        }

        return detachables;
    }

    /**
     * Query all "detachable" blocks on the sides of a given block.
     *
     * @param startBlock The start block
     * @return A list of any blocks that are considered "detachable"
     */
    protected static List<Block> sideDetachables(Block startBlock) {
        List<Block> detachables = new ArrayList<>();

        for (BlockFace face : attachmentFacesSides) {
            Block neighbor = startBlock.getRelative(face);
            if (TagLib.SIDE_DETACHABLES.isTagged(neighbor.getType())) {
                detachables.add(neighbor);
            }
        }

        return detachables;
    }
}
