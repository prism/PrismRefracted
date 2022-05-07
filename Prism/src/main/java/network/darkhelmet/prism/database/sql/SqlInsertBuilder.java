package network.darkhelmet.prism.database.sql;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.Handler;
import network.darkhelmet.prism.database.InsertQuery;
import network.darkhelmet.prism.database.PrismDataSource;
import network.darkhelmet.prism.database.QueryBuilder;
import network.darkhelmet.prism.players.PlayerIdentification;
import network.darkhelmet.prism.players.PrismPlayer;
import network.darkhelmet.prism.utils.IntPair;
import network.darkhelmet.prism.utils.block.Utilities;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlInsertBuilder extends QueryBuilder implements InsertQuery {
    final ArrayList<Handler> extraDataQueue = new ArrayList<>();
    private PreparedStatement batchStatement;
    private Connection batchConnection;

    /**
     * Create an insert builder.
     * @param dataSource Data source
     */
    public SqlInsertBuilder(PrismDataSource dataSource) {
        super(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public long insertActionIntoDatabase(Handler a) {
        int worldId = 0;
        long id = 0;
        String worldName = a.getLoc().getWorld().getName();
        if (Prism.prismWorlds.containsKey(worldName)) {
            worldId = Prism.prismWorlds.get(worldName);
        }
        int actionId = 0;
        if (Prism.prismActions.containsKey(a.getActionType().getName())) {
            actionId = Prism.prismActions.get(a.getActionType().getName());
        }

        PrismPlayer prismPlayer = PlayerIdentification.getPrismPlayerByNameFromCache(a.getSourceName());
        int playerId = prismPlayer.getId();

        if (worldId == 0 || actionId == 0 || playerId == 0) {
            Prism.debug("SQL 数据错误: Handler:" + a.toString());
        }
        IntPair newIds = Prism.getItems().materialToIds(a.getMaterial(),
                Utilities.dataString(a.getBlockData()));
        IntPair oldIds = Prism.getItems().materialToIds(a.getOldMaterial(),
                Utilities.dataString(a.getOldBlockData()));

        Location l = a.getLoc();

        try (
                Connection con = dataSource.getConnection();
                PreparedStatement s = con.prepareStatement(getQuery(), Statement.RETURN_GENERATED_KEYS)
        ) {
            applyToInsert(s, a, actionId, playerId, worldId, newIds, oldIds, l);
            s.executeUpdate();
            ResultSet generatedKeys = s.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }
            generatedKeys.close();
            if (a.hasExtraData()) {
                String serialData = a.serialize();
                if (serialData != null && !serialData.isEmpty()) {

                    try (
                            PreparedStatement s2 = con.prepareStatement(
                                    "INSERT INTO `" + prefix + "data_extra` (data_id, data) VALUES (?, ?)",
                                    Statement.RETURN_GENERATED_KEYS)) {
                        s2.setLong(1, id);
                        s2.setString(2, serialData);
                        s2.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public void createBatch() throws SQLException {
        batchConnection = dataSource.getConnection();
        if (batchConnection == null) {
            throw new SQLException("没有数据库连接");
        }
        batchConnection.setAutoCommit(false);
        batchStatement = batchConnection.prepareStatement(getQuery(), Statement.RETURN_GENERATED_KEYS);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean addInsertionToBatch(Handler a) throws SQLException {
        if (batchStatement == null) {
            return false;
        }
        int worldId = 0;
        String worldName = a.getLoc().getWorld().getName();
        if (Prism.prismWorlds.containsKey(worldName)) {
            worldId = Prism.prismWorlds.get(worldName);
        }
        int actionId = 0;
        if (Prism.prismActions.containsKey(a.getActionType().getName())) {
            actionId = Prism.prismActions.get(a.getActionType().getName());
        }

        PrismPlayer prismPlayer = PlayerIdentification.getPrismPlayerByNameFromCache(a.getSourceName());
        int playerId = prismPlayer.getId();

        IntPair newIds = Prism.getItems().materialToIds(a.getMaterial(),
                Utilities.dataString(a.getBlockData()));

        IntPair oldIds = Prism.getItems().materialToIds(a.getOldMaterial(),
                Utilities.dataString(a.getOldBlockData()));
        Location l = a.getLoc();
        applyToInsert(batchStatement, a, actionId, playerId, worldId, newIds, oldIds, l);
        batchStatement.addBatch();
        extraDataQueue.add(a);
        return true;
    }

    /**
     * Process the batch.
     * @throws SQLException on sql errors
     */
    public void processBatch() throws SQLException {
        if (batchStatement == null) {
            Prism.debug("批次插入为空");
            throw new SQLException("没有配置批处理语句");
        }
        batchStatement.executeBatch();
        batchConnection.commit();
        Prism.debug("批次插入已提交: " + System.currentTimeMillis());
        processExtraData(batchStatement.getGeneratedKeys());
        batchConnection.close();
    }

    /**
     * Process any extra data associated with the ResultSet.
     * @param keys ResultSet
     * @throws SQLException SQLException.
     */
    public void processExtraData(ResultSet keys) throws SQLException {
        if (extraDataQueue.isEmpty()) {
            return;
        }
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement s = conn.prepareStatement("INSERT INTO `"
                        + prefix + "data_extra` (data_id,data) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            conn.setAutoCommit(false);
            int i = 0;
            while (keys.next()) {
                // @todo should not happen
                if (i >= extraDataQueue.size()) {
                    Prism.log("跳过 " + prefix + " 的额外数据 data.id " + keys.getLong(1)
                            + " 因为队列没有它的数据.");
                    continue;
                }

                final Handler a = extraDataQueue.get(i);
                if (a.hasExtraData()) {
                    String serialData = a.serialize();

                    if (serialData != null && !serialData.isEmpty()) {
                        s.setLong(1, keys.getLong(1));
                        s.setString(2, serialData);
                        s.addBatch();
                    }
                } else {
                    Prism.debug("跳过 " + prefix + " 的额外数据 data.id " + keys.getLong(1)
                            + " 因为队列没有它的数据.");
                }

                i++;
            }

            // The main delay is here
            s.executeBatch();

            if (conn.isClosed()) {
                Prism.log(
                        "Prism 数据库错误. 我们得在构建批量插入查询的额外数据的过程中保释.");
            } else {
                conn.commit();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
            Prism.getPrismDataSource().handleDataSourceException(e);
        }
    }

    private void applyToInsert(PreparedStatement s, Handler a, int actionId, int playerId, int worldId,
                               IntPair newIds, IntPair oldIds, Location l) throws SQLException {
        s.setLong(1, a.getUnixEpoch());
        s.setInt(2, actionId);
        s.setInt(3, playerId);
        s.setInt(4, worldId);
        s.setInt(5, newIds.first);
        s.setInt(6, newIds.second);
        s.setInt(7, oldIds.first);
        s.setInt(8, oldIds.second);
        s.setInt(9, l.getBlockX());
        s.setInt(10, l.getBlockY());
        s.setInt(11, l.getBlockZ());
    }

    private String getQuery() {

        return "INSERT INTO " + prefix
                + "data (epoch,action_id,player_id,world_id,block_id,block_subid,old_block_id,old_block_subid,"
                + "x,y,z) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    }
}
