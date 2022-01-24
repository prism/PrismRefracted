package network.darkhelmet.prism.utils;

import java.util.UUID;

public class TypeUtils {
    /**
     * Prevent instantiation.
     */
    private TypeUtils() {}

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