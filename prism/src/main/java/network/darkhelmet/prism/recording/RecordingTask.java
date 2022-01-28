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

package network.darkhelmet.prism.recording;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.activities.IActivity;
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
