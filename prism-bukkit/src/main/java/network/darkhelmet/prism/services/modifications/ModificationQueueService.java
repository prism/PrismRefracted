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

package network.darkhelmet.prism.services.modifications;

import com.google.inject.Inject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import net.jodah.expiringmap.ExpiringMap;

import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.modifications.IModificationQueue;
import network.darkhelmet.prism.api.services.modifications.IModificationQueueService;
import network.darkhelmet.prism.api.services.modifications.ModificationQueueMode;
import network.darkhelmet.prism.api.services.modifications.ModificationQueueResult;
import network.darkhelmet.prism.api.services.modifications.ModificationResult;
import network.darkhelmet.prism.injection.factories.IRestoreFactory;
import network.darkhelmet.prism.injection.factories.IRollbackFactory;
import network.darkhelmet.prism.services.modifications.state.BlockStateChange;

import org.bukkit.entity.Player;

public class ModificationQueueService implements IModificationQueueService {
    /**
     * Cache the current queue, if any.
     */
    private IModificationQueue currentQueue = null;

    /**
     * The restore factory.
     */
    private final IRestoreFactory restoreFactory;

    /**
     * The rollback factory.
     */
    private final IRollbackFactory rollbackFactory;

    /**
     * A cache of recently used queues.
     */
    Map<Object, ModificationQueueResult> usedQueues = ExpiringMap.builder()
        .maxSize(4)
        .expiration(5, TimeUnit.MINUTES)
        .expirationListener((owner, result) -> cancelQueueForOwner(owner))
        .build();

    /**
     * Constructor.
     *
     * @param restoreFactory The restore factory
     * @param rollbackFactory The rollback factory.
     */
    @Inject
    public ModificationQueueService(
        IRestoreFactory restoreFactory,
        IRollbackFactory rollbackFactory) {
        this.restoreFactory = restoreFactory;
        this.rollbackFactory = rollbackFactory;
    }

    @Override
    public boolean queueAvailable() {
        return currentQueue == null;
    }

    @Override
    public boolean cancelQueueForOwner(Object owner) {
        if (currentQueue != null && currentQueue.owner().equals(owner)) {
            this.currentQueue.destroy();
            this.currentQueue = null;

            return true;
        }

        if (usedQueues.containsKey(owner)) {
            cancelPreview(owner, usedQueues.get(owner));

            usedQueues.remove(owner);

            return true;
        }

        return false;
    }

    /**
     * Re-send live blocks for ones we faked.
     *
     * @param owner The owner
     * @param queueResult The queue result
     */
    protected void cancelPreview(Object owner, ModificationQueueResult queueResult) {
        if (!queueResult.mode().equals(ModificationQueueMode.PLANNING)) {
            return;
        }

        if (owner instanceof Player player) {
            for (final Iterator<ModificationResult> iterator = queueResult.results().listIterator();
                 iterator.hasNext(); ) {
                final ModificationResult result = iterator.next();

                if (result.stateChange() instanceof BlockStateChange blockStateChange) {
                    player.sendBlockChange(
                        blockStateChange.oldState().getLocation(), blockStateChange.oldState().getBlockData());
                }

                iterator.remove();
            }
        }
    }

    @Override
    public IModificationQueue currentQueue() {
        return currentQueue;
    }

    @Override
    public Optional<IModificationQueue> currentQueueForOwner(Object owner) {
        if (currentQueue != null && currentQueue.owner().equals(owner)) {
            return Optional.of(currentQueue);
        }

        return Optional.empty();
    }

    @Override
    public IModificationQueue newRollbackQueue(Object owner, List<IActivity> modifications) {
        if (!queueAvailable()) {
            throw new IllegalStateException("No queue available until current queue finished.");
        }

        // Cancel any cached queues
        cancelQueueForOwner(owner);

        this.currentQueue = rollbackFactory.create(owner, modifications, this::onEnd);

        return this.currentQueue;
    }

    @Override
    public IModificationQueue newRestoreQueue(Object owner, List<IActivity> modifications) {
        if (!queueAvailable()) {
            throw new IllegalStateException("No queue available until current queue finished.");
        }

        // Cancel any cached queues
        cancelQueueForOwner(owner);

        this.currentQueue = restoreFactory.create(owner, modifications, this::onEnd);

        return this.currentQueue;
    }

    /**
     * On queue end, handle some cleanup.
     *
     * @param result Modification queue result
     */
    protected void onEnd(ModificationQueueResult result) {
        usedQueues.put(currentQueue.owner(), result);
        this.currentQueue.destroy();
        this.currentQueue = null;
    }
}