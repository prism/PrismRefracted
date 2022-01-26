package network.darkhelmet.prism.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.actions.IBlockAction;
import network.darkhelmet.prism.api.actions.IItemAction;
import network.darkhelmet.prism.api.actions.types.ActionResultType;
import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.actions.types.BlockActionType;
import network.darkhelmet.prism.api.actions.types.ItemActionType;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ActionRegistry implements IActionRegistry {
    /**
     * Cache of action types by key.
     */
    private final Map<String, ActionType> actionsTypes = new HashMap<>();

    /**
     * Static cache of Prism action types.
     */
    public static final ActionType BLOCK_BREAK = new BlockActionType("block-break", ActionResultType.REMOVES, true);
    public static final ActionType ITEM_DROP = new ItemActionType("item-drop", ActionResultType.REMOVES, true);

    /**
     * Construct the action registry.
     */
    public ActionRegistry() {
        // Register Prism actions
        registerAction(BLOCK_BREAK);
        registerAction(ITEM_DROP);
    }

    @Override
    public IBlockAction createBlockAction(ActionType type, Block block) {
        if (!(type instanceof BlockActionType)) {
            throw new IllegalArgumentException("Block actions cannot be made from non-block action types.");
        }

        return new BlockStateAction(type, block.getState());
    }

    @Override
    public IItemAction createItemStackAction(ActionType type, ItemStack itemStack) {
        if (!(type instanceof ItemActionType)) {
            throw new IllegalArgumentException("Item actions cannot be made from non-item action types.");
        }

        return new ItemStackAction(type, itemStack);
    }

    @Override
    public void registerAction(ActionType actionType) {
        if (actionsTypes.containsKey(actionType.key())) {
            throw new IllegalArgumentException("Registry already has an action type with that key.");
        }

        actionsTypes.put(actionType.key(), actionType);
    }

    @Override
    public Optional<ActionType> getActionType(String key) {
        if (actionsTypes.containsKey(key)) {
            return Optional.of(actionsTypes.get(key));
        }

        return Optional.empty();
    }
}
