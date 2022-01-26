package network.darkhelmet.prism.api.actions;

import java.util.Optional;

import network.darkhelmet.prism.api.actions.types.ActionType;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface IActionRegistry {
    /**
     * Create a new action for the given type/block.
     *
     * @param type The action type
     * @param block The block
     * @return The block action
     */
    IBlockAction createBlockAction(ActionType type, Block block);

    /**
     * Create a new action for the given type/item stack.
     *
     * @param type The action type
     * @param itemStack The item stack
     * @return The item stack action
     */
    IItemAction createItemStackAction(ActionType type, ItemStack itemStack);

    /**
     * Get an action type by key.
     *
     * @param key The key
     * @return The action type, if any
     */
    Optional<ActionType> getActionType(String key);

    /**
     * Register a new action type.
     *
     * @param actionType The action type
     */
    void registerAction(ActionType actionType);
}
