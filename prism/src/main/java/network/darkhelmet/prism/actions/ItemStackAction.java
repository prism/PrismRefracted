/*
 * Prism (Refracted)
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.prism.actions;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;

import network.darkhelmet.prism.api.actions.IItemAction;
import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.modifications.ModificationResult;

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
    public ModificationResult applyRollback(IActivity activityContext, boolean isPreview) {
        return ModificationResult.SKIPPED;
    }

    @Override
    public ModificationResult applyRestore(IActivity activityContext, boolean isPreview) {
        return ModificationResult.SKIPPED;
    }
}
