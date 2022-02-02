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

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.displays.DisplayService;
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Command(value = "prism", alias = {"pr"})
public class NearCommand extends BaseCommand {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

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
     * @param configurationService The configuration service
     * @param storageAdapter The storage adapter
     * @param displayService The display service
     */
    @Inject
    public NearCommand(
            ConfigurationService configurationService,
            IStorageAdapter storageAdapter,
            DisplayService displayService) {
        this.configurationService = configurationService;
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
        Vector minVector = LocationUtils.getMinVector(loc, configurationService.prismConfig().nearRadius());
        Vector maxVector = LocationUtils.getMaxVector(loc, configurationService.prismConfig().nearRadius());

        final ActivityQuery query = ActivityQuery.builder().world(loc.getWorld().getUID())
            .minVector(minVector).maxVector(maxVector).limit(configurationService.prismConfig().perPage()).build();
        Prism.newChain().async(() -> {
            try {
                PaginatedResults<IActivity> paginatedResults = storageAdapter.queryActivitiesAsInformation(query);

                displayService.show(player, paginatedResults);
            } catch (Exception e) {
                Prism.getInstance().handleException(e);
            }
        }).execute();
    }
}