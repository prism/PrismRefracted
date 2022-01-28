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

package network.darkhelmet.prism.api.storage.models;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

public class MaterialDataModel {
    /**
     * The material key.
     */
    protected final String materialKey;

    /**
     * The data, if any.
     */
    protected final String data;

    /**
     * Construct a new material/data model.
     *
     * @param materialKey The material key
     */
    public MaterialDataModel(String materialKey) {
        this.materialKey = materialKey;
        this.data = null;
    }

    /**
     * Construct a new material/data model.
     *
     * @param materialKey The material key
     * @param data The data
     */
    public MaterialDataModel(String materialKey, String data) {
        this.materialKey = materialKey;
        this.data = data;
    }

    /**
     * Get the material key.
     *
     * @return The key
     */
    public String materialKey() {
        return materialKey;
    }

    /**
     * Get the data.
     *
     * @return The data, if any
     */
    public @Nullable String data() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        var that = (MaterialDataModel) obj;
        return Objects.equals(this.materialKey, that.materialKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialKey, data);
    }

    @Override
    public String toString() {
        return "MaterialStateModel["
            + "materialKey=" + materialKey + ","
            + "data=" + data + ']';
    }
}