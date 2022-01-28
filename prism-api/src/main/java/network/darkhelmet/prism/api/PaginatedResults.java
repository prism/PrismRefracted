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

package network.darkhelmet.prism.api;

import java.util.List;

public class PaginatedResults<T> {
    /**
     * The segmented results.
     */
    List<T> results;

    /**
     * Construct a new paginated results object.
     *
     * @param results A list of results
     */
    public PaginatedResults(List<T> results) {
        this.results = results;
    }

    /**
     * Check if the results are empty.
     *
     * @return True if no results
     */
    public boolean isEmpty() {
        return results.isEmpty();
    }

    /**
     * Get the results.
     *
     * @return The results
     */
    public List<T> results() {
        return results;
    }
}
