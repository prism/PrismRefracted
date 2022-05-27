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

        Prism.log("Forcing recorder queue to run a new batch before shutdown...");

        draining = true;
        RecordingManager.failedDbConnectionCount = 0;

        final RecordingTask recorderTask = new RecordingTask(plugin);

        // Faster drain
        RecordingTask.setActionsPerInsert(15000);
        Prism.getInstance().getConfig().set("prism.query.max-failures-before-wait", 10);
        Prism.getInstance().getConfig().set("prism.query.queue-empty-tick-delay", 0);

        // Force queue to empty
        while (!RecordingQueue.getQueue().isEmpty()) {

            Prism.log("Starting drain batch...");
            Prism.log("Current queue size: " + RecordingQueue.getQueue().size());

            if (Prism.getPrismDataSource().isPaused()) {
                Prism.getPrismDataSource().setPaused(false);
                Prism.log("Force unpaused the recorder for drain.");
            }

            // run insert
            try {
                recorderTask.insertActionsIntoDatabase();
            } catch (final Exception e) {
                e.printStackTrace();
                Prism.log("Stopping queue drain due to caught exception. Queue items lost: "
                        + RecordingQueue.getQueue().size());
                break;
            }

            if (RecordingManager.failedDbConnectionCount > 0) {
                Prism.log("Stopping queue drain due to detected database error. Queue items lost: "
                        + RecordingQueue.getQueue().size());
            }
        }
    }
}