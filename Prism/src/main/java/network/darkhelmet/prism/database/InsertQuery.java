package network.darkhelmet.prism.database;

import network.darkhelmet.prism.api.actions.Handler;

public interface InsertQuery {
    /**
     * Returns the id of the action.
     * @param a Handler
     * @return long
     */
    long insertActionIntoDatabase(Handler a);

    void createBatch() throws Exception;

    boolean addInsertionToBatch(Handler a) throws Exception;

    void processBatch() throws Exception;

}
