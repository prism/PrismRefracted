package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import network.darkhelmet.prism.api.activity.Activity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.config.StorageConfiguration;

import org.intellij.lang.annotations.Language;

public class MysqlBatch implements IActivityBatch {
    /**
     * The storage configuration.
     */
    private StorageConfiguration storageConfig;

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
    public MysqlBatch(StorageConfiguration storageConfiguration) {
        this.storageConfig = storageConfiguration;
    }

    @Override
    public void startBatch() throws SQLException {
        connection = DB.getGlobalDatabase().getConnection();
        connection.setAutoCommit(false);

        @Language("SQL") String sql = "INSERT INTO " + storageConfig.prefix() + "activity "
            + "(epoch, x, y, z) VALUES (?, ?, ?, ?)";

        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public void add(Activity activity) throws SQLException {
        statement.setLong(1, activity.timestamp() / 1000);
        statement.setInt(2, activity.location().getBlockX());
        statement.setInt(3, activity.location().getBlockY());
        statement.setInt(4, activity.location().getBlockZ());

        statement.addBatch();
    }

    @Override
    public void commitBatch() throws SQLException {
        statement.executeBatch();
        connection.commit();

        statement.close();
        connection.close();
    }
}
