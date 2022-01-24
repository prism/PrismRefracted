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
     * Prevent instantiation.
     */
    private MysqlSchemaUpdater() {}

    /**
     * Updates schema from 8 (Prism 2.x & 3.x) to v4 (4.x).
     *
     * @param storageConfig The storage config
     * @throws SQLException The database exception
     */
    public static void update_8_to_v4(
        StorageConfiguration storageConfig) throws SQLException {
        // -------------
        // ACTIONS TABLE
        // -------------

        // Decrease the size of the action_id col
        @Language("SQL") String actionPk = "ALTER TABLE `" + storageConfig.prefix() + "actions`"
            + "CHANGE COLUMN `action_id` `action_id` TINYINT UNSIGNED NOT NULL AUTO_INCREMENT ";
        DB.executeUpdate(actionPk);

        // ----------------
        // DATA/EXTRA TABLE
        // ----------------

        // Rename data table
        @Language("SQL") String renameData = "ALTER TABLE `" + storageConfig.prefix() + "data` "
            + "RENAME TO `" + storageConfig.prefix() + "activities`";
        DB.executeUpdate(renameData);

        // Drop foreign key in extra table
        @Language("SQL") String dropExtraFk = "ALTER TABLE `" + storageConfig.prefix() + "data_extra` "
            + "DROP FOREIGN KEY `" + storageConfig.prefix() + "data_extra_ibfk_1`;";
        DB.executeUpdate(dropExtraFk);

        // Update data table (don't drop player_id yet)
        @Language("SQL") String updateData = "ALTER TABLE `" + storageConfig.prefix() + "activities`"
            + "DROP COLUMN `block_subid`,"
            + "DROP COLUMN `old_block_subid`,"
            + "CHANGE COLUMN `action_id` `action_id` TINYINT UNSIGNED NOT NULL AFTER `z`,"
            + "CHANGE COLUMN `id` `activty_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,"
            + "CHANGE COLUMN `epoch` `timestamp` INT UNSIGNED NOT NULL,"
            + "CHANGE COLUMN `world_id` `world_id` TINYINT UNSIGNED NOT NULL ,"
            + "CHANGE COLUMN `block_id` `material_id` MEDIUMINT NULL,"
            + "CHANGE COLUMN `old_block_id` `old_material_id` MEDIUMINT NULL DEFAULT NULL,"
            + "CHANGE COLUMN `player_id` `player_id` INT UNSIGNED NOT NULL AFTER `old_material_id`,"
            + "ADD COLUMN `cause_id` INT NOT NULL AFTER `player_id`;";
        DB.executeUpdate(updateData);

        // ------------
        // ID MAP TABLE
        // ------------

        // Drop block subid column from id mapping. What a relic.
        @Language("SQL") String updateIdMap = "ALTER TABLE `" + storageConfig.prefix() + "id_map`"
            + "DROP COLUMN `block_subid`;";
        DB.executeUpdate(updateIdMap);

        // Rename id map table
        @Language("SQL") String renameId = "ALTER TABLE `" + storageConfig.prefix() + "id_map` "
            + "RENAME TO `" + storageConfig.prefix() + "material_data`";
        DB.executeUpdate(renameId);

        // Change material data schema
        @Language("SQL") String materialSchema = "ALTER TABLE `" + storageConfig.prefix() + "material_data` "
            + "CHANGE COLUMN `block_id` `material_id` SMALLINT NOT NULL AUTO_INCREMENT FIRST,"
            + "CHANGE COLUMN `material` `material` VARCHAR(45) NULL,"
            + "CHANGE COLUMN `state` `data` VARCHAR(155) NULL,"
            + "DROP PRIMARY KEY,"
            + "ADD PRIMARY KEY (`material_id`),"
            + "ADD UNIQUE INDEX `materialdata` (`material` ASC, `data` ASC);";
        DB.executeUpdate(materialSchema);

        // ----------
        // META TABLE
        // ----------

        // Change material data schema
        @Language("SQL") String metaSchema = "ALTER TABLE `" + storageConfig.prefix() + "meta` "
            + "CHANGE COLUMN `id` `id` TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,"
            + "ADD UNIQUE INDEX `k_UNIQUE` (`k` ASC);";
        DB.executeUpdate(metaSchema);

        // -------------
        // PLAYERS TABLE
        // -------------

        // Populate the causes table. Prism <= v3 treated non-players as fake players and there's
        // sadly no way to separate them here. Clean databases on v4 will have them separated.
        // there's some janky fake-player stuff that exceeds the 16 char player names, we have to ignore it.
        @Language("SQL") String causesPopulator = "INSERT INTO `" + storageConfig.prefix() + "causes` "
            + "(cause, player_id) SELECT player, player_id FROM `" + storageConfig.prefix() + "players` "
            + "WHERE LENGTH(player) <= 16";
        DB.executeUpdate(causesPopulator);

        // Convert activities table player_ids into cause_ids
        @Language("SQL") String causeIdPopulator = "UPDATE `" + storageConfig.prefix() + "activities` "
            + "SET cause_id = (SELECT cause_id FROM prism_causes "
            + "WHERE prism_causes.player_id = prism_activities.player_id);";
        DB.executeUpdate(causeIdPopulator);

        // Drop player_id col from activities
        @Language("SQL") String dropPlayerId = "ALTER TABLE `" + storageConfig.prefix() + "activities`"
            + "DROP COLUMN `player_id`;";
        DB.executeUpdate(dropPlayerId);

        // ------------
        // WORLDS TABLE
        // ------------

        // Add world_uuid column (but allow nulls, as no values exist
        @Language("SQL") String updateWorlds = "ALTER TABLE `" + storageConfig.prefix() + "worlds`"
            + "CHANGE COLUMN `world_id` `world_id` TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,"
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

        // --------------
        // SCHEMA VERSION
        // --------------

        // Update the schema version
        @Language("SQL") String updateSchema = "UPDATE " + storageConfig.prefix() + "meta "
            + "SET v = 'v4' WHERE k = 'schema_ver'";
        DB.executeUpdate(updateSchema);

        Prism.getInstance().log("Updated database schema to version: v4");
    }
}
