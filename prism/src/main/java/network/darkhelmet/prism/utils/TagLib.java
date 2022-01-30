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

import org.bukkit.Material;
import org.bukkit.Tag;

public class TagLib {
    /**
     * Prevent instantiation.
     */
    private TagLib() {}

    /**
     * All plants that have a one-block structure.
     */
    public static final MaterialTag PLANTS = new MaterialTag(
        Material.GRASS,
        Material.FERN,
        Material.DEAD_BUSH,
        Material.DANDELION,
        Material.POPPY,
        Material.BLUE_ORCHID,
        Material.ALLIUM,
        Material.AZURE_BLUET,
        Material.RED_TULIP,
        Material.ORANGE_TULIP,
        Material.WHITE_TULIP,
        Material.PINK_TULIP,
        Material.OXEYE_DAISY,
        Material.BROWN_MUSHROOM,
        Material.RED_MUSHROOM,
        Material.LILY_PAD,
        Material.KELP,
        Material.KELP_PLANT,
        Material.SWEET_BERRY_BUSH)
        .append(Tag.WALL_CORALS)
        .append(Tag.CORALS);

    /**
     * Plants that have a two-block structure.
     */
    public static final MaterialTag TALL_PLANTS = new MaterialTag(
        Material.SUNFLOWER,
        Material.LILAC,
        Material.ROSE_BUSH,
        Material.PEONY,
        Material.TALL_GRASS,
        Material.LARGE_FERN,
        Material.TALL_SEAGRASS);

    /**
     * All plants (not counting crops).
     */
    public static final MaterialTag ALL_PLANTS = new MaterialTag(PLANTS).append(TALL_PLANTS);

    /**
     * All vegetation that can be grown.
     */
    public static final MaterialTag GROWABLES = new MaterialTag(
        Material.BAMBOO,
        Material.CACTUS,
        Material.KELP,
        Material.KELP_PLANT,
        Material.SUGAR_CANE,
        Material.CHORUS_PLANT,
        Material.CHORUS_FLOWER
    ).append(Tag.CROPS);

    /**
     * All banners that are placed on the top of a block.
     */
    public static final MaterialTag TOP_BANNERS = new MaterialTag(Tag.BANNERS)
        .exclude("_WALL_", MaterialTag.MatchMode.CONTAINS);

    /**
     * Banners hung on a wall.
     */
    public static final MaterialTag WALL_BANNERS = new MaterialTag(Tag.BANNERS).exclude(TOP_BANNERS);

    /**
     * Materials that attach to any side of a block.
     */
    public static final MaterialTag DETACHABLES = new MaterialTag(
        Material.AMETHYST_CLUSTER);

    /**
     * All redstone-related items that detach when connected block is broken.
     */
    public static final MaterialTag REDSTONE_DETACHABLE = new MaterialTag(
        Material.COMPARATOR,
        Material.LEVER,
        Material.REPEATER,
        Material.REDSTONE_TORCH,
        Material.REDSTONE_WALL_TORCH,
        Material.REDSTONE_WIRE
    ).append(Tag.BUTTONS, Tag.PRESSURE_PLATES);

    /**
     * All materials that can attach to themselves on the bottom (breaks travel downward).
     */
    public static final MaterialTag RECURSIVE_BOTTOM_DETACHABLES = new MaterialTag(
        Material.CHAIN,
        Material.POINTED_DRIPSTONE,
        Material.VINE
    ).append(Tag.CAVE_VINES);

    /**
     * Materials that attach to the bottom of a block.
     */
    public static final MaterialTag BOTTOM_DETACHABLES = new MaterialTag(
        Material.SPORE_BLOSSOM,
        Material.LANTERN
    ).append(Tag.CAVE_VINES).append(DETACHABLES, RECURSIVE_BOTTOM_DETACHABLES);

    /**
     * All materials that can detach from the side of a block.
     */
    public static final MaterialTag SIDE_DETACHABLES = new MaterialTag(
        // Pistons
        Material.STICKY_PISTON,
        Material.PISTON,
        Material.PISTON_HEAD,
        Material.MOVING_PISTON,

        // Torches
        Material.WALL_TORCH,
        Material.REDSTONE_WALL_TORCH,
        Material.SOUL_WALL_TORCH,

        // Hanging
        Material.ITEM_FRAME,
        Material.PAINTING,

        // Misc
        Material.COCOA,
        Material.GLOW_LICHEN,
        Material.LEVER,
        Material.NETHER_PORTAL,
        Material.SCAFFOLDING,
        Material.TRIPWIRE_HOOK)
        .append(Tag.BUTTONS, Tag.WALL_SIGNS, Tag.CLIMBABLE)
        .append(WALL_BANNERS, DETACHABLES);

    /**
     * All materials that can attach to themselves on the top (breaks travel upward).
     */
    public static final MaterialTag RECURSIVE_TOP_DETACHABLES = new MaterialTag(
        Material.BAMBOO,
        Material.KELP,
        Material.CACTUS,
        Material.SCAFFOLDING,
        Material.SUGAR_CANE,
        Material.TWISTING_VINES,
        Material.TWISTING_VINES_PLANT,
        Material.WEEPING_VINES,
        Material.WEEPING_VINES_PLANT
    ).append(Tag.CAVE_VINES);

    /**
     * All materials that can detach from the top of a block.
     */
    public static final MaterialTag TOP_DETACHABLES = new MaterialTag(
        Material.STICKY_PISTON,
        Material.DEAD_BUSH,
        Material.PISTON,
        Material.PISTON_HEAD,
        Material.MOVING_PISTON,
        Material.TORCH,
        Material.SOUL_TORCH,
        Material.LEVER,
        Material.SNOW,
        Material.NETHER_PORTAL,
        Material.LILY_PAD,
        Material.NETHER_WART,
        Material.BEACON,
        Material.ITEM_FRAME,
        Material.LANTERN,
        Material.CHAIN,
        Material.CONDUIT,
        Material.BELL)
        .append(
            Tag.DOORS,
            Tag.RAILS,
            Tag.SAPLINGS,
            Tag.STANDING_SIGNS)
        .append(
            Tag.BUTTONS,
            Tag.CARPETS,
            Tag.FLOWER_POTS)
        .append(REDSTONE_DETACHABLE, GROWABLES, ALL_PLANTS, DETACHABLES, TOP_BANNERS, RECURSIVE_TOP_DETACHABLES);
}
