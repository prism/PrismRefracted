package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import co.aikar.idb.PooledDatabaseOptions;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.StorageConfiguration;

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
        } else {
            // Create the prism data table. This one's the fatso.
            @Language("SQL") String dataQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "data` ("
                + "`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,"
                + "`epoch` int(10) unsigned NOT NULL,"
                + "`action_id` int(10) unsigned NOT NULL,"
                + "`player_id` int(10) unsigned NOT NULL,"
                + "`world_id` int(10) unsigned NOT NULL,"
                + "`x` int(11) NOT NULL,"
                + "`y` int(11) NOT NULL,"
                + "`z` int(11) NOT NULL,"
                + "`block_id` mediumint(5) DEFAULT NULL,"
                + "`old_block_id` mediumint(5) DEFAULT NULL,"
                + "PRIMARY KEY (`id`),"
                + "KEY `epoch` (`epoch`),"
                + "KEY `location` (`world_id`, `x`, `z`, `y`, `action_id`),"
                + "KEY `player` (`player_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            DB.executeUpdate(dataQuery);

            // Create the extra data table
            @Language("SQL") String extraQuery = "CREATE TABLE IF NOT EXISTS `"
                + storageConfig.prefix() + "data_extra` ("
                + "`extra_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,"
                + "`data_id` bigint(20) unsigned NOT NULL,"
                + "`data` text NULL,"
                + "`te_data` text NULL,"
                + "PRIMARY KEY (`extra_id`), KEY `data_id` (`data_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            DB.executeUpdate(extraQuery);

            // Create the id map table
            @Language("SQL") String idQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "id_map` ("
                + "`material` varchar(63) NOT NULL,"
                + "`state` varchar(255) NOT NULL,"
                + "`block_id` mediumint(5) NOT NULL AUTO_INCREMENT,"
                + "PRIMARY KEY (`material`, `state`),"
                + "UNIQUE KEY (`block_id`, `block_subid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            DB.executeUpdate(idQuery);

            // Create actions table
            @Language("SQL") String actionsQuery = "CREATE TABLE IF NOT EXISTS `"
                + storageConfig.prefix() + "actions` ("
                + "`action_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`action` varchar(25) NOT NULL,"
                + "PRIMARY KEY (`action_id`), UNIQUE KEY `action` (`action`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            DB.executeUpdate(actionsQuery);

            // Create the players table
            @Language("SQL") String playersQuery = "CREATE TABLE IF NOT EXISTS `"
                + storageConfig.prefix() + "players` ("
                + "`player_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`player` varchar(255) NOT NULL,"
                + "`player_uuid` binary(16) NOT NULL,"
                + "PRIMARY KEY (`player_id`),"
                + "UNIQUE KEY `player` (`player`), UNIQUE KEY `player_uuid` (`player_uuid`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            DB.executeUpdate(playersQuery);

            // Create worlds table
            @Language("SQL") String worldsQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "worlds` ("
                + "`world_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`world` varchar(255) NOT NULL,"
                + "`world_uuid` binary(16) NOT NULL,"
                + "PRIMARY KEY (`world_id`),"
                + "UNIQUE KEY `world` (`world`),"
                + "UNIQUE KEY `world_uuid_UNIQUE` (`world_uuid`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            DB.executeUpdate(worldsQuery);
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
