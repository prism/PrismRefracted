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

package network.darkhelmet.prism.api.actions.types;

import network.darkhelmet.prism.api.actions.ActionData;
import network.darkhelmet.prism.api.actions.IAction;

public interface IActionType {
    /**
     * Creates an action given the action data.
     *
     * @param actionData The action data
     * @return The action
     */
    IAction createAction(ActionData actionData);

    /**
     * Get the key.
     *
     * @return The key
     */
    String key();

    /**
     * Get the family key (The common part after the hyphen).
     *
     * @return The family key
     */
    String familyKey();

    /**
     * Get the past-tense translation key for this specific action.
     *
     * @return The past tense translation key
     */
    String pastTenseTranslationKey();

    /**
     * Get the action result type.
     *
     * @return The result type
     */
    ActionResultType resultType();

    /**
     * Get if this action type if usually reversible.
     *
     * @return True if reversible
     */
    boolean reversible();
}
