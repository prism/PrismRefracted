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

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class OutputConfiguration {
    @Comment("Used to prefix any other messages with <prefix> placeholders.")
    private String prefix = "<dark_gray>(<#ff55ff>\uFF01<dark_gray>) <#ff55ff>Prism<gray> \u300b";

    @Comment("Used for all \"success\" messages.")
    private String success = "<prefix><green><message>";

    @Comment("Used for all \"error\" messages.")
    private String error = "<prefix><red><message>";

    @Comment("Used for all \"info\" messages.")
    private String info = "<prefix><white><message>";

    @Comment("Used for the heading message before outputting a list (of events, etc).")
    private String heading = "<prefix><white><heading> <underlined><#0ccfcb><bold><message>";

    @Comment("Configure the primary line of activity messages. (for multiple actions grouped together).")
    private String activity = "<sign> <#03a5fc><cause> <white><actionPastTense>"
        + " <#03a5fc><content> <gray>x<count> <white><since> <gray>(a:<actionFamily>)";

    @Comment("Used for all \"subdued\" messages.")
    private String subdued = "<prefix><gray><message>";

    @Comment("Configure the minus sign.")
    private String signMinus = "<red>-";

    @Comment("Configure the plus sign.")
    private String signPlus = "<green>+";

    public String prefix() {
        return prefix;
    }

    public String info() {
        return info;
    }

    public String error() {
        return error;
    }

    public String success() {
        return success;
    }

    public String heading() {
        return heading;
    }

    public String activity() {
        return activity;
    }

    public String subdued() {
        return subdued;
    }

    public String signMinus() {
        return signMinus;
    }

    public String signPlus() {
        return signPlus;
    }
}
