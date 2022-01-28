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

package network.darkhelmet.prism.storage.mysql.models;

import java.util.Objects;

import network.darkhelmet.prism.api.storage.models.MaterialDataModel;

public class SqlMaterialDataModel extends MaterialDataModel {
    /**
     * The primary key.
     */
    private int id;

    /**
     * Construct a sql material data model.
     *
     * @param id The primary key
     * @param materialKey The material key
     */
    public SqlMaterialDataModel(int id, String materialKey) {
        super(materialKey, null);

        this.id = id;
    }

    /**
     * Construct a sql material data model.
     *
     * @param id The primary key
     * @param materialKey The material key
     * @param data The data
     */
    public SqlMaterialDataModel(int id, String materialKey, String data) {
        super(materialKey, data);

        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        var that = (SqlMaterialDataModel) obj;
        return Objects.equals(this.id, that.id);
    }

    /**
     * The ID.
     *
     * @return The primary key
     */
    public int id() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, materialKey, data);
    }

    @Override
    public String toString() {
        return "SqlMaterialDataModel["
            + "id=" + id + ","
            + "materialKey=" + materialKey + ","
            + "data=" + data + ']';
    }
}
