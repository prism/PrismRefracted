package network.darkhelmet.prism.api.storage.cache;

import network.darkhelmet.prism.api.storage.models.WorldModel;

public interface IStorageCache {
    /**
     * Cache world models in memory.
     *
     * <p>Useful for lookups when the server is running.</p>
     *
     * @param worldModel The world model
     */
    void cacheWorldModel(WorldModel worldModel);
}
