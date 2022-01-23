package network.darkhelmet.prism.api.storage;

import java.util.Optional;

import network.darkhelmet.prism.api.storage.models.ActionModel;
import network.darkhelmet.prism.api.storage.models.WorldModel;

import org.bukkit.World;

public interface IStorageAdapter {
    /**
     * Close any connections. May not be applicable to the chosen storage.
     */
    void close();

    /**
     * Get or register an action.
     *
     * @param actionKey The action key
     * @return The action model
     */
    Optional<ActionModel> getOrRegisterAction(String actionKey);

    /**
     * Get an action.
     *
     * @param actionKey The action key
     * @return The action model
     */
    Optional<ActionModel> getAction(String actionKey);

    /**
     * Register an action.
     *
     * @param actionKey The action key
     * @return The action model
     * @throws Exception The database exception
     */
    ActionModel registerAction(String actionKey) throws Exception;

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
