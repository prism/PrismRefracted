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
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.filters.FilterService;
import network.darkhelmet.prism.services.recording.RecordingQueue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The action registry.
     */
    private final IActionRegistry actionRegistry;

    /**
     * The filter service.
     */
    private final FilterService filterService;

    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     * @param actionRegistry The action registry
     * @param filterService The filter service
     */
    @Inject
    public PlayerDropItemListener(
            ConfigurationService configurationService,
            IActionRegistry actionRegistry,
            FilterService filterService) {
        this.configurationService = configurationService;
        this.actionRegistry = actionRegistry;
        this.filterService = filterService;
    }

    /**
     * Listens to (player) item drop events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        // Ignore if this event is disabled
        if (!configurationService.prismConfig().actions().itemDrop()) {
            return;
        }

        // Build the action
        final IAction action = actionRegistry.createItemStackAction(ActionRegistry.ITEM_DROP,
            event.getItemDrop().getItemStack());

        final IActivity activity = Activity.builder()
            .action(action).cause(event.getPlayer()).location(event.getPlayer().getLocation()).build();

        if (filterService.allows(activity)) {
            RecordingQueue.addToQueue(activity);
        }
    }
}
