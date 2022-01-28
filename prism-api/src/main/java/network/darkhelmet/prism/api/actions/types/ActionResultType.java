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

public enum ActionResultType {
    /**
     * Actions which result in a creation.
     *
     * <p>Example: block place.</p>
     */
    CREATES,

    /**
     * Actions which have no "real" result and are purely informational. (Or can't be realistically reversed).
     *
     * <p>Example: vehicle enter</p>
     */
    NONE,

    /**
     * Actions which result in a removal.
     *
     * <p>Example: block broken, item removed, etc.</p>
     */
    REMOVES
}
