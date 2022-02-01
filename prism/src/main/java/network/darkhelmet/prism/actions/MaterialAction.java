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

import java.util.Locale;

import network.darkhelmet.prism.api.actions.IMaterialAction;
import network.darkhelmet.prism.api.actions.types.IActionType;

import org.bukkit.Material;

public abstract class MaterialAction extends Action implements IMaterialAction {
    /**
     * The material.
     */
    protected Material material;

    /**
     * Construct a new material action.
     *
     * @param type The action type
     * @param material The material
     */
    public MaterialAction(IActionType type, Material material) {
        super(type);

        this.material = material;
    }

    @Override
    public String formatContent() {
        return material.toString().toLowerCase(Locale.ENGLISH).replace("_", " ");
    }

    @Override
    public String serializeMaterial() {
        return material.toString().toLowerCase(Locale.ENGLISH);
    }
}
