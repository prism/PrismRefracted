package network.darkhelmet.prism.actionlibs;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.Handler;
import network.darkhelmet.prism.database.InsertQuery;
import network.darkhelmet.prism.measurement.QueueStats;

import java.sql.Connection;
import java.sql.SQLException;

public class RecordingTask implements Runnable {

    private final Prism plugin;
    private static int actionsPerInsert;

    public static void setActionsPerInsert(int adjust) {
        actionsPerInsert = adjust;
    }

    public static int getActionsPerInsert() {
        return actionsPerInsert;
    }


    /**
     * Create the task.
     *
     * @param plugin Plugin
     */
    public RecordingTask(Prism plugin) {
        this.plugin = plugin;
        actionsPerInsert = plugin.getConfig().getInt("prism.query.actions-per-insert-batch");
    }

    /**
     * Insert Action.
     *
     * @param a Handler
     * @return rows affected.
     */
    public static long insertActionIntoDatabase(Handler a) {
        return Prism.getPrismDataSource().getDataInsertionQuery().insertActionIntoDatabase(a);
    }

    /**
     * If the queue isn't empty run an insert.
     */
    public void save() {
        if (!RecordingQueue.getQueue().isEmpty()) {
            insertActionsIntoDatabase();
        }
    }

    /**
     * Create a Insertion.
     */
    void insertActionsIntoDatabase() {
        int actionsRecorded = 0;
        int perBatch = actionsPerInsert;
        if (perBatch < 1) {
            perBatch = 1000;
        }
        if (!RecordingQueue.getQueue().isEmpty()) {
            if (Prism.getPrismDataSource().isPaused()) {
                Prism.log(
                        "Prism 数据库已暂停. 外部行为暂停了数据库处理..."
                                + "正在计划到下一次记录");
                scheduleNextRecording();
                return;
            }
            long start = System.currentTimeMillis();
            Prism.debug("正在开始批次插入队列数据. " + start);
            try (
                    Connection conn = Prism.getPrismDataSource().getConnection()
            ) {
                if ((conn == null) || (conn.isClosed())) {
                    if (RecordingManager.failedDbConnectionCount == 0) {
                        Prism.log(
                                "Prism 数据库错误. 应该有一个已有的连接, 但是没有. "
                                        + "正在将行为放入队列.");
                    }
                    RecordingManager.failedDbConnectionCount++;
                    if (RecordingManager.failedDbConnectionCount > plugin.getConfig()
                            .getInt("prism.query.max-failures-before-wait")) {
                        Prism.log("连接问题过多. 暂时放弃处理.");
                        scheduleNextRecording();
                    }
                    Prism.debug("数据库连接仍在丢失中, 增加计数.");
                    return;
                } else {
                    RecordingManager.failedDbConnectionCount = 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Prism.getPrismDataSource().handleDataSourceException(e);
                return;
            }
            InsertQuery batchedQuery;
            try {
                batchedQuery = Prism.getPrismDataSource().getDataInsertionQuery();
                batchedQuery.createBatch();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof SQLException) {
                    Prism.getPrismDataSource().handleDataSourceException((SQLException) e);
                }
                Prism.debug("数据库连接问题;");
                RecordingManager.failedDbConnectionCount++;
                return;
            }
            int i = 0;
            while (!RecordingQueue.getQueue().isEmpty()) {
                final Handler a = RecordingQueue.getQueue().poll();

                // poll() returns null if queue is empty
                if (a == null) {
                    break;
                }
                if (a.isCanceled()) {
                    continue;
                }
                batchedQuery.insertActionIntoDatabase(a);

                actionsRecorded++;

                // Break out of the loop and just commit what we have
                if (i >= perBatch) {
                    Prism.debug("记录器: 超出批次上限, 正在进行插入. 剩余队列: "
                            + RecordingQueue.getQueue().size());
                    break;
                }
                i++;
            }
            long batchDoneTime = System.currentTimeMillis();
            long batchingTime = batchDoneTime - start;
            // The main delay is here
            try {
                batchedQuery.processBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Save the current count to the queue for short historical data
            long batchProcessedEnd = System.currentTimeMillis();
            long batchRunTime = batchProcessedEnd - batchDoneTime;
            plugin.queueStats.addRunInfo(new QueueStats.TaskRunInfo(actionsRecorded,batchingTime,batchRunTime));
        }
    }

    /**
     * Rebuild the datasource and schedule the next run.
     */
    @Override
    public void run() {
        if (RecordingManager.failedDbConnectionCount > 5) {
            Prism.getPrismDataSource().rebuildDataSource(); // force rebuild pool after several failures
        }
        save();
        scheduleNextRecording();
    }

    /**
     * Get the delay based on connection failure.
     *
     * @return delay
     */
    private int getTickDelayForNextBatch() {

        // If we have too many rejected connections, increase the schedule
        if (RecordingManager.failedDbConnectionCount > plugin.getConfig()
                .getInt("prism.query.max-failures-before-wait")) {
            return RecordingManager.failedDbConnectionCount * 20;
        }

        int recorderTickDelay = plugin.getConfig().getInt("prism.queue-empty-tick-delay");
        if (recorderTickDelay < 1) {
            recorderTickDelay = 3;
        }
        return recorderTickDelay;
    }

    /**
     * Schedule a async recording with delay.
     */
    private void scheduleNextRecording() {
        if (!plugin.isEnabled()) {
            Prism.log(
                    "由于插件现已关闭, 无法计划新的记录任务. "
                            + "如果您正在关闭服务器, 则可忽略这个问题.");
            return;
        }
        plugin.recordingTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin,
                this, getTickDelayForNextBatch());
    }
}
