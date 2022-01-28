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
import java.util.UUID;

public class WorldModel {
    /**
     * The world UUID.
     */
    protected final UUID worldUuid;

    /**
     * Construct a new world model.
     *
     * @param worldUuid The world UUID
     */
    public WorldModel(UUID worldUuid) {
        this.worldUuid = worldUuid;
    }

    /**
     * Get the world UUID.
     *
     * @return The world UUID
     */
    public UUID worldUuid() {
        return worldUuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        var that = (WorldModel) obj;
        return Objects.equals(this.worldUuid, that.worldUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldUuid);
    }

    @Override
    public String toString() {
        return "WorldModel["
            + "worldUUID=" + worldUuid + ']';
    }
}
