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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityUtils {
    /**
     * Prevent instantiation.
     */
    private EntityUtils() {}

    /**
     * Gets hanging entities within a given range of a starting location.
     *
     * @param startLoc The start location
     * @param range The range
     * @return A list of all hanging entities
     */
    public static List<Entity> hangingEntities(final Location startLoc, int range) {
        return Arrays.stream(startLoc.getChunk().getEntities()).filter(entity -> {
            if (isHanging(entity.getType()) && startLoc.getWorld().equals(entity.getWorld())) {
                return startLoc.distance(entity.getLocation()) < range;
            }

            return false;
        }).collect(Collectors.toList());
    }

    /**
     * Checks if an entity type is a hanging entity.
     *
     * @param entityType The entity type
     * @return True if entity type is hanging
     */
    protected static boolean isHanging(EntityType entityType) {
        return entityType.equals(EntityType.ITEM_FRAME)
                || entityType.equals(EntityType.GLOW_ITEM_FRAME)
                || entityType.equals(EntityType.PAINTING);
    }
}
