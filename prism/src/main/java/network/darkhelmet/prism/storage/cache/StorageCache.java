package network.darkhelmet.prism.storage.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import network.darkhelmet.prism.api.storage.cache.IStorageCache;
import network.darkhelmet.prism.api.storage.models.ActionModel;
import network.darkhelmet.prism.api.storage.models.MaterialDataModel;
import network.darkhelmet.prism.api.storage.models.WorldModel;

import org.bukkit.World;

public class StorageCache implements IStorageCache {
    /**
     * Cache the action models.
     */
    private final Map<String, ActionModel> actionModels = new HashMap<>();

    /**
     * Cache the material data models.
     */
    private final Map<String, MaterialDataModel> materialDataModels = new HashMap<>();

    /**
     * Cache of world models by the world uuid.
     */
    private final Map<UUID, WorldModel> worldModels = new HashMap<>();

    @Override
    public void cacheActionModel(ActionModel actionModel) {
        actionModels.put(actionModel.key(), actionModel);
    }

    /**
     * Helper method to make a "key" for a material model we can easily look up.
     *
     * @param model The model
     * @return The key (material + data)
     */
    private String keyFromMaterialData(MaterialDataModel model) {
        return model.materialKey() + model.data();
    }

    @Override
    public void cacheMaterialData(MaterialDataModel materialDataModel) {
        materialDataModels.put(keyFromMaterialData(materialDataModel), materialDataModel);
    }

    @Override
    public void cacheWorldModel(WorldModel worldModel) {
        worldModels.put(worldModel.worldUuid(), worldModel);
    }

    @Override
    public Optional<WorldModel> getWorldModel(World world) {
        return Optional.ofNullable(worldModels.get(world.getUID()));
    }
}
