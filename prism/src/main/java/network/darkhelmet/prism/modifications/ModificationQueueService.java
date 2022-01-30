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

package network.darkhelmet.prism.modifications;

import java.util.List;

import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.modifications.IModificationQueue;
import network.darkhelmet.prism.api.modifications.IModificationQueueService;
import network.darkhelmet.prism.api.modifications.ModificationQueueResult;

import org.bukkit.command.CommandSender;

public class ModificationQueueService implements IModificationQueueService {
    /**
     * Cache the owner of the current queue, if any.
     */
    private CommandSender owner = null;

    /**
     * Cache the current queue, if any.
     */
    private IModificationQueue currentQueue = null;

    @Override
    public boolean queueAvailable() {
        return currentQueue == null;
    }

    @Override
    public IModificationQueue newRollbackQueue(CommandSender owner, List<IActivity> modifications) {
        if (!queueAvailable()) {
            throw new IllegalStateException("No queue available until current queue finished.");
        }

        this.owner = owner;
        this.currentQueue = new Rollback(modifications, this::onComplete);
        return this.currentQueue;
    }

    @Override
    public IModificationQueue newRestoreQueue(CommandSender owner, List<IActivity> modifications) {
        if (!queueAvailable()) {
            throw new IllegalStateException("No queue available until current queue finished.");
        }

        this.owner = owner;
        this.currentQueue = new Restore(modifications, this::onComplete);
        return this.currentQueue;
    }

    /**
     * On queue completion, handle some cleanup.
     *
     * @param result Modification queue result
     */
    protected void onComplete(ModificationQueueResult result) {
        this.owner = null;
        this.currentQueue = null;
    }
}
