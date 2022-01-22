package network.darkhelmet.prism.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import network.darkhelmet.prism.api.storage.cache.IStorageCache;
import network.darkhelmet.prism.api.storage.models.WorldModel;

public class StorageCache implements IStorageCache {
    /**
     * Cache of world models by the world uuid.
     */
    private Map<UUID, WorldModel> worldModels = new HashMap<>();

    @Override
    public void cacheWorldModel(WorldModel worldModel) {
        worldModels.put(worldModel.worldUuid(), worldModel);
    }
}
