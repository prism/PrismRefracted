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

import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.actions.types.IActionType;

public abstract class Action implements IAction {
    /**
     * The type.
     */
    private IActionType type;

    /**
     * Construct a new action.
     *
     * @param type The action type
     */
    public Action(IActionType type) {
        this.type = type;
    }

    /**
     * Get the action type.
     *
     * @return The action type
     */
    public IActionType type() {
        return type;
    }
}
