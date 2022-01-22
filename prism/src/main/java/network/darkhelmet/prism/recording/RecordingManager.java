package network.darkhelmet.prism.recording;

import network.darkhelmet.prism.Prism;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class RecordingManager {
    /**
     * Cache the recording task.
     */
    BukkitTask recordingTask;

    /**
     * Construct the recording manager.
     */
    public RecordingManager() {
        queueNextRecording();
    }

    /**
     * Queue the next execution of this task.
     */
    public void queueNextRecording() {
        recordingTask = Bukkit.getServer().getScheduler()
            .runTaskLaterAsynchronously(Prism.getInstance(), new RecordingTask(), 10);
    }
}
