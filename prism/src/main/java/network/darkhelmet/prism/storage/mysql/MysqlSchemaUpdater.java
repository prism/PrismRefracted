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
        // Drop block subid column from id mapping. What a relic.
        @Language("SQL") String updateIdMap = "ALTER TABLE `" + storageConfig.prefix() + "id_map`"
            + "DROP COLUMN `block_subid`;";
        DB.executeUpdate(updateIdMap);

        // Drop block subid column from data. What a relic.
        @Language("SQL") String updateData = "ALTER TABLE `" + storageConfig.prefix() + "data`"
            + "DROP COLUMN `block_subid`,"
            + "DROP COLUMN `old_block_subid`;";
        DB.executeUpdate(updateData);

        // Rename id map table
        @Language("SQL") String renameId = "ALTER TABLE `" + storageConfig.prefix() + "id_map` "
            + "RENAME TO `" + storageConfig.prefix() + "material_data`";
        DB.executeUpdate(renameId);

        // Change material data schema
        @Language("SQL") String materialSchema = "ALTER TABLE `" + storageConfig.prefix() + "material_data` "
            + "CHANGE COLUMN `block_id` `material_id` MEDIUMINT(5) NOT NULL AUTO_INCREMENT FIRST,"
            + "CHANGE COLUMN `state` `data` VARCHAR(255) NULL,"
            + "DROP PRIMARY KEY,"
            + "ADD PRIMARY KEY (`material_id`),"
            + "ADD UNIQUE INDEX `materialdata` (`material` ASC, `data` ASC);";
        DB.executeUpdate(materialSchema);

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

        // Make uuid non-null
        @Language("SQL") String removeWorldNames = "ALTER TABLE `" + storageConfig.prefix() + "worlds`"
            + "CHANGE COLUMN `world_uuid` `world_uuid` BINARY(16) NOT NULL;";
        DB.executeUpdate(removeWorldNames);

        // Update the schema version
        @Language("SQL") String updateSchema = "UPDATE " + storageConfig.prefix() + "meta "
            + "SET v = 'v4' WHERE k = 'schema_ver'";
        DB.executeUpdate(updateSchema);

        Prism.getInstance().log("Updated database schema to version: v4");
    }
}
