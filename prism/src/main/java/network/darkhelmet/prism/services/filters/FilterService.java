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

package network.darkhelmet.prism.services.filters;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.filters.IFilterService;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.configuration.FilterConfiguartion;
import network.darkhelmet.prism.utils.MaterialTag;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

public class FilterService implements IFilterService {
    /**
     * The logger.
     */
    private final Logger logger;

    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * Cache all filters.
     */
    private final List<ActivityFilter> filters = new ArrayList<>();

    /**
     * Construct a new filter service.
     *
     * @param logger The logger
     * @param configurationService The configuration service
     */
    @Inject
    public FilterService(Logger logger, ConfigurationService configurationService) {
        this.logger = logger;
        this.configurationService = configurationService;

        loadFilters();
    }

    /**
     * Load all filters from the config.
     */
    public void loadFilters() {
        filters.clear();

        // Convert all configured filters into Filter objects
        for (FilterConfiguartion config : configurationService.prismConfig().filters()) {
            // Behavior
            if (config.behavior() == null) {
                logger.warn("Filter error: No behavior defined. Behavior must be either IGNORE or ALLOW.");

                continue;
            }

            // Worlds
            List<UUID> worldUuids = new ArrayList<>();
            for (String worldName : config.worlds()) {
                World world = Bukkit.getServer().getWorld(worldName);
                if (world == null) {
                    logger.warn("Filter error: No world found by name {}.", worldName);

                    continue;
                }

                worldUuids.add(world.getUID());
            }

            // Materials
            MaterialTag materialTag = new MaterialTag();
            for (String materialKey : config.materials()) {
                try {
                    Material material = Material.valueOf(materialKey.toUpperCase(Locale.ENGLISH));
                    materialTag.append(material);
                } catch (IllegalArgumentException e) {
                    logger.warn("Filter error: No material matching {}", materialKey);
                }
            }

            filters.add(new ActivityFilter(config.behavior(), worldUuids, config.actions(), materialTag));
        }
    }

    /**
     * Pass an activity through filters. If any disallow it, reject.
     *
     * @param activity The activity
     * @return True if filters rejected the activity
     */
    public boolean allows(IActivity activity) {
        for (ActivityFilter filter : filters) {
            // If any filter rejects this activity, it's done for.
            if (!filter.allows(activity)) {
                return false;
            }
        }

        return true;
    }
}
