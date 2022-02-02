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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtils {
    /**
     * Prevent instantiation.
     */
    private LocationUtils() {}

    /**
     * Returns the minimum vector for the chunk.
     *
     * @param chunk The chunk
     * @return The minimum vector
     */
    public static Vector getChunkMinVector(Chunk chunk) {
        int blockMinX = chunk.getX() * 16;
        int blockMinZ = chunk.getZ() * 16;
        return new Vector(blockMinX, 0, blockMinZ);
    }

    /**
     * Returns the maximum vector for the chunk.
     *
     * @param chunk The chunk
     * @return The maximum vector
     */
    public static Vector getChunkMaxVector(Chunk chunk) {
        int blockMinX = chunk.getX() * 16;
        int blockMinZ = chunk.getZ() * 16;
        int blockMaxX = blockMinX + 15;
        int blockMaxZ = blockMinZ + 15;
        return new Vector(blockMaxX, chunk.getWorld().getMaxHeight(), blockMaxZ);
    }

    /**
     * Get a max vector from a location/radius. This is the max corner of a bounding box.
     *
     * @param location The center location
     * @param radius The radius (in blocks)
     * @return The vector
     */
    public static Vector getMaxVector(Location location, int radius) {
        return new Vector(location.getX() + radius, location.getY() + radius, location.getZ() + radius);
    }

    /**
     * Get a min vector from a location/radius. This is the min corner of a bounding box.
     *
     * @param location The center location
     * @param radius The radius (in blocks)
     * @return The vector
     */
    public static Vector getMinVector(Location location, int radius) {
        return new Vector(location.getX() - radius, location.getY() - radius, location.getZ() - radius);
    }
}
