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

package network.darkhelmet.prism.services.wands;

import com.google.inject.Inject;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.wands.IWand;
import network.darkhelmet.prism.api.services.wands.WandMode;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.services.displays.DisplayService;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InspectionWand implements IWand {
    /**
     * The prism config.
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
     * The owner.
     */
    private Player owner;

    /**
     * Construct a new inspection wand.
     *
     * @param prismConfig The prism config
     * @param storageAdapter The storage adapter
     * @param displayService The display server
     */
    @Inject
    public InspectionWand(
            PrismConfiguration prismConfig,
            IStorageAdapter storageAdapter,
            DisplayService displayService) {
        this.storageAdapter = storageAdapter;
        this.displayService = displayService;
        this.prismConfig = prismConfig;
    }

    @Override
    public WandMode mode() {
        return WandMode.INSPECT;
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public void use(Location location) {
        final ActivityQuery query = ActivityQuery.builder().location(location).limit(prismConfig.perPage()).build();

        Prism.newChain().async(() -> {
            try {
                PaginatedResults<IActivity> paginatedResults = storageAdapter.queryActivitiesAsInformation(query);

                displayService.show(owner, paginatedResults);
            } catch (Exception e) {
                Prism.getInstance().handleException(e);
            }
        }).execute();
    }
}
