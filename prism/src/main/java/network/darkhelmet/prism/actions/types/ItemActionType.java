package network.darkhelmet.prism.actions.types;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;

import network.darkhelmet.prism.actions.ItemStackAction;
import network.darkhelmet.prism.api.actions.ActionData;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.actions.types.ActionResultType;
import network.darkhelmet.prism.api.actions.types.ActionType;

import org.bukkit.inventory.ItemStack;

public class ItemActionType extends ActionType {
    /**
     * Construct a new item action type.
     *
     * @param key The key
     * @param resultType The result type
     * @param reversible If action is reversible
     */
    public ItemActionType(String key, ActionResultType resultType, boolean reversible) {
        super(key, resultType, reversible);
    }

    @Override
    public IAction createAction(ActionData actionData) {
        NBTContainer container = new NBTContainer(actionData.customData());
        ItemStack itemStack = NBTItem.convertNBTtoItem(container);

        return new ItemStackAction(this, itemStack);
    }
}