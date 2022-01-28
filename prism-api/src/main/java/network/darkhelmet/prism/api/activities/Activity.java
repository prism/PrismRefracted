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

import network.darkhelmet.prism.api.actions.IAction;

import org.bukkit.Location;

public record Activity(IAction action, Location location, Object cause, long timestamp) implements IActivity {
    /**
     * Get a new builder.
     *
     * @return The activity builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * The action.
         */
        private IAction action;

        /**
         * The cause, if any.
         */
        private Object cause;

        /**
         * The location, if any.
         */
        private Location location;

        /**
         * The timestamp.
         */
        private long timestamp = System.currentTimeMillis();

        /**
         * Set an action.
         *
         * @param action The action
         * @return The builder
         */
        public Builder action(IAction action) {
            this.action = action;
            return this;
        }

        /**
         * Set a cause.
         *
         * @param cause The cause
         * @return The builder
         */
        public Builder cause(Object cause) {
            this.cause = cause;
            return this;
        }

        /**
         * Set a location.
         *
         * @param location The location
         * @return The builder
         */
        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        /**
         * Set a timestamp.
         *
         * @param timestamp The timestamp
         * @return The builder
         */
        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Build the final activity.
         *
         * @return The activity
         */
        public IActivity build() {
            return new Activity(action, location, cause, timestamp);
        }
    }
}
