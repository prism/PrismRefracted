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

package network.darkhelmet.prism.api.storage;

import java.util.List;

import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;

public interface IStorageAdapter {
    /**
     * Close any connections. May not be applicable to the chosen storage.
     */
    void close();

    /**
     * Creates a new batch manager.
     *
     * @return The batch
     */
    IActivityBatch createActivityBatch();

    /**
     * Query activities in a format intended for information display.
     *
     * @param query The activity query
     * @return Paginated list of activities
     * @throws Exception Storage layer exception
     */
    PaginatedResults<IActivity> queryActivitiesAsInformation(ActivityQuery query) throws Exception;

    /**
     * Query activities in a format for world modification.
     *
     * @param query The activity query
     * @return List of activities
     * @throws Exception Storage layer exception
     */
    List<IActivity> queryActivitiesAsModification(ActivityQuery query) throws Exception;

    /**
     * Check whether this storage system is enabled and ready.
     *
     * @return True if successfully initialized.
     */
    boolean ready();
}
