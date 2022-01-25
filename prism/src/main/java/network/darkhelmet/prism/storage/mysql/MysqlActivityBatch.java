package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import network.darkhelmet.prism.api.actions.BlockStateAction;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.utils.TypeUtils;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

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
        @Language("SQL") String sql = "INSERT INTO " + storageConfig.prefix() + "activities "
            + "(`timestamp`, `x`, `y`, `z`, `action_id`, `material_id`, `world_id`, `cause_id`) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public void add(Activity activity) throws SQLException {
        statement.setLong(1, activity.timestamp() / 1000);
        statement.setInt(2, activity.location().getBlockX());
        statement.setInt(3, activity.location().getBlockY());
        statement.setInt(4, activity.location().getBlockZ());

        // Set the action relationship
        byte actionId = getOrCreateActionId(activity.action().type().key());
        statement.setByte(5, actionId);

        // Set the material relationship
        int materialId = 0;
        if (activity.action() instanceof BlockStateAction blockStateAction) {
            String material = blockStateAction.serializeMaterial();
            String data = blockStateAction.serializeBlockData();

            materialId = getOrCreateMaterialId(material, data);
        }
        statement.setInt(6, materialId);

        // Set the world relationship
        World world = activity.location().getWorld();
        byte worldId = getOrCreateWorldId(world.getUID(), world.getName());
        statement.setByte(7, worldId);

        // Set the player relationship
        Long playerId = null;
        String cause = "unknown";
        if (activity.cause() instanceof Player player) {
            playerId = getOrCreatePlayerId(player.getUniqueId(), player.getName());
        }

        // Set the cause relationship
        long causeId = getOrCreateCauseId(cause, playerId);
        statement.setLong(8, causeId);

        statement.addBatch();
    }

    /**
     * Get or create the action record and return the primary key.
     *
     * @param actionKey The action key
     * @return The primary key
     * @throws SQLException The database exception
     */
    private byte getOrCreateActionId(String actionKey) throws SQLException {
        byte primaryKey;

        // Attempt to create the record
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "actions "
            + "(`action`) VALUES (?) ON DUPLICATE KEY UPDATE `action` = `action`";

        Long longPk = DB.executeInsert(insert, actionKey);

        if (longPk != null) {
            primaryKey = longPk.byteValue();
        } else {
            // Select the existing record
            @Language("SQL") String select = "SELECT action_id FROM " + storageConfig.prefix() + "actions "
                + "WHERE action = ? ";

            Integer intPk = DB.getFirstColumn(select, actionKey);
            primaryKey = intPk.byteValue();
        }

        return primaryKey;
    }

    /**
     * Get or create the cause record and return the primary key.
     *
     * @param cause The cause name
     * @param playerId The player id, if a player
     * @return The primary key
     * @throws SQLException The database exception
     */
    private long getOrCreateCauseId(String cause, @Nullable Long playerId) throws SQLException {
        long primaryKey;

        // Attempt to create the record
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "causes "
            + "(`cause`, `player_id`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `cause` = `cause`";

        Long longPk = DB.executeInsert(insert, cause, playerId);

        if (longPk != null) {
            primaryKey = longPk;
        } else if (playerId != null) {
            // Select the existing record on player
            @Language("SQL") String select = "SELECT cause_id FROM " + storageConfig.prefix() + "causes "
                + "WHERE player_id = ? ";

            primaryKey = DB.getFirstColumn(select, playerId);
        } else {
            // Select the existing record on cause
            @Language("SQL") String select = "SELECT cause_id FROM " + storageConfig.prefix() + "causes "
                + "WHERE cause = ? ";

            primaryKey = DB.getFirstColumn(select, cause);
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
     * Get or create the player record and return the primary key.
     *
     * <p>Note: This will update the player name.</p>
     *
     * @param playerUuid The player uuid
     * @param playerName The player name
     * @return The primary key
     * @throws SQLException The database exception
     */
    private long getOrCreatePlayerId(UUID playerUuid, String playerName) throws SQLException {
        long primaryKey;
        String uuidStr = TypeUtils.uuidToDbString(playerUuid);

        // Attempt to create the record, or update the world name
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "players "
            + "(`player`, `player_uuid`) VALUES (?, UNHEX(?)) ON DUPLICATE KEY UPDATE `player` = ?";

        Long longPk = DB.executeInsert(insert, playerName, uuidStr, playerName);

        if (longPk != null) {
            primaryKey = longPk;
        } else {
            // Select the existing record
            @Language("SQL") String select = "SELECT player_id FROM " + storageConfig.prefix() + "players "
                + "WHERE player_uuid = UNHEX(?)";

            primaryKey = DB.getFirstColumn(select, uuidStr);
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
    private byte getOrCreateWorldId(UUID worldUuid, String worldName) throws SQLException {
        byte primaryKey;
        String uuidStr = TypeUtils.uuidToDbString(worldUuid);

        // Attempt to create the record, or update the world name
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "worlds "
            + "(`world`, `world_uuid`) VALUES (?, UNHEX(?)) ON DUPLICATE KEY UPDATE `world` = ?";

        Long longPk = DB.executeInsert(insert, worldName, uuidStr, worldName);

        if (longPk != null) {
            primaryKey = longPk.byteValue();
        } else {
            // Select the existing record
            @Language("SQL") String select = "SELECT world_id FROM " + storageConfig.prefix() + "worlds "
                + "WHERE world_uuid = UNHEX(?)";

            Integer intPk = DB.getFirstColumn(select, uuidStr);
            primaryKey = intPk.byteValue();
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
