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

package network.darkhelmet.prism.api.modifications;

import java.util.List;

import network.darkhelmet.prism.api.activities.IActivity;

import org.bukkit.command.CommandSender;

public interface IModificationQueueService {
    /**
     * Check if the queue is free. Nicer than trying and getting exceptions.
     *
     * @return True if a new queue can be made.
     */
    boolean queueAvailable();

    /**
     * Create a new rollback queue.
     *
     * @param owner The owner
     * @param modifications A list of activities to make modifications
     * @return The rollback queue
     * @throws IllegalStateException If queue can't be created
     */
    IModificationQueue newRollbackQueue(CommandSender owner, List<IActivity> modifications);

    /**
     * Create a new restore queue.
     *
     * @param owner The owner
     * @param modifications A list of activities to make modifications
     * @return The restore queue
     * @throws IllegalStateException If queue can't be created
     */
    IModificationQueue newRestoreQueue(CommandSender owner, List<IActivity> modifications);
}
