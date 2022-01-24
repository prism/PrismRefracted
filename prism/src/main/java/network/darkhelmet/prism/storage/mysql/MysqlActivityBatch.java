package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import network.darkhelmet.prism.api.actions.BlockStateAction;
import network.darkhelmet.prism.api.activity.Activity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.utils.TypeUtils;

import org.bukkit.World;
import org.intellij.lang.annotations.Language;

public class MysqlActivityBatch implements IActivityBatch {
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
    public MysqlActivityBatch(StorageConfiguration storageConfiguration) {
        this.storageConfig = storageConfiguration;
    }

    @Override
    public void startBatch() throws SQLException {
        connection = DB.getGlobalDatabase().getConnection();
        connection.setAutoCommit(false);

        // Build the INSERT query
        @Language("SQL") String sql = "INSERT INTO " + storageConfig.prefix() + "activity "
            + "(epoch, x, y, z, action_id, material_id, world_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public void add(Activity activity) throws SQLException {
        statement.setLong(1, activity.timestamp() / 1000);
        statement.setInt(2, activity.location().getBlockX());
        statement.setInt(3, activity.location().getBlockY());
        statement.setInt(4, activity.location().getBlockZ());

        // Set the action relationship
        int actionId = getOrCreateActionId(activity.action().key());
        statement.setInt(5, actionId);

        // Set the material relationship
        int materialId = 0;
        if (activity.action() instanceof BlockStateAction blockStateAction) {
            String material = TypeUtils.materialToString(blockStateAction.blockState().getType());
            String data = TypeUtils.blockDataToString(blockStateAction.blockState().getBlockData());

            materialId = getOrCreateMaterialId(material, data);
        }
        statement.setInt(6, materialId);

        // Set the world relationship
        World world = activity.location().getWorld();
        long worldId = getOrCreateWorldId(world.getUID(), world.getName());
        statement.setLong(7, worldId);

        statement.addBatch();
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

    /**
     * Get or create the world record and return the primary key.
     *
     * <p>Note: This will update the world name.</p>
     *
     * @param worldUuid The world uuid
     * @param worldName The world name
     * @return The primary key
     * @throws SQLException The database exception
     */
    private int getOrCreateWorldId(UUID worldUuid, String worldName) throws SQLException {
        int primaryKey;
        String uuidStr = TypeUtils.uuidToDbString(worldUuid);

        // Attempt to create the record, or update the world name
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "worlds "
            + "(`world`, `world_uuid`) VALUES (?, UNHEX(?)) ON DUPLICATE KEY UPDATE `world` = ?";

        Long longPk = DB.executeInsert(insert, worldName, uuidStr, worldName);

        if (longPk != null) {
            primaryKey = longPk.intValue();
        } else {
            // Select the existing record
            @Language("SQL") String select = "SELECT world_id FROM " + storageConfig.prefix() + "worlds "
                + "WHERE world_uuid = UNHEX(?)";

            primaryKey = DB.getFirstColumn(select, uuidStr);
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
