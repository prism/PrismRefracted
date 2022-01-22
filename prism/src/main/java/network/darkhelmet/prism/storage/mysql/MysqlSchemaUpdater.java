package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;

import java.sql.SQLException;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.utils.TypeUtils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.intellij.lang.annotations.Language;

public class MysqlSchemaUpdater {
    /**
     * Updates schema from 8 (Prism 2.x & 3.x) to v4 (4.x).
     *
     * @param storageConfig The storage config
     * @throws SQLException The database exception
     */
    public static void update_8_to_v4(
        StorageConfiguration storageConfig) throws SQLException {
        // Add world_uuid column (but allow nulls, as no values exist
        @Language("SQL") String updateWorlds = "ALTER TABLE `" + storageConfig.prefix() + "worlds`"
            + "ADD COLUMN `world_uuid` BINARY(16) NULL AFTER `world`,"
            + "ADD UNIQUE INDEX `world_uuid_UNIQUE` (`world_uuid` ASC);";
        DB.executeUpdate(updateWorlds);

        // Update schema with world UUIDs
        for (World world : Bukkit.getServer().getWorlds()) {
            @Language("SQL") String sql = "UPDATE `" + storageConfig.prefix() + "worlds`"
                + "SET world_uuid = UNHEX(?) WHERE world = ?";

            String worldUid = TypeUtils.uuidToDbString(world.getUID());
            DB.executeInsert(sql, worldUid, world.getName());
        }

        // Delete worlds without a UUID
        @Language("SQL") String deleteWorlds = "DELETE FROM`" + storageConfig.prefix() + "worlds`"
            + "WHERE world_uuid IS NULL";
        int deletions = DB.executeUpdate(deleteWorlds);

        String worldMsg = String.format("Deleted %d worlds from the database that are no longer present.", deletions);
        Prism.getInstance().log(worldMsg);

        // Drop world name and make uuid non-null
        @Language("SQL") String removeWorldNames = "ALTER TABLE `" + storageConfig.prefix() + "worlds`"
            + "DROP COLUMN `world`,"
            + "CHANGE COLUMN `world_uuid` `world_uuid` BINARY(16) NOT NULL,"
            + "DROP INDEX `world`;";
        DB.executeUpdate(removeWorldNames);

        // Update the schema version
        @Language("SQL") String updateSchema = "UPDATE " + storageConfig.prefix() + "meta "
            + "SET v = 'v4' WHERE k = 'schema_ver'";
        DB.executeUpdate(updateSchema);

        Prism.getInstance().log("Updated database schema to version: v4");
    }
}
