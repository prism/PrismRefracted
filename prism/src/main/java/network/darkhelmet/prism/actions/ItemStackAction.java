package network.darkhelmet.prism.actions;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;

import network.darkhelmet.prism.api.actions.IItemAction;
import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.IActivity;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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
    public boolean hasCustomData() {
        return this.nbtContainer != null;
    }

    @Override
    public @Nullable String serializeCustomData() {
        return nbtContainer.toString();
    }

    @Override
    public void applyRollback(IActivity activityContext) {
        System.out.println("rolling back item! " + activityContext.cause());
    }

    @Override
    public void applyRestore(IActivity activityContext) {}
}
