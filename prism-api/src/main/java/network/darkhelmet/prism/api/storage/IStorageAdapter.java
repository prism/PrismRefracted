package network.darkhelmet.prism.api.storage;

public interface IStorageAdapter {
    /**
     * Close any connections. May not be applicable to the chosen storage.
     */
    void close();
}
