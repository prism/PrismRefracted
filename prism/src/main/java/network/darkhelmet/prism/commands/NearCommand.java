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
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.services.displays.DisplayService;
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Command("prism")
@Alias("pr")
public class NearCommand extends CommandBase {
    /**
     * The prism configuration.
     */
    private final PrismConfiguration prismConfig;

    /**
     * The storage adapter.
     */
    private final IStorageAdapter storageAdapter;

    /**
     * The display service.
     */
    private final DisplayService displayService;

    /**
     * Construct the near command.
     *
     * @param prismConfig The prism configuration
     * @param storageAdapter The storage adapter
     */
    @Inject
    public NearCommand(PrismConfiguration prismConfig, IStorageAdapter storageAdapter, DisplayService displayService) {
        this.prismConfig = prismConfig;
        this.storageAdapter = storageAdapter;
        this.displayService = displayService;
    }

    /**
     * Run the near command. Searches for records nearby the player.
     *
     * @param player The player
     */
    @SubCommand("near")
    public void onNear(final Player player) {
        Location loc = player.getLocation();
        int radius = prismConfig.nearRadius();

        Vector minVector = LocationUtils.getMinVector(loc, radius);
        Vector maxVector = LocationUtils.getMaxVector(loc, radius);

        final ActivityQuery query = ActivityQuery.builder().minVector(minVector).maxVector(maxVector).build();
        Prism.newChain().async(() -> {
            try {
                PaginatedResults<IActivity> paginatedResults = storageAdapter.queryActivitiesPaginated(query);

                displayService.show(player, paginatedResults);
            } catch (Exception e) {
                Prism.getInstance().handleException(e);
            }
        }).execute();
    }
}