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

package network.darkhelmet.prism.api.displays;

import net.kyori.adventure.text.Component;

public interface DisplayFormatter<T> {
    /**
     * Format an object for display.
     *
     * @param object The object
     * @return The component
     */
    Component format(T object);

    /**
     * Format a heading.
     *
     * @return The component
     */
    Component heading();

    /**
     * Format a no-results message for this type.
     *
     * @return The component
     */
    Component noResults();
}
