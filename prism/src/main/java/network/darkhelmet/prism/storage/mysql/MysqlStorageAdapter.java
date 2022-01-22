package network.darkhelmet.prism.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.StorageConfiguration;

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
}
