package network.darkhelmet.prism.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.api.storage.models.WorldModel;
import network.darkhelmet.prism.config.StorageConfiguration;

import org.bukkit.World;
import org.intellij.lang.annotations.Language;

public class MysqlStorageAdapter implements IStorageAdapter {
    /**
     * The storage configuration.
     */
    protected StorageConfiguration storageConfig;

    /**
     * The database.
     */
    protected HikariDataSource database;

    /**
     * Construct a new instance.
     *
     * @param storageConfiguration The storage configuration
     */
    public MysqlStorageAdapter(StorageConfiguration storageConfiguration) {
        this.storageConfig = storageConfiguration;

        HikariConfig dbConfig = new HikariConfig();

        // Values from config file
        dbConfig.setJdbcUrl(storageConfiguration.jdbcUrl());
        dbConfig.setUsername(storageConfiguration.username());
        dbConfig.setPassword(storageConfiguration.password());

        // Currently-hardcoded configs
        dbConfig.addHealthCheckProperty("connectivityCheckTimeoutMs", "1000");
        dbConfig.addHealthCheckProperty("expected99thPercentileMs", "10");

        try {
            database = new HikariDataSource(dbConfig);

            describeDatabase();
            createTables();
            updateSchemas();
        } catch (HikariPool.PoolInitializationException e) {
            Prism.getInstance().error(String.format("Hikari Pool did not Initialize: %s", e.getMessage()));
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

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SHOW VARIABLES");
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs == null ) {
                throw new SQLNonTransientConnectionException("Database did not configure correctly.");
            }

            while (rs.next()) {
                dbInfo.put(rs.getString(1).toLowerCase(), rs.getString(2));
            }

            String version = dbInfo.get("version");
            String versionComment = dbInfo.get("version_comment");
            String msg = String.format("Database version: %s / %s", version, versionComment);
            Prism.getInstance().log(msg);
        }
    }

    /**
     * Create tables.
     *
     * @throws SQLException The database exception
     */
    protected void createTables() throws SQLException {
        try (
            Connection  conn = database.getConnection();
            Statement stmt = conn.createStatement()
        ) {
            // Create actions table
            @Language("SQL") String actionsQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "actions` ("
                + "`action_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`action` varchar(25) NOT NULL,"
                + "PRIMARY KEY (`action_id`)," + "UNIQUE KEY `action` (`action`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.executeUpdate(actionsQuery);

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
                + "`block_subid` mediumint(5) DEFAULT NULL,"
                + "`old_block_id` mediumint(5) DEFAULT NULL,"
                + "`old_block_subid` mediumint(5) DEFAULT NULL,"
                + "PRIMARY KEY (`id`),"
                + "KEY `epoch` (`epoch`),"
                + "KEY  `location` (`world_id`, `x`, `z`, `y`, `action_id`),"
                + "KEY  `player` (`player_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.executeUpdate(dataQuery);

            // Create the extra data table
            @Language("SQL") String extraQuery = "CREATE TABLE IF NOT EXISTS `"
                + storageConfig.prefix() + "data_extra` ("
                + "`extra_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,"
                + "`data_id` bigint(20) unsigned NOT NULL,"
                    + "`data` text NULL," + "`te_data` text NULL,"
                + "PRIMARY KEY (`extra_id`)," + "KEY `data_id` (`data_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.executeUpdate(extraQuery);

            // Create the id map table
            @Language("SQL") String idQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "id_map` ("
                + "`material` varchar(63) NOT NULL,"
                + "`state` varchar(255) NOT NULL,"
                + "`block_id` mediumint(5) NOT NULL AUTO_INCREMENT,"
                + "`block_subid` mediumint(5) NOT NULL DEFAULT 0," + "PRIMARY KEY (`material`, `state`),"
                + "UNIQUE KEY (`block_id`, `block_subid`)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.executeUpdate(idQuery);

            // Create the meta table
            @Language("SQL") String metaQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "meta` ("
                + "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`k` varchar(25) NOT NULL,"
                + "`v` varchar(255) NOT NULL,"
                + "PRIMARY KEY (`id`)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.executeUpdate(metaQuery);

            // Create the players table
            @Language("SQL") String playersQuery = "CREATE TABLE IF NOT EXISTS `"
                + storageConfig.prefix() + "players` ("
                + "`player_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`player` varchar(255) NOT NULL,"
                + "`player_uuid` binary(16) NOT NULL,"
                + "PRIMARY KEY (`player_id`),"
                + "UNIQUE KEY `player` (`player`)," + "UNIQUE KEY `player_uuid` (`player_uuid`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stmt.executeUpdate(playersQuery);

            // Create worlds table
            @Language("SQL") String worldsQuery = "CREATE TABLE IF NOT EXISTS `" + storageConfig.prefix() + "worlds` ("
                + "`world_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`world` varchar(255) NOT NULL,"
                + "PRIMARY KEY (`world_id`)," + "UNIQUE KEY `world` (`world`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

            stmt.executeUpdate(worldsQuery);
        }
    }

    /**
     * Update the schema as needed.
     *
     * @throws SQLException The database exception
     */
    protected void updateSchemas() throws SQLException {
        @Language("SQL") String sql = "SELECT v FROM " + storageConfig.prefix() + "meta WHERE k = 'schema_ver'";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                String schemaVersion = rs.getString("v");
                Prism.getInstance().log(String.format("Prism database version: %s", schemaVersion));

                // Update: 8 -> v4
                if (schemaVersion.equalsIgnoreCase("8")) {
                    MysqlSchemaUpdater.update_8_to_v4(storageConfig, database);
                }
            }
        }
    }

//    public WorldModel getOrCreateWorld(World world) {
//
//    }
}
