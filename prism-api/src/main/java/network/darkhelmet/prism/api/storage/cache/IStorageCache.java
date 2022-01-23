package network.darkhelmet.prism.api.storage.cache;

import java.util.Optional;

import network.darkhelmet.prism.api.storage.models.ActionModel;
import network.darkhelmet.prism.api.storage.models.WorldModel;

import org.bukkit.World;

public interface IStorageCache {
    /**
     * Cache action models in memory.
     *
     * <p>Useful for lookups when the server is running.</p>
     *
     * @param actionModel The action model
     */
    void cacheActionModel(ActionModel actionModel);

    /**
     * Cache world models in memory.
     *
     * <p>Useful for lookups when the server is running.</p>
     *
     * @param worldModel The world model
     */
    void cacheWorldModel(WorldModel worldModel);

    /**
     * Get a world model.
     *
     * @param world The world
     * @return World model, if world not null and model present
     */
    Optional<WorldModel> getWorldModel(World world);
}
