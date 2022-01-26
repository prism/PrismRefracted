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

import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.services.configuration.PrismConfiguration;
import network.darkhelmet.prism.services.expectations.ExpectationService;
import network.darkhelmet.prism.services.recording.RecordingQueue;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    /**
     * The prism config.
     */
    private final PrismConfiguration prismConfig;

    /**
     * The action registry.
     */
    private final IActionRegistry actionRegistry;

    /**
     * The expectation service.
     */
    private final ExpectationService expectationService;

    /**
     * Construct the listener.
     *
     * @param prismConfig The prism config
     */
    @Inject
    public BlockPlaceListener(
            PrismConfiguration prismConfig,
            IActionRegistry actionRegistry,
            ExpectationService expectationService) {
        this.prismConfig = prismConfig;
        this.actionRegistry = actionRegistry;
        this.expectationService = expectationService;
    }

    /**
     * Listens for block break events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        // Ignore if this event is disabled
        if (!prismConfig.actions().blockPlace()) {
            return;
        }

        Block blockPlaced = event.getBlockPlaced();
        final BlockState replacedState = event.getBlockReplacedState();

        // Build the action
        final IAction action = actionRegistry.createBlockAction(
            ActionRegistry.BLOCK_PLACE, blockPlaced.getState(), replacedState);

        // Build the block break by player activity
        final IActivity activity = Activity.builder()
            .action(action).location(blockPlaced.getLocation()).cause(player).build();

        RecordingQueue.addToQueue(activity);
    }
}
