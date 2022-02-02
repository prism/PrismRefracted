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

package network.darkhelmet.prism.services.configuration;

import java.util.ArrayList;
import java.util.List;

import network.darkhelmet.prism.api.services.filters.FilterBehavior;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class FilterConfiguartion {
    /**
     * Worlds.
     */
    private List<String> worlds = new ArrayList<>();

    /**
     * The filter behavior.
     */
    private FilterBehavior behavior;

    /**
     * Get the behavior.
     *
     * @return The behavior
     */
    public FilterBehavior behavior() {
        return behavior;
    }

    /**
     * Get the worlds.
     *
     * @return The worlds.
     */
    public List<String> worlds() {
        return worlds;
    }
}
