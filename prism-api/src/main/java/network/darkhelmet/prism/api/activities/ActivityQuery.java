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

public class ActivityQuery {
    public enum Sort {
        ASCENDING, DESCENDING
    }

    /**
     * The minimum vector. Sets a corner of a bounding box.
     */
    private Vector minVector;

    /**
     * The maximum vector. Sets a corner of a bounding box.
     */
    private Vector maxVector;

    /**
     * The sort direction.
     */
    private Sort sort;

    /**
     * Construct an activity query.
     *
     * @param minVector The minimum vector
     * @param maxVector The maximum vector
     */
    public ActivityQuery(Vector minVector, Vector maxVector, Sort sort) {
        this.minVector = minVector;
        this.maxVector = maxVector;
        this.sort = sort;
    }

    /**
     * Get the minumum vector.
     *
     * @return The minumum vector
     */
    public Vector minVector() {
        return minVector;
    }

    /**
     * Get the maximum vector.
     *
     * @return The maximum vector
     */
    public Vector maxVector() {
        return maxVector;
    }

    /**
     * Get the sort direction.
     *
     * @return The sort direction
     */
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
         * The minimum vector.
         */
        private Vector minVector;

        /**
         * The maximum vector.
         */
        private Vector maxVector;

        /**
         * The sort direction.
         */
        private Sort sort = Sort.DESCENDING;

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
            return new ActivityQuery(minVector, maxVector, sort);
        }
    }
}
