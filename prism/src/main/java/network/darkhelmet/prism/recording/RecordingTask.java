package network.darkhelmet.prism.recording;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.IStorageAdapter;

public class RecordingTask implements Runnable {
    @Override
    public void run() {
        save();

        // Schedule the next recording
        Prism.getInstance().recordingManager().queueNextRecording();
    }

    /**
     * Saves anything in the queue, or as many as we can.
     */
    public void save() {
        if (!RecordingQueue.getQueue().isEmpty()) {
            try {
                int batchCount = 0;
                int batchMax = Prism.getInstance().storageConfig().batchMax();

                IStorageAdapter storageAdapter = Prism.getInstance().storageAdapter();
                IActivityBatch batch = storageAdapter.createActivityBatch();
                batch.startBatch();

                while (!RecordingQueue.getQueue().isEmpty()) {
                    batchCount++;
                    final Activity activity = RecordingQueue.getQueue().poll();
                    batch.add(activity);

                    // Batch max exceeded, break
                    if (batchCount > batchMax) {
                        String msg = "Recorder: Batch max exceeded, running insert. Queue remaining: %d";
                        Prism.getInstance().debug(String.format(msg, RecordingQueue.getQueue().size()));

                        break;
                    }
                }

                batch.commitBatch();
            } catch (Exception e) {
                Prism.getInstance().handleException(e);
            }
        }
    }
}
