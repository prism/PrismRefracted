package network.darkhelmet.prism.api.activity;

import network.darkhelmet.prism.api.actions.Action;
import org.bukkit.Location;

public record Activity(Action action, Location location, Object cause, Long timestamp) {
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
         * The action
         */
        private Action action;

        /**
         * The cause, if any
         */
        private Object cause;

        /**
         * The location, if any
         */
        private Location location;

        /**
         * The timestamp
         */
        private Long timestamp = System.currentTimeMillis();

        /**
         * Set an action.
         *
         * @param action The action
         * @return The builder
         */
        public Builder action(Action action) {
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
         * Build the final activity.
         *
         * @return The activity
         */
        public Activity build() {
            return new Activity(action, location, cause, timestamp);
        }
    }
}
