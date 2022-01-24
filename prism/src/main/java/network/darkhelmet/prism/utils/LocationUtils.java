package network.darkhelmet.prism.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtils {
    /**
     * Prevent instantiation.
     */
    private LocationUtils() {}

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
