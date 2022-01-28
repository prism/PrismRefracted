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

public abstract class ActionType implements IActionType {
    /**
     * The key.
     */
    protected String key;

    /**
     * The action result type.
     */
    protected ActionResultType resultType;

    /**
     * Indicates whether this action type is usually reversible.
     */
    protected boolean reversible;

    /**
     * Construct a new action type.
     *
     * @param key The key
     * @param resultType The result type
     * @param reversible If action is reversible
     */
    public ActionType(String key, ActionResultType resultType, boolean reversible) {
        this.key = key;
        this.resultType = resultType;
        this.reversible = reversible;
    }

    /**
     * Get the key.
     *
     * @return The key
     */
    public String key() {
        return key;
    }

    /**
     * Get the action result type.
     *
     * @return The result type
     */
    public ActionResultType resultType() {
        return resultType;
    }

    /**
     * Get if this action type if usually reversible.
     *
     * @return True if reversible
     */
    public boolean reversible() {
        return reversible;
    }
}
