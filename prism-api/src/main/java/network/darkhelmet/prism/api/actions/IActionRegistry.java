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

package network.darkhelmet.prism.api.actions;

import java.util.Optional;

import network.darkhelmet.prism.api.actions.types.ActionType;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
     * Create a new action for the given entity.
     *
     * @param type The action type
     * @param entity The entity
     * @return The entity action
     */
    IEntityAction createEntityAction(ActionType type, Entity entity);

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
