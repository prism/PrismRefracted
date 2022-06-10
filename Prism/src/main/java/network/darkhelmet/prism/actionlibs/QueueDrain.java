package network.darkhelmet.prism.actionlibs;

import network.darkhelmet.prism.Prism;

public class QueueDrain {

    private static boolean draining = false;

    private final Prism plugin;

    public static boolean isDraining() {
        return draining;
    }

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

        draining = true;
        RecordingManager.failedDbConnectionCount = 0;

        draining = true;
        RecordingManager.failedDbConnectionCount = 0;

        final RecordingTask recorderTask = new RecordingTask(plugin);

        // Faster drain
        RecordingTask.setActionsPerInsert(15000);
        Prism.getInstance().getConfig().set("prism.query.max-failures-before-wait", 10);
        Prism.getInstance().getConfig().set("prism.query.queue-empty-tick-delay", 0);

        // Force queue to empty
        while (!RecordingQueue.getQueue().isEmpty()) {

            Prism.log("正在开始新一批次队列清空...");
            Prism.log("目前队列大小: " + RecordingQueue.getQueue().size());

            if (Prism.getPrismDataSource().isPaused()) {
                Prism.getPrismDataSource().setPaused(false);
                Prism.log("Force unpaused the recorder for drain.");
            }

            if (Prism.getPrismDataSource().isPaused()) {
                Prism.getPrismDataSource().setPaused(false);
                Prism.log("Force unpaused the recorder for drain.");
            }

            // run insert
            try {
                recorderTask.insertActionsIntoDatabase();
            } catch (final Exception e) {
                e.printStackTrace();
                Prism.log("停止队列清空, 由于捕获到异常. 失去的队列条目: "
                        + RecordingQueue.getQueue().size());
                break;
            }

            if (RecordingManager.failedDbConnectionCount > 0) {
                Prism.log("停止队列清空, 由于检测到数据库错误. 失去的队列条目: "
                        + RecordingQueue.getQueue().size());
            }
        }
    }
}