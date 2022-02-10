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

package network.darkhelmet.prism.services.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class PrismConfiguration {
    @Comment("""
        Actions are in-game events/changes that Prism can record data for.
        Some are purely informational, some can be reversed/restored.
        Disabling any here will completely prevent prism from recording them.
        """)
    private ActionsConfiguration actions = new ActionsConfiguration();

    @Comment("Enable plugin debug mode. Produces extra logging to help diagnose issues.")
    private boolean debug = false;

    @Comment("""
        The default locale for plugin messages. Messages given to players
        will use their client locale settings.
        """)
    private Locale defaultLocale = Locale.US;

    @Comment("""
            Filters allow fine-grained control over what prism records.
            See the wiki for documentation.
            """)
    private List<FilterConfiguartion> filters = new ArrayList<>();

    @Comment("Sets the default radius to use when searching for nearby activity.")
    private int nearRadius = 5;

    @Comment("Limits how many results are shown \"per page\" when doing lookups.")
    private int perPage = 5;

    @Comment("""
            Lookup queries are cached so that they can be re-used or paginated.
            This value (duration in ticks, default = 5 minutes) determines how
            long they're held in memory before being discarded.
            """)
    private long lookupExpiration = 5 * 60 * 20;

    /**
     * Get the actions config.
     *
     * @return The actions config
     */
    public ActionsConfiguration actions() {
        return actions;
    }

    /**
     * Get the filters config.
     *
     * @return The filters config
     */
    public List<FilterConfiguartion> filters() {
        return filters;
    }

    /**
     * Get the debug setting.
     *
     * @return True if debug enabled.
     */
    public boolean debug() {
        return debug;
    }

    /**
     * Get the default locale.
     */
    public Locale defaultLocale() {
        return defaultLocale;
    }

    /**
     * Get the near radius.
     *
     * @return The near radius
     */
    public int nearRadius() {
        return nearRadius;
    }

    /**
     * Get the per page limit.
     *
     * @return The per page limit
     */
    public int perPage() {
        return perPage;
    }

    /**
     * Get the lookup expiration.
     *
     * @return The lookup expiration
     */
    public long lookupExpiration() {
        return lookupExpiration;
    }
}