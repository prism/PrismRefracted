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

package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import co.aikar.idb.PooledDatabaseOptions;

import com.google.inject.Inject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.actions.ActionData;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.actions.types.IActionType;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.GroupedActivity;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.utils.TypeUtils;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.intellij.lang.annotations.Language;

public class MysqlStorageAdapter implements IStorageAdapter {
    /**
     * The logger.
     */
    private final Logger logger;

    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The action registry.
     */
    protected final IActionRegistry actionRegistry;

    /**
     * The schema updater.
     */
    private final MysqlSchemaUpdater schemaUpdater;

    /**
     * The query builder.
     */
    private final MysqlQueryBuilder queryBuilder;

    /**
     * Toggle whether this storage system is enabled and ready.
     */
    protected boolean ready = false;

    /**
     * Construct a new instance.
     *
     * @param configurationService The configuration service
     */
    @Inject
    public MysqlStorageAdapter(
            Logger logger,
            ConfigurationService configurationService,
            IActionRegistry actionRegistry,
            MysqlSchemaUpdater schemaUpdater,
            MysqlQueryBuilder queryBuilder) {
        this.logger = logger;
        this.configurationService = configurationService;
        this.actionRegistry = actionRegistry;
        this.schemaUpdater = schemaUpdater;
        this.queryBuilder = queryBuilder;

        try {
            DatabaseOptions options = DatabaseOptions.builder().mysql(
                configurationService.storageConfig().username(),
                configurationService.storageConfig().password(),
                configurationService.storageConfig().database(),
                configurationService.storageConfig().host() + ":"
                        + configurationService.storageConfig().port()).build();
            Database db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
            DB.setGlobalDatabase(db);

            describeDatabase();
            prepareSchema();

            ready = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detect version and other information regarding the database.
     *
     * @throws SQLException The database exception
     */
    protected void describeDatabase() throws SQLException {
        Map<String, String> dbInfo = new HashMap<>();

        for (DbRow row : DB.getResults("SHOW VARIABLES")) {
            dbInfo.put(row.getString("Variable_name").toLowerCase(), row.getString("Value"));
        }

        String version = dbInfo.get("version");
        String versionComment = dbInfo.get("version_comment");
        String versionMsg = String.format("Database version: %s / %s", version, versionComment);
        logger.info(versionMsg);

        long innodbSizeMb = Long.parseLong(dbInfo.get("innodb_buffer_pool_size")) / 1024 / 1024;
        logger.info(String.format("innodb_buffer_pool_size: %d", innodbSizeMb));
        logger.info(String.format("sql_mode: %s", dbInfo.get("sql_mode")));
    }

    /**
     * Create tables.
     *
     * @throws SQLException The database exception
     */
    protected void prepareSchema() throws SQLException {
        // Create causes table. This is done here because:
        // 1. We need it for new installs anyway
        // 2. Updater logic needs it for 8->v4
        @Language("SQL") String createCauses = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "causes` ("
            + "`cause_id` int unsigned NOT NULL AUTO_INCREMENT,"
            + "`cause` varchar(25) NOT NULL,"
            + "`player_id` int NULL,"
            + "PRIMARY KEY (`cause_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(createCauses);

        // Look for existing tables first.
        List<String> tables = DB.getFirstColumnResults("SHOW TABLES LIKE ?",
            configurationService.storageConfig().prefix() + "%");
        if (tables.contains(configurationService.storageConfig().prefix() + "meta")) {
            // Check existing schema version before we do anything.
            // We can't create tables if existing ones are
            // going to be renamed during an update phase.
            // We'd run into collisions
            @Language("SQL") String sql = "SELECT v FROM " + configurationService.storageConfig().prefix()
                + "meta WHERE k = 'schema_ver'";

            String schemaVersion = DB.getFirstColumn(sql);
            logger.info(String.format("Prism database version: %s", schemaVersion));

            updateSchemas(schemaVersion);
        }

        // Create actions table
        @Language("SQL") String actionsQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "actions` ("
            + "`action_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,"
            + "`action` varchar(25) NOT NULL,"
            + "PRIMARY KEY (`action_id`), UNIQUE KEY `action` (`action`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(actionsQuery);

        // Create the activities table. This one's the fatso.
        @Language("SQL") String activitiesQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "activities` ("
            + "`activity_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
            + "`timestamp` int(10) unsigned NOT NULL,"
            + "`world_id` tinyint(3) unsigned NOT NULL,"
            + "`x` int(11) NOT NULL,"
            + "`y` int(11) NOT NULL,"
            + "`z` int(11) NOT NULL,"
            + "`action_id` tinyint(3) unsigned NOT NULL,"
            + "`material_id` smallint(6) unsigned DEFAULT NULL,"
            + "`old_material_id` smallint(6) unsigned DEFAULT NULL,"
            + "`entity_type_id` smallint(6) unsigned NULL,"
            + "`cause_id` int(11) NOT NULL,"
            + "PRIMARY KEY (`activity_id`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(activitiesQuery);

        // Create the custom data table
        @Language("SQL") String extraQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "activities_custom_data` ("
            + "`extra_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
            + "`activity_id` int(10) unsigned NOT NULL,"
            + "`version` SMALLINT NULL,"
            + "`data` text,"
            + "PRIMARY KEY (`extra_id`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(extraQuery);

        // Create the entity types table
        @Language("SQL") String entityTypeQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "entity_types` ("
            + "`entity_type_id` smallint(6) NOT NULL AUTO_INCREMENT,"
            + "`entity_type` varchar(45) DEFAULT NULL,"
            + "PRIMARY KEY (`entity_type_id`),"
            + "UNIQUE KEY `entity_type` (`entity_type`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(entityTypeQuery);

        // Create the material data table
        @Language("SQL") String matDataQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "material_data` ("
            + "`material_id` smallint(6) NOT NULL AUTO_INCREMENT,"
            + "`material` varchar(45) DEFAULT NULL,"
            + "`data` varchar(155) DEFAULT NULL,"
            + "PRIMARY KEY (`material_id`),"
            + "UNIQUE KEY `materialdata` (`material`,`data`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(matDataQuery);

        // Create the meta data table
        @Language("SQL") String metaQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "meta` ("
            + "`meta_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,"
            + "`k` varchar(25) NOT NULL,"
            + "`v` varchar(155) NOT NULL,"
            + "PRIMARY KEY (`meta_id`),"
            + "UNIQUE KEY `k` (`k`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(metaQuery);

        // Create the players table
        @Language("SQL") String playersQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "players` ("
            + "`player_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
            + "`player` varchar(16) NOT NULL,"
            + "`player_uuid` binary(16) NOT NULL,"
            + "PRIMARY KEY (`player_id`),"
            + "UNIQUE KEY `player_uuid` (`player_uuid`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(playersQuery);

        // Create worlds table
        @Language("SQL") String worldsQuery = "CREATE TABLE IF NOT EXISTS `"
            + configurationService.storageConfig().prefix() + "worlds` ("
            + "`world_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,"
            + "`world` varchar(255) NOT NULL,"
            + "`world_uuid` binary(16) NOT NULL,"
            + "PRIMARY KEY (`world_id`),"
            + "UNIQUE KEY `world_uuid` (`world_uuid`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(worldsQuery);

        // Insert the schema version
        @Language("SQL") String setSchemaVer = "INSERT INTO `"
            + configurationService.storageConfig().prefix() + "meta` "
            + " (`k`, `v`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `v` = `v`";
        DB.executeInsert(setSchemaVer, "schema_ver", "v4");
    }

    /**
     * Update the schema as needed.
     *
     * @throws SQLException The database exception
     */
    protected void updateSchemas(String schemaVersion) throws SQLException {
        // Update: 8 -> v4
        if (schemaVersion.equalsIgnoreCase("8")) {
            DB.createTransaction(stm -> schemaUpdater.update_8_to_v4(configurationService.storageConfig()));
        }
    }

    @Override
    public PaginatedResults<IActivity> queryActivitiesAsInformation(ActivityQuery query) throws SQLException {
        List<DbRow> rows = queryBuilder.queryActivities(query, configurationService.storageConfig().prefix());

        int totalResults = 0;
        if (!rows.isEmpty()) {
            totalResults = rows.get(0).getInt("totalRows");
        }

        int currentPage = (query.offset() / query.limit()) + 1;

        return new PaginatedResults<>(activityMapper(rows, query), query.limit(), totalResults, currentPage);
    }

    @Override
    public List<IActivity> queryActivitiesAsModification(ActivityQuery query) throws SQLException {
        List<DbRow> results = queryBuilder.queryActivities(query, configurationService.storageConfig().prefix());
        return activityMapper(results, query);
    }

    /**
     * Maps activity data to an action and IActivity.
     *
     * @param results The results
     * @param query The original query
     * @return The activity list
     */
    protected List<IActivity> activityMapper(List<DbRow> results, ActivityQuery query) {
        List<IActivity> activities = new ArrayList<>();

        for (DbRow row : results) {
            String actionKey = row.getString("action");
            Optional<IActionType> optionalActionType = actionRegistry.getActionType(actionKey);
            if (optionalActionType.isEmpty()) {
                String msg = "Failed to find action type. Type: %s";
                logger.warn(String.format(msg, actionKey));
                continue;
            }

            IActionType actionType = optionalActionType.get();

            // World
            UUID worldUuid = TypeUtils.uuidFromDbString(row.getString("worldUuid"));
            World world = Bukkit.getServer().getWorld(worldUuid);
            if (world == null) {
                String msg = "Failed to find game world for activity query. World UUID: %s";
                logger.warn(String.format(msg, worldUuid));
                continue;
            }

            // Location (average location for grouped)
            int x = row.getInt("x");
            int y = row.getInt("y");
            int z = row.getInt("z");
            Location location = new Location(world, x, y, z);

            // Entity type
            EntityType entityType = null;
            String entityTypeName = row.getString("entity_type");
            if (entityTypeName != null) {
                entityType = EntityType.valueOf(entityTypeName.toUpperCase(Locale.ENGLISH));
            }

            // Material/serialization data
            Material material = null;
            String materialName = row.getString("material");
            if (materialName != null) {
                material = Material.valueOf(materialName.toUpperCase(Locale.ENGLISH));
            }

            // Cause/player
            Object cause = row.getString("cause");
            if (row.getString("playerUuid") != null) {
                UUID playerUuid = TypeUtils.uuidFromDbString(row.getString("playerUuid"));
                cause = Bukkit.getOfflinePlayer(playerUuid);
            }

            if (!query.grouped()) {
                long timestamp = row.get("timestamp");
                String materialData = row.getString("material_data");
                String customData = row.getString("custom_data");
                Integer customDataVersion = row.getInt("data_version");
                short version = customDataVersion.shortValue();

                // Material/serialization data
                Material replacedMaterial = null;
                String replacedMaterialName = row.getString("old_material");
                if (replacedMaterialName != null) {
                    replacedMaterial = Material.valueOf(replacedMaterialName.toUpperCase(Locale.ENGLISH));
                }

                String replacedMaterialData = row.getString("old_material_data");

                // Build the action data
                ActionData actionData = new ActionData(
                    material, materialData, replacedMaterial, replacedMaterialData, entityType, customData, version);

                // Build the activity
                IActivity activity = new Activity(actionType.createAction(actionData), location, cause, timestamp);

                // Add to result list
                activities.add(activity);
            } else {
                // Build the action data
                ActionData actionData = new ActionData(
                    material, null, null, null, entityType, null, (short) 0);

                // Count
                int count = row.getInt("groupCount");

                // Timestamp
                BigDecimal timestamp = row.get("timestamp");

                // Build the grouped activity
                IActivity activity = new GroupedActivity(
                    actionType.createAction(actionData), location, cause, timestamp.longValue(), count);

                // Add to result list
                activities.add(activity);
            }
        }

        return activities;
    }

    @Override
    public IActivityBatch createActivityBatch() {
        return new MysqlActivityBatch(configurationService.storageConfig());
    }

    @Override
    public void close() {
        DB.close();
    }

    @Override
    public boolean ready() {
        return ready;
    }
}