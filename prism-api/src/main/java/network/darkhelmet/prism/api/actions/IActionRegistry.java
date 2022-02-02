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

import java.util.Collection;
import java.util.Optional;

import network.darkhelmet.prism.api.actions.types.IActionType;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface IActionRegistry {
    /**
     * Get all registered action types.
     *
     * @return All registered action types
     */
    Collection<IActionType> actionTypes();

    /**
     * Get all action types that belong to a family.
     *
     * @param family The family
     * @return All matching action types
     */
    Collection<IActionType> actionTypesInFamily(String family);

    /**
     * Create a new action for the given type/block.
     *
     * @param type The action type
     * @param blockState The block state
     * @return The block action
     */
    IBlockAction createBlockAction(IActionType type, BlockState blockState);

    /**
     * Create a new action for the given type/block, including a replaced block state.
     *
     * @param type The action type
     * @param blockState The new block state
     * @param replaced The replaced block state
     * @return The block action
     */
    IBlockAction createBlockAction(IActionType type, BlockState blockState, BlockState replaced);

    /**
     * Create a new action for the given entity.
     *
     * @param type The action type
     * @param entity The entity
     * @return The entity action
     */
    IEntityAction createEntityAction(IActionType type, Entity entity);

    /**
     * Create a new action for the given type/item stack.
     *
     * @param type The action type
     * @param itemStack The item stack
     * @return The item stack action
     */
    IItemAction createItemStackAction(IActionType type, ItemStack itemStack);

    /**
     * Get an action type by key.
     *
     * @param key The key
     * @return The action type, if any
     */
    Optional<IActionType> getActionType(String key);

    /**
     * Register a new action type.
     *
     * @param actionType The action type
     */
    void registerAction(IActionType actionType);
}
