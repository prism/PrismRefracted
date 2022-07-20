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

package network.darkhelmet.prism.core.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import network.darkhelmet.prism.api.actions.IBlockAction;
import network.darkhelmet.prism.api.actions.ICustomData;
import network.darkhelmet.prism.api.actions.IEntityAction;
import network.darkhelmet.prism.api.actions.IMaterialAction;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.activities.ISingleActivity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.util.NamedIdentity;
import network.darkhelmet.prism.core.services.cache.CacheService;
import network.darkhelmet.prism.core.utils.TypeUtils;
import network.darkhelmet.prism.idb.DB;
import network.darkhelmet.prism.loader.services.configuration.StorageConfiguration;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

public class MysqlActivityBatch implements IActivityBatch {
    /**
     * The serializer version.
     */
    private final short serializerVersion;

    /**
     * The storage configuration.
     */
    private final StorageConfiguration storageConfig;

    /**
     * The cache service.
     */
    private final CacheService cacheService;

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
     * @param serializerVersion The serializer version
     * @param storageConfiguration The storage configuration
     * @param cacheService The cache service
     */
    public MysqlActivityBatch(
            short serializerVersion,
            StorageConfiguration storageConfiguration,
            CacheService cacheService) {
        this.serializerVersion = serializerVersion;
        this.storageConfig = storageConfiguration;
        this.cacheService = cacheService;
    }

    @Override
    public void startBatch() throws SQLException {
        connection = DB.getGlobalDatabase().getConnection();
        connection.setAutoCommit(false);

        // Build the INSERT query
        @Language("SQL") String sql = "INSERT INTO " + storageConfig.prefix() + "activities "
            + "(`timestamp`, `x`, `y`, `z`, `action_id`, `entity_type_id`,"
                + "`material_id`, `old_material_id`, `world_id`, `cause_id`, `descriptor`) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public void add(ISingleActivity activity) throws SQLException {
        statement.setLong(1, activity.timestamp() / 1000);
        statement.setInt(2, activity.location().intX());
        statement.setInt(3, activity.location().intY());
        statement.setInt(4, activity.location().intZ());

        // Set the action relationship
        byte actionId = getOrCreateActionId(activity.action().type().key());
        statement.setByte(5, actionId);

        // Set the entity relationship
        if (activity.action() instanceof IEntityAction) {
            int entityTypeId = getOrCreateEntityTypeId(((IEntityAction) activity.action()).serializeEntityType());
            statement.setInt(6, entityTypeId);
        } else {
            statement.setNull(6, Types.INTEGER);
        }

        // Set the material relationship
        if (activity.action() instanceof IMaterialAction) {
            String material = ((IMaterialAction) activity.action()).serializeMaterial();
            String data = null;

            if (activity.action() instanceof IBlockAction) {
                data = ((IBlockAction) activity.action()).serializeBlockData();
            }

            statement.setInt(7, getOrCreateMaterialId(material, data));
        } else {
            statement.setNull(7, Types.INTEGER);
        }

        // Set the replaced material relationship
        if (activity.action() instanceof IBlockAction blockAction) {
            String replacedMaterial = blockAction.serializeReplacedMaterial();
            String replacedData = blockAction.serializeReplacedBlockData();

            int oldMaterialId = getOrCreateMaterialId(replacedMaterial, replacedData);
            statement.setInt(8, oldMaterialId);
        } else {
            statement.setNull(8, Types.INTEGER);
        }

        // Set the world relationship
        NamedIdentity world = activity.location().world();
        byte worldId = getOrCreateWorldId(world.uuid(), world.name());
        statement.setByte(9, worldId);

        // Set the player relationship
        Long playerId = null;
        if (activity.player() != null) {
            playerId = getOrCreatePlayerId(activity.player().uuid(), activity.player().name());
        }

        // Set the cause relationship
        long causeId = getOrCreateCauseId(activity.cause(), playerId);
        statement.setLong(10, causeId);

        // Set the descriptor
        statement.setString(11, activity.action().descriptor());

        if (activity.action() instanceof ICustomData) {
            if (((ICustomData) activity.action()).hasCustomData()) {
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
        if (cacheService.actionKeyPkMap().containsKey(actionKey)) {
            return cacheService.actionKeyPkMap().getByte(actionKey);
        }

        byte primaryKey;

        // Select any existing record
        @Language("SQL") String select = "SELECT action_id FROM " + storageConfig.prefix() + "actions "
            + "WHERE action = ?";

        Integer intPk = DB.getFirstColumn(select, actionKey);
        if (intPk != null) {
            primaryKey = intPk.byteValue();
        } else {
            // Attempt to create the record
            @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "actions "
                + "(`action`) VALUES (?)";

            Long longPk = DB.executeInsert(insert, actionKey);

            if (longPk != null) {
                primaryKey = longPk.byteValue();
            } else {
                throw new SQLException(
                    String.format("Failed to get or create an action record. Action: %s", actionKey));
            }
        }

        cacheService.actionKeyPkMap().put(actionKey, primaryKey);

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
        if (playerId != null && cacheService.playerCausePkMap().containsKey(playerId.longValue())) {
            return cacheService.playerCausePkMap().get(playerId.longValue());
        }

        if (cause != null && cacheService.namedCausePkMap().containsKey(cause)) {
            return cacheService.namedCausePkMap().getLong(cause);
        }

        long primaryKey;

        Long longPk;
        if (playerId != null) {
            // Select the existing record on player
            @Language("SQL") String select = "SELECT cause_id FROM " + storageConfig.prefix() + "causes "
                + "WHERE player_id = ?";

            longPk = DB.getFirstColumn(select, playerId);
        } else {
            // Select the existing record on cause
            @Language("SQL") String select = "SELECT cause_id FROM " + storageConfig.prefix() + "causes "
                + "WHERE cause = ?";

            longPk = DB.getFirstColumn(select, cause);
        }

        if (longPk != null) {
            primaryKey = longPk;
        } else {
            // Attempt to create the record
            @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "causes "
                + "(`cause`, `player_id`) VALUES (?, ?)";

            longPk = DB.executeInsert(insert, cause, playerId);

            if (longPk != null) {
                primaryKey = longPk;
            } else {
                throw new SQLException(
                    String.format("Failed to get or create a cause record. Cause: %s, %d", cause, playerId));
            }
        }

        if (cause != null) {
            cacheService.namedCausePkMap().put(cause, primaryKey);
        }

        if (playerId != null) {
            cacheService.playerCausePkMap().put(playerId.longValue(), primaryKey);
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
        if (cacheService.entityTypePkMap().containsKey(entityType)) {
            return cacheService.entityTypePkMap().getInt(entityType);
        }

        int primaryKey;

        // Select the existing record
        @Language("SQL") String select = "SELECT entity_type_id FROM " + storageConfig.prefix() + "entity_types "
            + "WHERE entity_type = ? ";

        Integer intPk = DB.getFirstColumn(select, entityType);
        if (intPk != null) {
            primaryKey = intPk;
        } else {
            // Attempt to create the record
            @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "entity_types "
                + "(`entity_type`) VALUES (?)";

            Long longPk = DB.executeInsert(insert, entityType);

            if (longPk != null) {
                primaryKey = longPk.intValue();
            } else {
                throw new SQLException(
                    String.format("Failed to get or create a entity type record. Material: %s", entityType));
            }
        }

        cacheService.entityTypePkMap().put(entityType, primaryKey);

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
        if (blockData == null && cacheService.materialPkMap().containsKey(material)) {
            return cacheService.materialPkMap().getInt(material);
        }

        int primaryKey;

        // Select the existing material or material+data
        @Language("SQL") String select = "SELECT material_id FROM "
            + storageConfig.prefix() + "materials "
            + "WHERE material = ? ";

        Integer intPk;
        if (blockData != null) {
            select += "AND data = ?";
            intPk = DB.getFirstColumn(select, material, blockData);
        } else {
            select += "AND data IS NULL";
            intPk = DB.getFirstColumn(select, material);
        }

        if (intPk != null) {
            primaryKey = intPk;
        } else {
            // Attempt to create the record
            @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "materials "
                + "(`material`, `data`) VALUES (?, ?)";

            Long longPk = DB.executeInsert(insert, material, blockData);

            if (longPk != null) {
                primaryKey = longPk.intValue();
            } else {
                throw new SQLException(
                    String.format("Failed to get or create a material record. Material: %s %s",
                        material, blockData));
            }
        }

        if (blockData == null) {
            cacheService.materialPkMap().put(material, primaryKey);
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
        if (cacheService.playerUuidPkMap().containsKey(playerUuid)) {
            return cacheService.playerUuidPkMap().getLong(playerUuid);
        }

        long primaryKey;
        String uuidStr = TypeUtils.uuidToDbString(playerUuid);

        // Select the existing record
        @Language("SQL") String select = "SELECT player_id FROM " + storageConfig.prefix() + "players "
            + "WHERE player_uuid = UNHEX(?)";

        Long longPk = DB.getFirstColumn(select, uuidStr);
        if (longPk != null) {
            primaryKey = longPk;
        } else {
            // Attempt to create the record, or update the world name
            @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "players "
                + "(`player`, `player_uuid`) VALUES (?, UNHEX(?))";

            longPk = DB.executeInsert(insert, playerName, uuidStr);

            if (longPk != null) {
                primaryKey = longPk;
            } else {
                throw new SQLException(
                    String.format("Failed to get or create a player record. Player: %s", playerUuid));
            }
        }

        cacheService.playerUuidPkMap().put(playerUuid, primaryKey);

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
        if (cacheService.worldUuidPkMap().containsKey(worldUuid)) {
            return cacheService.worldUuidPkMap().getByte(worldUuid);
        }

        byte primaryKey;
        String uuidStr = TypeUtils.uuidToDbString(worldUuid);

        // Select any existing record
        // Note: We check *then* insert instead of using on duplicate key because ODK would
        // generate a new auto-increment primary key and update it every time, leading to ballooning PKs
        @Language("SQL") String select = "SELECT world_id FROM " + storageConfig.prefix() + "worlds "
            + "WHERE world_uuid = UNHEX(?)";

        Integer intPk = DB.getFirstColumn(select, uuidStr);
        if (intPk != null) {
            primaryKey = intPk.byteValue();
        } else {
            // Attempt to create the record, or update the world name
            @Language("SQL") String insert = "INSERT INTO " + storageConfig.prefix() + "worlds "
                + "(`world`, `world_uuid`) VALUES (?, UNHEX(?))";

            Long longPk = DB.executeInsert(insert, worldName, uuidStr);
            if (longPk != null) {
                primaryKey = longPk.byteValue();
            } else {
                throw new SQLException(
                    String.format("Failed to get or create a world record. World: %s", worldUuid));
            }
        }

        cacheService.worldUuidPkMap().put(worldUuid, primaryKey);

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

        // Restore auto-commit
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

        int i = 0;
        while (keys.next()) {
            if (activitiesWithCustomData.containsKey(i)) {
                IActivity activity = activitiesWithCustomData.get(i);
                ICustomData customDataAction = (ICustomData) activity.action();

                int activityId = keys.getInt(1);
                String customData = customDataAction.serializeCustomData();

                dataStatement.setInt(1, activityId);
                dataStatement.setShort(2, serializerVersion);
                dataStatement.setString(3, customData);
                dataStatement.addBatch();
            }

            i++;
        }

        dataStatement.executeBatch();
        connection.commit();
    }
}