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
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.services.recording.RecordingQueue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    /**
     * The prism config.
     */
    private PrismConfiguration prismConfig;

    /**
     * The action registry.
     */
    private final IActionRegistry actionRegistry;

    /**
     * Construct the listener.
     *
     * @param prismConfig The prism config
     */
    @Inject
    public PlayerDropItemListener(PrismConfiguration prismConfig, IActionRegistry actionRegistry) {
        this.prismConfig = prismConfig;
        this.actionRegistry = actionRegistry;
    }

    /**
     * Listens to (player) item drop events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        // Ignore if this event is disabled
        if (!prismConfig.actions().itemDrop()) {
            return;
        }

        // Build the action
        final IAction action = actionRegistry.createItemStackAction(ActionRegistry.ITEM_DROP,
            event.getItemDrop().getItemStack());

        final IActivity activity = Activity.builder()
            .action(action).cause(event.getPlayer()).location(event.getPlayer().getLocation()).build();

        RecordingQueue.addToQueue(activity);
    }
}
