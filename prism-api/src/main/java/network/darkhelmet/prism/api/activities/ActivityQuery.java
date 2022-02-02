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

package network.darkhelmet.prism.api.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import network.darkhelmet.prism.api.actions.types.IActionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public record ActivityQuery(
    boolean isLookup,
    boolean grouped,
    Collection<IActionType> actionTypes,
    Location location,
    UUID worldUuid,
    Vector minVector,
    Vector maxVector,
    Collection<Material> materials,
    Collection<EntityType> entityTypes,
    Collection<String> playerNames,
    Long since,
    Long before,
    int offset,
    int limit,
    ActivityQuery.Sort sort) {
    /**
     * Describe the sort directions.
     */
    public enum Sort {
        ASCENDING, DESCENDING
    }

    /**
     * Get a new builder.
     *
     * @return The activity query builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * The action types.
         */
        private Collection<IActionType> actionTypes = new ArrayList<>();

        /**
         * Indicate this is a lookup query.
         */
        private boolean isLookup = true;

        /**
         * Indicate if results should be grouped.
         */
        private boolean grouped = true;

        /**
         * The location.
         */
        private Location location;

        /**
         * The world uuid.
         */
        private UUID worldUuid;

        /**
         * The minimum vector.
         */
        private Vector minVector;

        /**
         * The maximum vector.
         */
        private Vector maxVector;

        /**
         * A list of materials.
         */
        private final Collection<Material> materials = new ArrayList<>();

        /**
         * A list of entity types.
         */
        private final Collection<EntityType> entityTypes = new ArrayList<>();

        /**
         * A list of player names.
         */
        private final Collection<String> playerNames = new ArrayList<>();

        /**
         * A lower-bound timestamp.
         */
        private Long since = null;

        /**
         * A upper-bound timestamp.
         */
        private Long before = null;

        /**
         * The offset.
         */
        private int offset = 0;

        /**
         * The limit.
         */
        private int limit = 0;

        /**
         * The sort direction.
         */
        private Sort sort = Sort.DESCENDING;

        /**
         * Add an action type.
         *
         * @param actionType The action type
         * @return The builder
         */
        public Builder actionType(IActionType actionType) {
            this.actionTypes.add(actionType);
            return this;
        }

        /**
         * Add action types.
         *
         * @param actionTypes The action types
         * @return The builder
         */
        public Builder actionTypes(Collection<IActionType> actionTypes) {
            this.actionTypes.addAll(actionTypes);
            return this;
        }

        /**
         * Set whether results should be grouped.
         *
         * @param isGrouped If grouped
         * @return The builder
         */
        public Builder grouped(boolean isGrouped) {
            this.grouped = isGrouped;
            return this;
        }

        /**
         * Set whether this is a lookup. The query results can be grouped
         * and the order by is different.
         *
         * @param isLookup If lookup
         * @return The builder
         */
        public Builder lookup(boolean isLookup) {
            this.isLookup = isLookup;

            if (!isLookup) {
                grouped(false);
            }

            return this;
        }

        /**
         * Set a single location. Also sets the world.
         *
         * @param location The location
         * @return The builder
         */
        public Builder location(Location location) {
            this.location = location;
            return world(location.getWorld().getUID());
        }

        /**
         * Set the world.
         *
         * @param world The world
         * @return The builder
         */
        public Builder world(World world) {
            return world(world.getUID());
        }

        /**
         * Set the world by uuid.
         *
         * @param worldUuid The world uuid
         * @return The builder
         */
        public Builder world(UUID worldUuid) {
            this.worldUuid = worldUuid;
            return this;
        }

        /**
         * Set the min vector - the min corner of a bounding box.
         *
         * @param vector The vector
         * @return The builder
         */
        public Builder minVector(Vector vector) {
            this.minVector = vector;
            return this;
        }

        /**
         * Set the max vector - the max corner of a bounding box.
         *
         * @param vector The vector
         * @return The builder
         */
        public Builder maxVector(Vector vector) {
            this.maxVector = vector;
            return this;
        }

        /**
         * Add a material.
         *
         * @param material The material
         * @return The builder
         */
        public Builder material(Material material) {
            this.materials.add(material);
            return this;
        }

        /**
         * Add materials.
         *
         * @param materials The materials
         * @return The builder
         */
        public Builder materials(Collection<Material> materials) {
            this.materials.addAll(materials);
            return this;
        }

        /**
         * Add an entity type.
         *
         * @param entityType The entity type
         * @return The builder
         */
        public Builder entityType(EntityType entityType) {
            this.entityTypes.add(entityType);
            return this;
        }

        /**
         * Add an entity type.
         *
         * @param entityTypes The entity types
         * @return The builder
         */
        public Builder entityTypes(Collection<EntityType> entityTypes) {
            this.entityTypes.addAll(entityTypes);
            return this;
        }

        /**
         * Add a player by name.
         *
         * @param playerName The player name
         * @return The builder
         */
        public Builder playerByName(String playerName) {
            this.playerNames.add(playerName);
            return this;
        }

        /**
         * Set the lower-bound timestamp.
         *
         * @param since The timestamp
         * @return The builder
         */
        public Builder since(long since) {
            this.since = since;
            return this;
        }

        /**
         * Set the upper-bound timestamp.
         *
         * @param before The timestamp
         * @return The builder
         */
        public Builder before(long before) {
            this.before = before;
            return this;
        }

        /**
         * Set the offset.
         *
         * @param offset The offset
         * @return The builder
         */
        public Builder offset(int offset) {
            this.offset = offset;
            return this;
        }

        /**
         * Set the limit.
         *
         * @param limit The limit
         * @return The builder
         */
        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Set the sort direction. Defaults to descending.
         *
         * @param sort The sort direction.
         * @return The builder
         */
        public Builder sort(Sort sort) {
            this.sort = sort;
            return this;
        }

        /**
         * Build the activity query.
         *
         * @return The activity query
         */
        public ActivityQuery build() {
            return new ActivityQuery(
                isLookup, grouped, actionTypes, location, worldUuid,
                minVector, maxVector, materials, entityTypes, playerNames, since, before, offset, limit, sort);
        }
    }
}
