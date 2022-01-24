package network.darkhelmet.prism.recording;

import java.util.concurrent.LinkedBlockingQueue;

import network.darkhelmet.prism.api.activities.Activity;

public class RecordingQueue {
    /**
     * Queue of activities.
     */
    private static final LinkedBlockingQueue<Activity> queue = new LinkedBlockingQueue<>();

    /**
     * Add an activity to the recording queue.
     *
     * @param activity Activity
     */
    public static void addToQueue(final Activity activity) {
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
    public static LinkedBlockingQueue<Activity> getQueue() {
        return queue;
    }
}