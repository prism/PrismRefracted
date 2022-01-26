package network.darkhelmet.prism.api.storage;

import java.util.List;

import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.models.ActivityRow;

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
    PaginatedResults<ActivityRow> queryActivities(ActivityQuery query) throws Exception;

    /**
     * Query activities as actions we can provide to an applier.
     *
     * @param query The activity query
     * @return List of action
     * @throws Exception Storage layer exception
     */
    List<IActivity> queryActivitiesAsActions(ActivityQuery query) throws Exception;

    /**
     * Check whether this storage system is enabled and ready.
     *
     * @return True if successfully initialized.
     */
    boolean ready();
}
