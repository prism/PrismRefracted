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

import network.darkhelmet.prism.api.services.wands.IWand;
import network.darkhelmet.prism.services.wands.WandService;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {
    /**
     * The wand service.
     */
    private final WandService wandService;

    /**
     * Construct the listener.
     *
     * @param wandService The wand service
     */
    @Inject
    public PlayerInteractListener(WandService wandService) {
        this.wandService = wandService;
    }

    /**
     * Listen to player interact events.
     *
     * @param event Tne event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        // Ignore if block is null (can't get location)
        // or if the event is fired for an off-hand click.
        // (Block will be null when clicking air)
        if (block == null || (event.getHand() != null && !event.getHand().equals(EquipmentSlot.HAND))) {
            return;
        }

        // Check if the player has a wand
        Optional<IWand> wand = wandService.getWand(player);
        if (wand.isPresent()) {
            // Left click = block's location
            // Right click = location of block connected to the clicked block face
            Location targetLocation = block.getLocation();
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                targetLocation = block.getRelative(event.getBlockFace()).getLocation();
            }

            // Use the wand
            wand.get().use(targetLocation);

            // Cancel the event
            event.setCancelled(true);
        }
    }
}
