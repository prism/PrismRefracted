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
import network.darkhelmet.prism.actions.BlockStateAction;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.actions.ActionType;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.api.storage.models.ActivityRow;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.utils.TypeUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
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
        String msg = String.format("Database version: %s / %s", version, versionComment);
        Prism.getInstance().log(msg);
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
            + "`cause` varchar(16) NOT NULL,"
            + "`player_id` int NULL,"
            + "PRIMARY KEY (`cause_id`),"
            + "UNIQUE KEY `cause` (`cause`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
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
    }

    /**
     * Update the schema as needed.
     *
     * @throws SQLException The database exception
     */
    protected void updateSchemas(String schemaVersion) throws SQLException {
        // Update: 8 -> v4
        if (schemaVersion.equalsIgnoreCase("8")) {
            MysqlSchemaUpdater.update_8_to_v4(storageConfig);
        }
    }

    @Override
    public PaginatedResults<ActivityRow> queryActivities(ActivityQuery query) throws SQLException {
        List<ActivityRow> results = new ArrayList<>();

        for (DbRow row : MysqlQueryBuilder.queryActivities(query, storageConfig.prefix())) {
            String action = row.getString("action");
            String cause = row.getString("cause");
            String material = row.getString("material");
            int timestamp = row.getInt("timestamp");

            ActivityRow activity =  new ActivityRow(action, cause, timestamp, material);
            results.add(activity);
        }

        return new PaginatedResults<>(results);
    }

    @Override
    public List<IAction> queryActivitiesAsActions(ActivityQuery query) throws SQLException {
        List<IAction> results = new ArrayList<>();

        for (DbRow row : MysqlQueryBuilder.queryActivities(query, storageConfig.prefix())) {
            String actionKey = row.getString("action");
            Optional<ActionType> optionalActionType = Prism.getInstance().actionRegistry().getActionType(actionKey);
            if (optionalActionType.isEmpty()) {
                String msg = "Failed to find action type. Type: %s";
                Prism.getInstance().error(String.format(msg, actionKey));
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

            String materialName = row.getString("material");
            if (materialName != null) {
                Material material = Material.valueOf(materialName.toUpperCase(Locale.ENGLISH));
                String customData = row.getString("custom_data");

                BlockData blockData = null;
                String materialData = row.getString("material_data");
                if (materialData != null) {
                    blockData = Bukkit.createBlockData(materialName + materialData);
                }

                BlockStateAction action = new BlockStateAction(
                    optionalActionType.get(), location, material, blockData, customData);
                results.add(action);
            }
        }

        return results;
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
