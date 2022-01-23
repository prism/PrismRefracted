package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.activity.Activity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.cache.IStorageCache;
import network.darkhelmet.prism.api.storage.models.WorldModel;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.storage.mysql.models.SqlWorldModel;

import org.intellij.lang.annotations.Language;

public class MysqlActivityBatch implements IActivityBatch {
    /**
     * The storage configuration.
     */
    private StorageConfiguration storageConfig;

    /**
     * Cache the cache.
     */
    private IStorageCache storageCache;

    /**
     * The connection.
     */
    private Connection connection;

    /**
     * The statement.
     */
    private PreparedStatement statement;

    /**
     * Construct a new batch handler.
     *
     * @param storageConfiguration The storage configuration
     */
    public MysqlActivityBatch(StorageConfiguration storageConfiguration) {
        this.storageConfig = storageConfiguration;

        storageCache = Prism.getInstance().storageCache();
    }

    @Override
    public void startBatch() throws SQLException {
        connection = DB.getGlobalDatabase().getConnection();
        connection.setAutoCommit(false);

        @Language("SQL") String sql = "INSERT INTO " + storageConfig.prefix() + "activity "
            + "(epoch, world_id, x, y, z) VALUES (?, ?, ?, ?, ?)";

        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public void add(Activity activity) throws SQLException {
        Optional<WorldModel> optionWorldModel = storageCache.getWorldModel(activity.location().getWorld());

        if (optionWorldModel.isPresent()) {
            long worldId = ((SqlWorldModel) optionWorldModel.get()).id();

            statement.setLong(1, activity.timestamp() / 1000);
            statement.setLong(2, worldId);
            statement.setInt(3, activity.location().getBlockX());
            statement.setInt(4, activity.location().getBlockY());
            statement.setInt(5, activity.location().getBlockZ());

            statement.addBatch();
        } else {
            String msg = "Failed to record data because cache data was missing. World: %s";
            Prism.getInstance().debug(String.format(msg, optionWorldModel.isPresent()));
        }
    }

    @Override
    public void commitBatch() throws SQLException {
        statement.executeBatch();
        connection.commit();

        statement.close();
        connection.close();
    }
}
