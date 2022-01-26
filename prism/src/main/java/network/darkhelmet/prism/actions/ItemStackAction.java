package network.darkhelmet.prism.actions;

import network.darkhelmet.prism.api.actions.ActionType;
import network.darkhelmet.prism.api.actions.IItemAction;

import org.bukkit.inventory.ItemStack;

public class ItemStackAction extends Action implements IItemAction {
    public ItemStackAction(ActionType type, ItemStack itemStack) {
        super(type);
    }

    @Override
    public boolean hasCustomData() {
        // @todo idk?
        return false;
    }

    @Override
    public String serializeCustomData() {
        return "";
    }
}
