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

import java.util.Optional;

import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.services.ExpectationService;
import network.darkhelmet.prism.services.recording.RecordingQueue;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

public class HangingBreakListener implements Listener {
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
    public HangingBreakListener(
            PrismConfiguration prismConfig,
            IActionRegistry actionRegistry,
            ExpectationService expectationService) {
        this.prismConfig = prismConfig;
        this.actionRegistry = actionRegistry;
        this.expectationService = expectationService;
    }

    /**
     * Listens to hanging break events.
     *
     * <p>Hanging items broken directly by a player fall under HangingBreakByEntityEvent.
     * This is merely here to capture indirect causes (physics) for when they detach
     * from a block.</p>
     *
     * @param event HangingBreakEvent The hanging break event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingBreakEvent(final HangingBreakEvent event) {
        // Ignore if this event is disabled
        if (!prismConfig.actions().hangingBreak()) {
            return;
        }

        // Ignore other causes. Entity cause already handled.
        if (!event.getCause().equals(HangingBreakEvent.RemoveCause.PHYSICS)) {
            return;
        }

        final Hanging hanging = event.getEntity();

        Optional<Object> expectation = expectationService.expectation(hanging);
        expectation.ifPresent(o -> {
            // Queue a recording
            recordHangingBreak(hanging, o);

            // Remove from cache
            expectationService.metExpectation(hanging);
        });
    }

    /**
     * Record a hanging entity break.
     *
     * @param hanging The hanging entity
     * @param cause The cause
     */
    protected void recordHangingBreak(Entity hanging, Object cause) {
        final IAction action = actionRegistry.createEntityAction(ActionRegistry.HANGING_BREAK, hanging);

        // Build the block break by player activity
        final IActivity activity = Activity.builder()
            .action(action).location(hanging.getLocation()).cause(cause).build();

        RecordingQueue.addToQueue(activity);
    }
}
