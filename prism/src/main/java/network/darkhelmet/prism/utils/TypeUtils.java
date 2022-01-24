package network.darkhelmet.prism.utils;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class TypeUtils {
    /**
     * Prevent instantiation.
     */
    private TypeUtils() {}

    /**
     * "Serializes" material name to string. Material is not namespaced.
     *
     * @param material The material
     * @return The string
     */
    public static String materialToString(Material material) {
        return material.toString().toLowerCase(Locale.ENGLISH);
    }

    /**
     * "Serializes" block data to a string. Returns block data only, not namespaced material.
     *
     * @param data The block data
     * @return The string
     */
    public static String blockDataToString(BlockData data) {
        return data.getAsString().replaceAll("^[^\\[]+", "");
    }

    /**
     * Converts UUID to a string ready for use against database.
     *
     * @param uuid A UUID
     * @return The encoded UUID string
     */
    public static String uuidToDbString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    /**
     * Converts UUID from a string used in the database.
     *
     * @param uuid The "encoded" UUID string
     * @return The UUID
     */
    public static UUID uuidFromDbString(String uuid) {
        // Positions need to be -2
        String completeUuid = uuid.substring(0, 8);
        completeUuid += "-" + uuid.substring(8, 12);
        completeUuid += "-" + uuid.substring(12, 16);
        completeUuid += "-" + uuid.substring(16, 20);
        completeUuid += "-" + uuid.substring(20);
        completeUuid = completeUuid.toLowerCase();
        return UUID.fromString(completeUuid);
    }
}