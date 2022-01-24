package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.BlockStateAction;
import network.darkhelmet.prism.api.activity.Activity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.cache.IStorageCache;
import network.darkhelmet.prism.api.storage.models.WorldModel;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.storage.mysql.models.SqlWorldModel;
import network.darkhelmet.prism.utils.TypeUtils;

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

        // Build the INSERT query
        @Language("SQL") String sql = "INSERT INTO " + storageConfig.prefix() + "activity "
            + "(epoch, world_id, x, y, z, action_id, material_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

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

            int actionId = getOrCreateActionId(activity.action().key());
            statement.setInt(6, actionId);

            int materialId = 0;
            if (activity.action() instanceof BlockStateAction blockStateAction) {
                String material = TypeUtils.materialToString(blockStateAction.blockState().getType());
                String data = TypeUtils.blockDataToString(blockStateAction.blockState().getBlockData());

                materialId = getOrCreateMaterialId(material, data);
            }

            statement.setInt(7, materialId);

            statement.addBatch();
        } else {
            String msg = "Failed to record data because cache data was missing. World: %s";
            Prism.getInstance().debug(String.format(msg, optionWorldModel.isPresent()));
        }
    }

    /**
     * Get or create the action record and return the primary key.
     *
     * @param actionKey The action key
     * @return The primary key
     * @throws SQLException The database exception
     */
    private int getOrCreateActionId(String actionKey) throws SQLException {
        int primaryKey;

        // Attempt to create the record
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "actions "
            + "(`action`) VALUES (?) ON DUPLICATE KEY UPDATE `action` = `action`";

        Long longPk = DB.executeInsert(insert, actionKey);

        if (longPk != null) {
            primaryKey = longPk.intValue();
        } else {
            // Select the existing record
            @Language("SQL") String select = "SELECT action_id FROM " + storageConfig.prefix() + "actions "
                + "WHERE action = ? ";

            primaryKey = DB.getFirstColumn(select, actionKey);
        }

        return primaryKey;
    }

    /**
     * Get or create the material data record and return the primary key.
     *
     * @param material The material
     * @param data The data, if any
     * @return The primary key
     * @throws SQLException The database exception
     */
    private int getOrCreateMaterialId(String material, String data) throws SQLException {
        int primaryKey;

        // Attempt to create the record
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "material_data "
            + "(`material`, `data`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `material` = `material`";

        Long longPk = DB.executeInsert(insert, material, data);

        if (longPk != null) {
            primaryKey = longPk.intValue();
        } else {
            // Select the existing material or material+data
            @Language("SQL") String select = "SELECT material_id FROM "
                + storageConfig.prefix() + "material_data "
                + "WHERE material = ? ";

            if (data != null) {
                select += "AND data = ?";
                primaryKey = DB.getFirstColumn(select, material, data);
            } else {
                select += "AND data IS NULL";
                primaryKey = DB.getFirstColumn(select, material);
            }
        }

        return primaryKey;
    }

    @Override
    public void commitBatch() throws SQLException {
        statement.executeBatch();
        connection.commit();

        statement.close();
        connection.close();
    }
}
