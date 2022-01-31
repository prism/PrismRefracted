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

package network.darkhelmet.prism.services.recording;

import com.google.inject.Inject;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.recording.IRecordingService;
import network.darkhelmet.prism.api.storage.IActivityBatch;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.StorageConfiguration;

public class RecordingTask implements Runnable {
    /**
     * The storage config.
     */
    private final StorageConfiguration storageConfig;

    /**
     * The storage adapter.
     */
    private final IStorageAdapter storageAdapter;

    /**
     * The recording manager.
     */
    private final IRecordingService recordingService;

    /**
     * Construct a new recording task.
     *
     * @param storageAdapter The storage adapter
     */
    @Inject
    public RecordingTask(
        StorageConfiguration storageConfig, IStorageAdapter storageAdapter, IRecordingService recordingService) {
        this.storageConfig = storageConfig;
        this.storageAdapter = storageAdapter;
        this.recordingService = recordingService;
    }

    @Override
    public void run() {
        save();

        // Schedule the next recording
        recordingService.queueNextRecording(new RecordingTask(storageConfig, storageAdapter, recordingService));
    }

    /**
     * Saves anything in the queue, or as many as we can.
     */
    public void save() {
        if (!RecordingQueue.getQueue().isEmpty()) {
            try {
                int batchCount = 0;
                int batchMax = storageConfig.batchMax();

                IActivityBatch batch = storageAdapter.createActivityBatch();
                batch.startBatch();

                while (!RecordingQueue.getQueue().isEmpty()) {
                    batchCount++;
                    final IActivity activity = RecordingQueue.getQueue().poll();
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
