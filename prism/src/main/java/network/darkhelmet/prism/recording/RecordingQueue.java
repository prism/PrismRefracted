package network.darkhelmet.prism.recording;

import java.util.concurrent.LinkedBlockingQueue;

import network.darkhelmet.prism.api.activities.IActivity;

public class RecordingQueue {
    /**
     * Queue of activities.
     */
    private static final LinkedBlockingQueue<IActivity> queue = new LinkedBlockingQueue<>();

    /**
     * Add an activity to the recording queue.
     *
     * @param activity Activity
     */
    public static void addToQueue(final IActivity activity) {
        if (activity == null) {
            return;
        }

        queue.add(activity);
    }

    /**
     * Get the queue.
     *
     * @return the queue
     */
    public static LinkedBlockingQueue<IActivity> getQueue() {
        return queue;
    }
}