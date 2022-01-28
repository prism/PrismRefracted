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

import network.darkhelmet.prism.api.activities.IActivity;

public interface IActivityBatch {
    /**
     * Start a new batch.
     *
     * @throws Exception Storage layer exception
     */
    void startBatch() throws Exception;

    /**
     * Add an activity object to the batch.
     *
     * @param activity The activity
     * @throws Exception Storage layer exception
     */
    void add(IActivity activity) throws Exception;

    /**
     * Commit the batch.
     *
     * @throws Exception Storage layer exception
     */
    void commitBatch() throws Exception;
}
