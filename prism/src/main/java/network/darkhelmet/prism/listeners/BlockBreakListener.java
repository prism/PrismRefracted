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

package network.darkhelmet.prism.listeners;

import com.google.inject.Inject;

import java.util.ArrayList;

import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.expectations.ExpectationService;
import network.darkhelmet.prism.services.filters.FilterService;
import network.darkhelmet.prism.services.recording.RecordingQueue;
import network.darkhelmet.prism.utils.BlockUtils;
import network.darkhelmet.prism.utils.EntityUtils;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The action registry.
     */
    private final IActionRegistry actionRegistry;

    /**
     * The expectation service.
     */
    private final ExpectationService expectationService;

    /**
     * The filter service.
     */
    private final FilterService filterService;

    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     * @param actionRegistry The action registry
     * @param expectationService The expectation service
     * @param filterService The filter service
     */
    @Inject
    public BlockBreakListener(
            ConfigurationService configurationService,
            IActionRegistry actionRegistry,
            ExpectationService expectationService,
            FilterService filterService) {
        this.configurationService = configurationService;
        this.actionRegistry = actionRegistry;
        this.expectationService = expectationService;
        this.filterService = filterService;
    }

    /**
     * Listens for block break events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = BlockUtils.rootBlock(event.getBlock());

        // Find any hanging entities.
        if (configurationService.prismConfig().actions().hangingBreak()) {
            for (Entity hanging : EntityUtils.hangingEntities(block.getLocation(), 2)) {
                expectationService.expect(hanging, player);
            }
        }

        // Ignore if this event is disabled
        if (!configurationService.prismConfig().actions().blockBreak()) {
            return;
        }

        // Record all blocks that will detach
        for (Block detachable : BlockUtils.detachables(new ArrayList<>(), block)) {
            recordBlockBreak(detachable, player);
        }

        // Record all blocks that will fall
        for (Block faller : BlockUtils.gravity(new ArrayList<>(), block)) {
            recordBlockBreak(faller, player);
        }

        // Record this block
        recordBlockBreak(block, player);
    }

    /**
     * Convenience method for recording a block break by a player.
     *
     * @param block The broken block
     * @param player The player
     */
    protected void recordBlockBreak(Block block, Player player) {
        // Build the action
        final IAction action = actionRegistry.createBlockAction(ActionRegistry.BLOCK_BREAK, block.getState());

        // Build the block break by player activity
        final IActivity activity = Activity.builder()
            .action(action).location(block.getLocation()).cause(player).build();

        if (filterService.allows(activity)) {
            RecordingQueue.addToQueue(activity);
        }
    }
}