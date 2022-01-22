package network.darkhelmet.prism.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.StorageConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlStorageAdapter implements IStorageAdapter {
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
        } catch (HikariPool.PoolInitializationException e) {
            Prism.getInstance().error(String.format("Hikari Pool did not Initialize: %s", e.getMessage()));
        }
    }

    protected void describeDatabase() throws SQLException {
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
            Prism.log("Prism detected your database is version:" + version + " / " + versionComment);
            Prism.log("You have set nonStandardSql to " + nonStandardSql);
            Prism.log("You are able to use non standard SQL");
            if (!nonStandardSql) {
                Prism.log("Prism will use standard sql queries");
            }
        } catch (SQLNonTransientConnectionException e) {
            Prism.warn(e.getMessage());
        } catch (SQLException e) {
            Prism.log("You are not able to use non standard Sql");
            if (nonStandardSql) {
                Prism.log("This sounds like a configuration error.  If you have database access"
                        + "errors please set nonStandardSql to false");
            }
        }
    }
}
