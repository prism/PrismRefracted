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

package network.darkhelmet.prism.services.filters;

import java.util.List;
import java.util.UUID;

import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.filters.FilterBehavior;

public class ActivityFilter {
    /**
     * The behavior of this filter.
     */
    private FilterBehavior behavior;

    /**
     * All world UUIDs.
     */
    private final List<UUID> worldUuids;

    /**
     * Construct a new activity filter.
     *
     * @param behavior The behavior
     * @param worldUuids The world UUIDs
     */
    public ActivityFilter(FilterBehavior behavior, List<UUID> worldUuids) {
        this.behavior = behavior;
        this.worldUuids = worldUuids;
    }

    /**
     * Check if this filter allows the activity.
     *
     * @param activity The activity
     * @return True if the filter allows it
     */
    public boolean allows(IActivity activity) {
        // Worlds matched...
        boolean worldMatched =  worldsMatch(activity);

        if (ignoring() && worldMatched) {
            // Reject when filters match and we're ignoring
            return false;
        } else if (allowing() && !worldMatched) {
            // Reject when we're allowing but filters don't match
            return false;
        }

        return true;
    }

    /**
     * Check if filter mode is "allow".
     *
     * @return True if mode is "allow"
     */
    private boolean allowing() {
        return behavior.equals(FilterBehavior.ALLOW);
    }

    /**
     * Check if filter mode is "ignore".
     *
     * @return True if mode is "ignore"
     */
    private boolean ignoring() {
        return behavior.equals(FilterBehavior.IGNORE);
    }

    /**
     * Check if any worlds match the activity.
     *
     * <p>If none listed, the filter will match all.</p>
     *
     * @param activity The activity
     * @return True if world UUID matched
     */
    private boolean worldsMatch(IActivity activity) {
        return worldUuids.isEmpty() || worldUuids.contains(activity.location().getWorld().getUID());
    }
}
