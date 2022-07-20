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

import network.darkhelmet.prism.actions.ActionFactory;
import network.darkhelmet.prism.actions.types.ActionTypeRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.ISingleActivity;
import network.darkhelmet.prism.api.services.expectations.ExpectationType;
import network.darkhelmet.prism.api.util.WorldCoordinate;
import network.darkhelmet.prism.loader.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.expectations.ExpectationService;
import network.darkhelmet.prism.services.recording.RecordingService;
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

public class HangingBreakListener extends AbstractListener implements Listener {
    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     * @param actionFactory The action factory
     * @param expectationService The expectation service
     * @param recordingService The recording service
     */
    @Inject
    public HangingBreakListener(
            ConfigurationService configurationService,
            ActionFactory actionFactory,
            ExpectationService expectationService,
            RecordingService recordingService) {
        super(configurationService, actionFactory, expectationService, recordingService);
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
        if (!configurationService.prismConfig().actions().hangingBreak()) {
            return;
        }

        final Hanging hanging = event.getEntity();

        if (event.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)) {
            recordHangingBreak(hanging, "explosion");
        } else if (event.getCause().equals(HangingBreakEvent.RemoveCause.PHYSICS)) {
            // Physics causes. Hopefully find the actual cause through an expectation
            Optional<Object> expectation = expectationService.cacheFor(ExpectationType.DETACH).expectation(hanging);
            expectation.ifPresent(o -> {
                // Queue a recording
                recordHangingBreak(hanging, o);

                // Remove from cache
                expectationService.cacheFor(ExpectationType.DETACH).metExpectation(hanging);
            });
        }
    }

    /**
     * Record a hanging entity break.
     *
     * @param hanging The hanging entity
     * @param cause The cause
     */
    protected void recordHangingBreak(Entity hanging, Object cause) {
        final IAction action = actionFactory.createEntityAction(ActionTypeRegistry.HANGING_BREAK, hanging);

        WorldCoordinate at = LocationUtils.locToWorldCoordinate(hanging.getLocation());

        // Build the activity
        Activity.ActivityBuilder builder = Activity.builder();
        builder.action(action).location(at);

        if (cause instanceof Player player) {
            builder.player(player.getUniqueId(), player.getName());
        } else {
            builder.cause(causeDescriptor(cause));
        }

        ISingleActivity activity = builder.build();
        recordingService.addToQueue(activity);
    }
}