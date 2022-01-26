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

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class ActionsConfiguration {
    @Comment("block-break is when a player or entity destroys a block (except from burn/explode).")
    private boolean blockBreak = true;

    @Comment("block-place is when a player or entity places a block.")
    private boolean blockPlace = true;

    @Comment("entity-kill is when an entity (or player) kills another.")
    private boolean entityKill = true;

    @Comment("""
            hanging-break is when an item frame or painting is broken/detached.
            This event will operate if block-break is false, even for detachments.
            """)
    private boolean hangingBreak = true;

    @Comment("item-drop is when a player or block drops an item on the ground.")
    private boolean itemDrop = true;

    /**
     * Get if block-break enabled.
     *
     * @return True if enabled
     */
    public boolean blockBreak() {
        return blockBreak;
    }

    /**
     * Get if block-place enabled.
     *
     * @return True if enabled
     */
    public boolean blockPlace() {
        return blockPlace;
    }

    /**
     * Get if entity-kill enabled.
     *
     * @return True if enabled
     */
    public boolean entityKill() {
        return entityKill;
    }

    /**
     * Get if hanging-break enabled.
     *
     * @return True if enabled
     */
    public boolean hangingBreak() {
        return hangingBreak;
    }

    /**
     * Get if item-drop enabled.
     *
     * @return True if enabled
     */
    public boolean itemDrop() {
        return itemDrop;
    }
}
