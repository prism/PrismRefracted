package network.darkhelmet.prism.api.storage;

import network.darkhelmet.prism.api.activity.Activity;

public interface IActivityBatch {
    /**
     * Start a new batch.
     *
     * @throws Exception Storage layer exception
     */
    void startBatch() throws Exception;

    /**
     * Add an activity object to the batch.
     *
     * @param activity The activity
     * @throws Exception Storage layer exception
     */
    void add(Activity activity) throws Exception;

    /**
     * Commit the batch.
     *
     * @throws Exception Storage layer exception
     */
    void commitBatch() throws Exception;
}
