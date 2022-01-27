package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import co.aikar.idb.PooledDatabaseOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.actions.ActionData;
import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.utils.TypeUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.intellij.lang.annotations.Language;

public class MysqlStorageAdapter implements IStorageAdapter {
    /**
     * The storage configuration.
     */
    protected StorageConfiguration storageConfig;

    /**
     * Toggle whether this storage system is enabled and ready.
     */
    protected boolean ready = false;

    /**
     * Construct a new instance.
     *
     * @param storageConfiguration The storage configuration
     */
    public MysqlStorageAdapter(StorageConfiguration storageConfiguration) {
        this.storageConfig = storageConfiguration;

        try {
            DatabaseOptions options = DatabaseOptions.builder().mysql(
                storageConfig.username(),
                storageConfig.password(),
                storageConfig.database(),
                storageConfig.host() + ":" + storageConfig.port()).build();
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
        Prism.getInstance().log(versionMsg);

        long innodbSizeMb = Long.parseLong(dbInfo.get("innodb_buffer_pool_size")) / 1024 / 1024;
        Prism.getInstance().log(String.format("innodb_buffer_pool_size: %d", innodbSizeMb));
        Prism.getInstance().log(String.format("sql_mode: %s", dbInfo.get("sql_mode")));
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
        @Language("SQL") String createCauses = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "causes` ("
            + "`cause_id` int unsigned NOT NULL AUTO_INCREMENT,"
            + "`cause` varchar(25) NOT NULL,"
            + "`player_id` int NULL,"
            + "PRIMARY KEY (`cause_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(createCauses);

        // Look for existing tables first.
        List<String> tables = DB.getFirstColumnResults("SHOW TABLES LIKE ?",
            storageConfig.prefix() + "%");
        if (tables.contains(storageConfig.prefix() + "meta")) {
            // Check existing schema version before we do anything.
            // We can't create tables if existing ones are
            // going to be renamed during an update phase.
            // We'd run into collisions
            @Language("SQL") String sql = "SELECT v FROM " + storageConfig.prefix() + "meta WHERE k = 'schema_ver'";

            String schemaVersion = DB.getFirstColumn(sql);
            Prism.getInstance().log(String.format("Prism database version: %s", schemaVersion));

            updateSchemas(schemaVersion);
        }

        // Create actions table
        @Language("SQL") String actionsQuery = "CREATE TABLE IF NOT EXISTS `"
            + storageConfig.prefix() + "actions` ("
            + "`action_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,"
            + "`action` varchar(25) NOT NULL,"
            + "PRIMARY KEY (`action_id`), UNIQUE KEY `action` (`action`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(actionsQuery);

        // Create the activities table. This one's the fatso.
        @Language("SQL") String activitiesQuery = "CREATE TABLE IF NOT EXISTS `"
            + storageConfig.prefix() + "activities` ("
            + "`activity_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
            + "`timestamp` int(10) unsigned NOT NULL,"
            + "`world_id` tinyint(3) unsigned NOT NULL,"
            + "`x` int(11) NOT NULL,"
            + "`y` int(11) NOT NULL,"
            + "`z` int(11) NOT NULL,"
            + "`action_id` tinyint(3) unsigned NOT NULL,"
            + "`material_id` mediumint(9) DEFAULT NULL,"
            + "`old_material_id` mediumint(9) DEFAULT NULL,"
            + "`cause_id` int(11) NOT NULL,"
            + "PRIMARY KEY (`activity_id`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(activitiesQuery);

        // Create the custom data table
        @Language("SQL") String extraQuery = "CREATE TABLE IF NOT EXISTS `"
            + storageConfig.prefix() + "activities_custom_data` ("
            + "`extra_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
            + "`activity_id` int(10) unsigned NOT NULL,"
            + "`data` text,"
            + "PRIMARY KEY (`extra_id`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(extraQuery);

        // Create the material data table
        @Language("SQL") String matDataQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "material_data` ("
            + "`material_id` smallint(6) NOT NULL AUTO_INCREMENT,"
            + "`material` varchar(45) DEFAULT NULL,"
            + "`data` varchar(155) DEFAULT NULL,"
            + "PRIMARY KEY (`material_id`),"
            + "UNIQUE KEY `materialdata` (`material`,`data`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(matDataQuery);

        // Create the meta data table
        @Language("SQL") String metaQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "meta` ("
            + "`meta_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,"
            + "`k` varchar(25) NOT NULL,"
            + "`v` varchar(155) NOT NULL,"
            + "PRIMARY KEY (`meta_id`),"
            + "UNIQUE KEY `k_UNIQUE` (`k`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(metaQuery);

        // Create the players table
        @Language("SQL") String playersQuery = "CREATE TABLE IF NOT EXISTS `"
            + storageConfig.prefix() + "players` ("
            + "`player_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
            + "`player` varchar(16) NOT NULL,"
            + "`player_uuid` binary(16) NOT NULL,"
            + "PRIMARY KEY (`player_id`),"
            + "UNIQUE KEY `player_uuid` (`player_uuid`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(playersQuery);

        // Create worlds table
        @Language("SQL") String worldsQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "worlds` ("
            + "`world_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,"
            + "`world` varchar(255) NOT NULL,"
            + "`world_uuid` binary(16) NOT NULL,"
            + "PRIMARY KEY (`world_id`),"
            + "UNIQUE KEY `world_uuid_UNIQUE` (`world_uuid`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        DB.executeUpdate(worldsQuery);

        // Insert the schema version
        @Language("SQL") String setSchemaVer = "INSERT INTO `" + storageConfig.prefix() + "meta` "
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
            DB.createTransaction(stm -> MysqlSchemaUpdater.update_8_to_v4(storageConfig));
        }
    }

    @Override
    public PaginatedResults<IActivity> queryActivitiesPaginated(ActivityQuery query) throws SQLException {
        List<IActivity> results = activityMapper(MysqlQueryBuilder.queryActivities(query, storageConfig.prefix()));

        return new PaginatedResults<>(results);
    }

    @Override
    public List<IActivity> queryActivities(ActivityQuery query) throws SQLException {
        return activityMapper(MysqlQueryBuilder.queryActivities(query, storageConfig.prefix()));
    }

    /**
     * Maps activity data to an action and IActivity.
     *
     * @param results The results
     * @return The activity list
     */
    protected List<IActivity> activityMapper(List<DbRow> results) {
        List<IActivity> activities = new ArrayList<>();

        for (DbRow row : results) {
            String actionKey = row.getString("action");
            Optional<ActionType> optionalActionType = Prism.getInstance().actionRegistry().getActionType(actionKey);
            if (optionalActionType.isEmpty()) {
                String msg = "Failed to find action type. Type: %s";
                Prism.getInstance().error(String.format(msg, actionKey));
                continue;
            }

            ActionType actionType = optionalActionType.get();
            if (!actionType.reversible()) {
                // Skip because this action type is not reversible
                continue;
            }

            // World
            UUID worldUuid = TypeUtils.uuidFromDbString(row.getString("worldUuid"));
            World world = Bukkit.getServer().getWorld(worldUuid);
            if (world == null) {
                String msg = "Failed to find game world for activity query. World UUID: %s";
                Prism.getInstance().error(String.format(msg, worldUuid));
                continue;
            }

            // Location
            int x = row.getInt("x");
            int y = row.getInt("y");
            int z = row.getInt("z");
            Location location = new Location(world, x, y, z);

            // Material/serialization data
            Material material = null;
            String materialName = row.getString("material");
            if (materialName != null) {
                material = Material.valueOf(materialName.toUpperCase(Locale.ENGLISH));
            }

            String materialData = row.getString("material_data");
            String customData = row.getString("custom_data");

            // Cause/player
            Object cause = row.getString("cause");
            if (row.getString("playerUuid") != null) {
                UUID playerUuid = TypeUtils.uuidFromDbString(row.getString("playerUuid"));
                cause = Bukkit.getOfflinePlayer(playerUuid);
            }

            long timestamp = row.getLong("timestamp");

            // Build the action data
            ActionData actionData = new ActionData(material, materialName, materialData, customData);

            // Build the activity
            IActivity activity = Activity.builder()
                .action(actionType.createAction(actionData))
                .timestamp(timestamp).cause(cause).location(location).build();

            // Add to result list
            activities.add(activity);
        }

        return activities;
    }

    @Override
    public IActivityBatch createActivityBatch() {
        return new MysqlActivityBatch(storageConfig);
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
