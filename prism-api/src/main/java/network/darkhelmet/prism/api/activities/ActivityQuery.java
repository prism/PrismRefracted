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

import org.bukkit.util.Vector;

public record ActivityQuery(
    boolean isLookup,
    Vector minVector,
    Vector maxVector,
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
     * Get the minimum vector.
     *
     * @return The minimum vector
     */
    @Override
    public Vector minVector() {
        return minVector;
    }

    /**
     * Get the maximum vector.
     *
     * @return The maximum vector
     */
    @Override
    public Vector maxVector() {
        return maxVector;
    }

    /**
     * Get the sort direction.
     *
     * @return The sort direction
     */
    @Override
    public Sort sort() {
        return sort;
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
         * Indicate this is a lookup query.
         */
        private boolean isLookup = true;

        /**
         * The minimum vector.
         */
        private Vector minVector;

        /**
         * The maximum vector.
         */
        private Vector maxVector;

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
         * Set whether this is a lookup. The query results can be grouped
         * and the order by is different.
         *
         * @param isLookup If lookup
         * @return The builder
         */
        public Builder setLookup(boolean isLookup) {
            this.isLookup = isLookup;
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
            return new ActivityQuery(isLookup, minVector, maxVector, offset, limit, sort);
        }
    }
}
