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
import network.darkhelmet.prism.api.actions.types.ActionResultType;
import network.darkhelmet.prism.api.actions.types.IActionType;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.modifications.ModificationResult;
import network.darkhelmet.prism.api.services.modifications.ModificationResultStatus;
import network.darkhelmet.prism.services.modifications.state.ItemStackStateChange;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackAction extends MaterialAction implements IItemAction {
    /**
     * The item stack.
     */
    private final ItemStack itemStack;

    /**
     * The nbt container.
     */
    private final NBTContainer nbtContainer;

    /**
     * Construct a new item stack action.
     * @param type The action type
     * @param itemStack The item stack
     */
    public ItemStackAction(IActionType type, ItemStack itemStack) {
        this(type, itemStack, null);
    }

    /**
     * Construct a new item stack action.
     *
     * @param type The action type
     * @param itemStack The item stack
     * @param descriptor The descriptor
     */
    public ItemStackAction(IActionType type, ItemStack itemStack, String descriptor) {
        super(type, itemStack.getType(), descriptor);

        this.itemStack = itemStack;
        this.nbtContainer = NBTItem.convertItemtoNBT(itemStack);
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
    public ModificationResult applyRollback(Object owner, IActivity activityContext, boolean isPreview) {
        activityContext.player();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(activityContext.player().uuid());

        // Give item back to player
        if (offlinePlayer.isOnline() && activityContext.action().type().resultType().equals(ActionResultType.REMOVES)) {
            Player player = (Player) offlinePlayer;
            player.getInventory().addItem(itemStack.clone());

            ItemStackStateChange stateChange = new ItemStackStateChange(itemStack.clone(), null);

            return new ModificationResult(ModificationResultStatus.APPLIED, stateChange);
        }

        return new ModificationResult(ModificationResultStatus.SKIPPED, null);
    }

    @Override
    public ModificationResult applyRestore(Object owner, IActivity activityContext, boolean isPreview) {
        return new ModificationResult(ModificationResultStatus.SKIPPED, null);
    }
}
