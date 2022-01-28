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

import com.google.inject.Inject;
import network.darkhelmet.prism.Prism;

import network.darkhelmet.prism.api.recording.IRecordingService;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class RecordingManager implements IRecordingService {
    /**
     * Cache the scheduled task.
     */
    private BukkitTask task;

    /**
     * Construct the recording manager.
     */
    @Inject
    public RecordingManager(RecordingTask recordingTask) {
        queueNextRecording(recordingTask);
    }

    /**
     * Queue the next execution of this task.
     */
    public void queueNextRecording(Runnable recordingTask) {
        task = Bukkit.getServer().getScheduler()
            .runTaskLaterAsynchronously(Prism.getInstance(), recordingTask, 10);
    }
}
