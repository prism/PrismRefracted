package network.darkhelmet.prism.actions;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;

import network.darkhelmet.prism.api.actions.ActionType;
import network.darkhelmet.prism.api.actions.IItemAction;

import org.bukkit.inventory.ItemStack;

public class ItemStackAction extends MaterialAction implements IItemAction {
    /**
     * The item stack.
     */
    private ItemStack itemStack;

    /**
     * The nbt container.
     */
    private NBTContainer nbtContainer;

    /**
     * Construct a new item stack action.
     *
     * @param type The action type
     * @param itemStack The item stack
     */
    public ItemStackAction(ActionType type, ItemStack itemStack) {
        super(type, itemStack.getType());

        this.itemStack = itemStack;
        nbtContainer = NBTItem.convertItemtoNBT(itemStack);
    }

    @Override
    public String serializeItem() {
        return nbtContainer.toString();
    }
}
