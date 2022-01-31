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

package network.darkhelmet.prism.commands;

import com.google.inject.Inject;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import network.darkhelmet.prism.api.services.wands.IWand;
import network.darkhelmet.prism.api.services.wands.WandMode;
import network.darkhelmet.prism.services.wands.WandService;

import org.bukkit.entity.Player;

@Command("prism")
@Alias("pr")
public class WandCommand extends CommandBase {
    /**
     * The wand service.
     */
    private final WandService wandService;

    /**
     * Construct the wand command.
     *
     * @param wandService The wand service
     */
    @Inject
    public WandCommand(WandService wandService) {
        this.wandService = wandService;
    }

    /**
     * Toggle a wand.
     *
     * @param player The player
     * @param wandMode The wand mode
     */
    @SubCommand("wand")
    @Completion({"#wands"})
    public void onWand(final Player player, @Optional WandMode wandMode) {
        // If no wand mode selected, yet player has an active wand, toggle it off
        if (wandMode == null && wandService.hasActiveWand(player)) {
            wandService.deactivateWand(player);

            return;
        }

        // Set mode if none selected
        wandMode = wandMode == null ? WandMode.INSPECT : wandMode;

        java.util.Optional<IWand> activeWand = wandService.getWand(player);
        if (activeWand.isPresent()) {
            if (activeWand.get().mode().equals(wandMode)) {
                // If the wand modes match, deactivate
                wandService.deactivateWand(player);
            } else {
                // If modes differ, just switch modes
                wandService.switchMode(player, wandMode);
            }

            return;
        }

        // Activate wand
        wandService.activateWand(player, wandMode);
    }
}