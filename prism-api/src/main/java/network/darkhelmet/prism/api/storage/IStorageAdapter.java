package network.darkhelmet.prism.api.storage;

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
     * Check whether this storage system is enabled and ready.
     *
     * @return True if successfully initialized.
     */
    boolean ready();
}
