package network.darkhelmet.prism.actionlibs;

import network.darkhelmet.prism.Prism;

public class QueueDrain {

    private final Prism plugin;

    /**
     * Creat a drain.
     *
     * @param plugin Prism.
     */
    public QueueDrain(Prism plugin) {
        this.plugin = plugin;
    }

    /**
     * Drain the queue.
     */
    public void forceDrainQueue() {

        Prism.log("正在强制记录器队列在关闭前运行新一批次...");

        final RecordingTask recorderTask = new RecordingTask(plugin);

        // Force queue to empty
        while (!RecordingQueue.getQueue().isEmpty()) {

            Prism.log("正在开始排水批次...");
            Prism.log("目前队列大小: " + RecordingQueue.getQueue().size());

            // run insert
            try {
                recorderTask.insertActionsIntoDatabase();
            } catch (final Exception e) {
                e.printStackTrace();
                Prism.log("停止队列排水, 由于捕获到异常. 失去的队列条目: "
                        + RecordingQueue.getQueue().size());
                break;
            }

            if (RecordingManager.failedDbConnectionCount > 0) {
                Prism.log("停止队列排水, 由于检测到数据库错误. 失去的队列条目: "
                        + RecordingQueue.getQueue().size());
            }
        }
    }
}