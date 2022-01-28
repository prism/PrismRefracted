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

package network.darkhelmet.prism.config;

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
    private ActionsConfig actions = new ActionsConfig();

    @Comment("Enable plugin debug mode. Produces extra logging to help diagnose issues.")
    private boolean debug = false;

    @Comment("""
        The default locale for plugin messages. Messages given to players
        will use their client locale settings.
        """)
    private Locale defaultLocale = Locale.US;

    @Comment("Sets the default radius to use when searching for nearby activity.")
    private int nearRadius = 5;

    @Comment("Configure in-game command outputs.")
    private OutputConfiguration outputs = new OutputConfiguration();

    /**
     * Get the actions config.
     *
     * @return The actions config
     */
    public ActionsConfig actions() {
        return actions;
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
     * Get the outout configuration.
     *
     * @return The outputs
     */
    public OutputConfiguration outputs() {
        return outputs;
    }
}