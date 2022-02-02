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

import network.darkhelmet.prism.actions.MaterialAction;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.filters.FilterBehavior;
import network.darkhelmet.prism.utils.MaterialTag;

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
     * Actions.
     */
    private final List<String> actions;

    /**
     * The material tag.
     */
    private final MaterialTag materialTag;

    /**
     * Construct a new activity filter.
     *
     * @param behavior The behavior
     * @param worldUuids The world UUIDs
     * @param actions The actions
     * @param materialTag The material tag
     */
    public ActivityFilter(
            FilterBehavior behavior,
            List<UUID> worldUuids,
            List<String> actions,
            MaterialTag materialTag) {
        this.behavior = behavior;
        this.worldUuids = worldUuids;
        this.actions = actions;
        this.materialTag = materialTag;
    }

    /**
     * Check if this filter allows the activity.
     *
     * @param activity The activity
     * @return True if the filter allows it
     */
    public boolean allows(IActivity activity) {
        boolean actionMatched = actionsMatch(activity);
        boolean worldMatched = worldsMatch(activity);
        boolean materialMatched = materialsMatch(activity);

        // If this filter exists we're guaranteed to require matches.
        // The filter can be either "ALLOW" or "IGNORE" but not both.
        // If any of the criteria are empty, they automatically match.
        // If any criteria were set, we compare against the activity for a match.
        if (allowing()) {
            // If ALLOW mode, all filters must match to approve this
            return worldMatched && actionMatched && materialMatched;
        } else {
            // If IGNORE mode, we *reject* this if all filters match
            return !(worldMatched && actionMatched && materialMatched);
        }
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

    /**
     * Check if any actions match the activity action.
     *
     * <p>If none listed, the filter will match all.</p>
     *
     * @param activity The activity
     * @return True if action key matches
     */
    private boolean actionsMatch(IActivity activity) {
        return actions.isEmpty() || actions.contains(activity.action().type().key());
    }

    /**
     * Check if any materials match the activity action.
     *
     * <p>If none listed, the filter will match all. Ignores non-material actions.</p>
     *
     * @param activity The activity
     * @return True if action material matches
     */
    private boolean materialsMatch(IActivity activity) {
        if (activity.action() instanceof MaterialAction materialAction) {
            return materialTag.isTagged(materialAction.material());
        }

        return true;
    }
}
