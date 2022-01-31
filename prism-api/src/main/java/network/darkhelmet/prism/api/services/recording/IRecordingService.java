package network.darkhelmet.prism.api.services.recording;

public interface IRecordingService {
    /**
     * Schedule the next recording task.
     *
     * @param task The task
     */
    void queueNextRecording(Runnable task);
}
