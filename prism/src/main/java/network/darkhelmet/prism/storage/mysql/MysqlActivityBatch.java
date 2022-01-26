/*
 * Prism (Refracted)
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.prism.storage.mysql;

import co.aikar.idb.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.IBlockAction;
import network.darkhelmet.prism.api.actions.ICustomData;
import network.darkhelmet.prism.api.actions.IEntityAction;
import network.darkhelmet.prism.api.actions.IMaterialAction;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.services.configuration.StorageConfiguration;
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
     * Cache a map of the activities with extra data.
     *
     * <p>The integer is "index" of the activity in this batch. Used to map
     * generated keys to the activity when we need to write custom data.</p>
     */
    private final Map<Integer, IActivity> activitiesWithCustomData = new HashMap<>();

    /**
     * Count the "index" of the activities in this batch.
     */
    private int activityBatchIndex = 0;

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
            + "(`timestamp`, `x`, `y`, `z`, `action_id`, `entity_type_id`,"
                + "`material_id`, `old_material_id`, `world_id`, `cause_id`) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public void add(IActivity activity) throws SQLException {
        statement.setLong(1, activity.timestamp() / 1000);
        statement.setInt(2, activity.location().getBlockX());
        statement.setInt(3, activity.location().getBlockY());
        statement.setInt(4, activity.location().getBlockZ());

        // Set the action relationship
        byte actionId = getOrCreateActionId(activity.action().type().key());
        statement.setByte(5, actionId);

        // Set the entity relationship
        int entityTypeId = 0;
        if (activity.action() instanceof IEntityAction entityAction) {
            entityTypeId = getOrCreateEntityTypeId(entityAction.serializeEntityType());
        }
        statement.setInt(6, entityTypeId);

        // Set the material relationship
        int materialId = 0;
        if (activity.action() instanceof IMaterialAction materialAction) {
            String material = materialAction.serializeMaterial();
            String data = null;

            if (activity.action() instanceof IBlockAction blockAction) {
                data = blockAction.serializeBlockData();
            }

            materialId = getOrCreateMaterialId(material, data);
        }
        statement.setInt(7, materialId);

        // Set the replaced material relationship
        int oldMaterialId = 0;
        if (activity.action() instanceof IBlockAction blockAction) {
            String replacedMaterial = blockAction.serializeReplacedMaterial();
            String replacedData = blockAction.serializeReplacedBlockData();

            oldMaterialId = getOrCreateMaterialId(replacedMaterial, replacedData);
        }
        statement.setInt(8, oldMaterialId);

        // Set the world relationship
        World world = activity.location().getWorld();
        byte worldId = getOrCreateWorldId(world.getUID(), world.getName());
        statement.setByte(9, worldId);

        // Set the player relationship
        Long playerId = null;
        String cause = "unknown";
        if (activity.cause() instanceof Player player) {
            playerId = getOrCreatePlayerId(player.getUniqueId(), player.getName());
        } else if (activity.cause() instanceof String causeStr) {
            cause = causeStr;
        }

        // Set the cause relationship
        long causeId = getOrCreateCauseId(cause, playerId);
        statement.setLong(10, causeId);

        if (activity.action() instanceof ICustomData customData) {
            if (customData.hasCustomData()) {
                activitiesWithCustomData.put(activityBatchIndex, activity);
            }
        }

        activityBatchIndex++;

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
     * Get or create the entity type record and return the primary key.
     *
     * @param entityType The entity type
     * @return The primary key
     * @throws SQLException The database exception
     */
    private int getOrCreateEntityTypeId(String entityType) throws SQLException {
        int primaryKey;

        // Attempt to create the record
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "entity_types "
            + "(`entity_type`) VALUES (?) ON DUPLICATE KEY UPDATE `entity_type` = `entity_type`";

        Long longPk = DB.executeInsert(insert, entityType);

        if (longPk != null) {
            primaryKey = longPk.intValue();
        } else {
            // Select the existing record
            @Language("SQL") String select = "SELECT entity_type_id FROM " + storageConfig.prefix() + "entity_types "
                + "WHERE entity_type = ? ";

            primaryKey = DB.getFirstColumn(select, entityType);
        }

        return primaryKey;
    }

    /**
     * Get or create the material data record and return the primary key.
     *
     * @param material The material
     * @param blockData The data, if any
     * @return The primary key
     * @throws SQLException The database exception
     */
    private int getOrCreateMaterialId(String material, String blockData) throws SQLException {
        int primaryKey;

        // Attempt to create the record
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "material_data "
            + "(`material`, `data`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `material` = `material`";

        Long longPk = DB.executeInsert(insert, material, blockData);

        if (longPk != null) {
            primaryKey = longPk.intValue();
        } else {
            // Select the existing material or material+data
            @Language("SQL") String select = "SELECT material_id FROM "
                + storageConfig.prefix() + "material_data "
                + "WHERE material = ? ";

            if (blockData != null) {
                select += "AND data = ?";
                primaryKey = DB.getFirstColumn(select, material, blockData);
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

        insertCustomData(statement.getGeneratedKeys());

        // Clear queue data, reset just in case the batch is restarted.
        activitiesWithCustomData.clear();
        activityBatchIndex = 0;

        // Probably not needed but why not be safe.
        connection.setAutoCommit(true);

        // Close stuff
        statement.close();
        connection.close();
    }

    /**
     * Inserts additional activity data when necessary (tile entities, items, etc).
     *
     * @param keys The generate keys resultset from the parent activity batch insert
     * @throws SQLException Database exception
     */
    private void insertCustomData(ResultSet keys) throws SQLException {
        @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "activities_custom_data "
            + "(`activity_id`, `version`, `data`) VALUES (?, ?, ?)";

        PreparedStatement dataStatement = connection.prepareStatement(insert);
        short version = Prism.getInstance().serializerVersion();

        int i = 0;
        while (keys.next()) {
            if (activitiesWithCustomData.containsKey(i)) {
                IActivity activity = activitiesWithCustomData.get(i);
                ICustomData customDataAction = (ICustomData) activity.action();

                int activityId = keys.getInt(1);
                String customData = customDataAction.serializeCustomData();

                dataStatement.setInt(1, activityId);
                dataStatement.setShort(2, version);
                dataStatement.setString(3, customData);
                dataStatement.addBatch();
            }

            i++;
        }

        dataStatement.executeBatch();
        connection.commit();
    }
}
