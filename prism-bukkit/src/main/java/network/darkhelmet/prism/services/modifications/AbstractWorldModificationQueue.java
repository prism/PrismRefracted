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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;

import network.darkhelmet.prism.PrismBukkit;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.modifications.IModificationQueue;
import network.darkhelmet.prism.api.services.modifications.ModificationQueueMode;
import network.darkhelmet.prism.api.services.modifications.ModificationQueueResult;
import network.darkhelmet.prism.api.services.modifications.ModificationResult;
import network.darkhelmet.prism.api.services.modifications.ModificationResultStatus;
import network.darkhelmet.prism.api.services.modifications.ModificationRuleset;
import network.darkhelmet.prism.loader.services.logging.LoggingService;
import network.darkhelmet.prism.utils.BlockUtils;
import network.darkhelmet.prism.utils.EntityUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;

public abstract class AbstractWorldModificationQueue implements IModificationQueue {
    /**
     * The logging service.
     */
    protected LoggingService loggingService;

    /**
     * The modification ruleset.
     */
    protected ModificationRuleset modificationRuleset;

    /**
     * Manage a queue of pending modifications.
     */
    protected final List<IActivity> modificationsQueue = Collections.synchronizedList(new LinkedList<>());

    /**
     * The onEnd handler.
     */
    protected final Consumer<ModificationQueueResult> onEndCallback;

    /**
     * The owner.
     */
    @Getter
    protected final Object owner;

    @Getter
    protected final ActivityQuery query;

    /**
     * The state.
     */
    @Getter
    protected ModificationQueueMode mode = ModificationQueueMode.UNDECIDED;

    /**
     * Cache the bukkit task id.
     */
    protected int taskId;

    /**
     * Count how many were read from the queue.
     */
    protected int countModificationsRead;

    /**
     * Count how many were applied.
     */
    protected int countApplied = 0;

    /**
     * Count how many were planned.
     */
    protected int countPlanned = 0;

    /**
     * Count how many were skipped.
     */
    protected int countSkipped = 0;

    /**
     * A list of all modification results.
     */
    protected final List<ModificationResult> results = new ArrayList<>();

    /**
     * Construct a new world modification.
     *
     * @param loggingService The logging service
     * @param owner The owner
     * @param query The query
     * @param modifications A list of all modifications
     * @param onEndCallback The ended callback
     */
    public AbstractWorldModificationQueue(
            LoggingService loggingService,
            ModificationRuleset modificationRuleset,
            Object owner,
            ActivityQuery query,
            final List<IActivity> modifications,
            Consumer<ModificationQueueResult> onEndCallback) {
        modificationsQueue.addAll(modifications);
        this.loggingService = loggingService;
        this.modificationRuleset = modificationRuleset;
        this.owner = owner;
        this.query = query;
        this.onEndCallback = onEndCallback;
    }

    /**
     * Apply a modification.
     *
     * @param activity The activity
     * @return The modification result
     */
    protected ModificationResult applyModification(IActivity activity) {
        return ModificationResult.builder().status(ModificationResultStatus.SKIPPED).build();
    }

    /**
     * Apply any pre-modification tasks.
     */
    protected void preProcess(ModificationQueueResult.ModificationQueueResultBuilder builder) {
        if (mode.equals(ModificationQueueMode.COMPLETING)) {
            if (modificationRuleset.removeDrops()
                    && query.worldUuid() != null
                    && query.minCoordinate() != null
                    && query.maxCoordinate() != null) {
                double x1 = query.minCoordinate().x();
                double y1 = query.minCoordinate().y();
                double z1 = query.minCoordinate().z();
                double x2 = query.maxCoordinate().x();
                double y2 = query.maxCoordinate().y();
                double z2 = query.maxCoordinate().z();
                BoundingBox boundingBox = new BoundingBox(x1, y1, z1, x2, y2, z2);

                World world = Bukkit.getWorld(query.worldUuid());
                int count = EntityUtils.removeDropsInRange(world, boundingBox);

                builder.removedDrops(count);
            }

            if (!modificationRuleset.removeBlocks().isEmpty()
                    && query.worldUuid() != null
                    && query.minCoordinate() != null
                    && query.maxCoordinate() != null) {
                double x1 = query.minCoordinate().x();
                double y1 = query.minCoordinate().y();
                double z1 = query.minCoordinate().z();
                double x2 = query.maxCoordinate().x();
                double y2 = query.maxCoordinate().y();
                double z2 = query.maxCoordinate().z();
                BoundingBox boundingBox = new BoundingBox(x1, y1, z1, x2, y2, z2);

                List<Material> materials = modificationRuleset.removeBlocks().stream().map(m ->
                    Material.valueOf(m.toUpperCase())).toList();

                World world = Bukkit.getWorld(query.worldUuid());
                int count = BlockUtils.removeBlocksByMaterial(world, boundingBox, materials).size();

                builder.removedBlocks(count);
            }
        }
    }

    /**
     * Apply any post-modification tasks.
     */
    protected void postProcess(ModificationQueueResult.ModificationQueueResultBuilder builder) {}

    @Override
    public void apply() {
        countModificationsRead = 0;
        this.mode = ModificationQueueMode.COMPLETING;
        execute();
    }

    protected void execute() {
        String queueSizeMsg = "Modification queue beginning application. Queue size: %d";
        loggingService.debug(String.format(queueSizeMsg, modificationsQueue.size()));

        if (!modificationsQueue.isEmpty()) {
            ModificationQueueResult.ModificationQueueResultBuilder builder = ModificationQueueResult.builder()
                .queue(this);

            if (countModificationsRead == 0) {
                preProcess(builder);
            }

            // Schedule a new sync task
            JavaPlugin plugin = PrismBukkit.getInstance().loaderPlugin();
            taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                loggingService.debug("New modification run beginning...");

                int iterationCount = 0;
                final int currentQueueOffset = countModificationsRead;

                if (currentQueueOffset < modificationsQueue.size()) {
                    for (final Iterator<IActivity> iterator = modificationsQueue.listIterator(currentQueueOffset);
                         iterator.hasNext();) {
                        final IActivity activity = iterator.next();

                        // Simulate queue pointer advancement for previews
                        if (mode.equals(ModificationQueueMode.PLANNING)) {
                            countModificationsRead++;
                        }

                        // Limit the absolute max number of steps per execution of this task
                        if (++iterationCount >= modificationRuleset.maxPerTask()) {
                            break;
                        }

                        ModificationResult result = ModificationResult.builder().activity(activity).build();

                        // Delegate reversible modifications to the actions
                        if (activity.action().type().reversible()) {
                            try {
                                result = applyModification(activity);
                            } catch (Exception e) {
                                result = ModificationResult.builder().activity(activity).errored().build();
                                loggingService.handleException(e);
                            }
                        }

                        results.add(result);

                        if (result.status().equals(ModificationResultStatus.PLANNED)) {
                            countPlanned++;
                        } else if (result.status().equals(ModificationResultStatus.APPLIED)) {
                            countApplied++;
                        } else {
                            countSkipped++;
                        }

                        // Remove from the queue if we're not previewing
                        if (mode.equals(ModificationQueueMode.COMPLETING)) {
                            iterator.remove();
                        }
                    }
                }

                // The task for this action is done being used
                if (modificationsQueue.isEmpty() || countModificationsRead >= modificationsQueue.size()) {
                    loggingService.debug("Modification queue fully processed, finishing up.");

                    // Cancel the repeating task
                    Bukkit.getServer().getScheduler().cancelTask(taskId);

                    // Post process
                    postProcess(builder);

                    ModificationQueueResult result = builder.mode(mode)
                        .results(results)
                        .applied(countApplied)
                        .planned(countPlanned)
                        .skipped(countSkipped)
                        .build();

                    onEnd(result);
                }
            }, 0, modificationRuleset.taskDelay());
        }
    }

    @Override
    public void destroy() {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
    }

    /**
     * Called when the modification queue has ended.
     *
     * @param result The result
     */
    protected void onEnd(ModificationQueueResult result) {
        // Execute the callback, letting the caller know we've ended
        onEndCallback.accept(result);
    }
}