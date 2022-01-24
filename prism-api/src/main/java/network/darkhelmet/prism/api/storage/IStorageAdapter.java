package network.darkhelmet.prism.api.storage;

import java.util.Optional;

import network.darkhelmet.prism.api.storage.models.ActionModel;
import network.darkhelmet.prism.api.storage.models.MaterialDataModel;
import network.darkhelmet.prism.api.storage.models.WorldModel;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

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
     * Get or register a world.
     *
     * @param world The world
     * @return The world model
     */
    Optional<WorldModel> getOrRegisterWorld(World world);

    /**
     * Get a world model for the given world.
     *
     * @param world The world
     * @return The world model
     */
    Optional<WorldModel> getWorld(World world);

    /**
     * Register a new world.
     *
     * @param world The world
     * @return The world model, unless errors occurred
     * @throws Exception Storage layer exception
     */
    WorldModel registerWorld(World world) throws Exception;

    /**
     * Check whether this storage system is enabled and ready.
     *
     * @return True if successfully initialized.
     */
    boolean ready();
}
