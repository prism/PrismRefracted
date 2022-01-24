package network.darkhelmet.prism.api.storage;

import network.darkhelmet.prism.api.activities.ActivityQuery;

public interface IStorageAdapter {
    /**
     * Close any connections. May not be applicable to the chosen storage.
     */
    void close();

    /**
     * Creates a new batch manager.
     *
     * @return The batch
     */
    IActivityBatch createActivityBatch();

    /**
     * Query activities.
     *
     * @param query The activity query
     * @throws Exception Storage layer exception
     */
    void queryActivities(ActivityQuery query) throws Exception;

    /**
     * Check whether this storage system is enabled and ready.
     *
     * @return True if successfully initialized.
     */
    boolean ready();
}
