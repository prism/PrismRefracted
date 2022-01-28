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

package network.darkhelmet.prism.displays;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;

import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.displays.DisplayFormatter;

import org.bukkit.command.CommandSender;

public class DisplayManager {
    /**
     * Display paginated results to a command sender.
     *
     * @param sender The command sender
     * @param results The paginated results
     */
    public static <T> void show(DisplayFormatter<T> formatter, CommandSender sender, PaginatedResults<T> results) {
        // Cache all message so we can send once formatted
        List<Component> messages = new ArrayList<>();

        // Heading
        messages.add(formatter.heading());

        // No results
        if (results.isEmpty()) {
            messages.add(formatter.noResults());
        } else {
            for (T row : results.results()) {
                messages.add(formatter.format(row));
            }
        }

        // Send all messages!
//        Audience audience = Prism.getInstance().audiences().sender(sender);
//        for (Component message : messages) {
//            audience.sendMessage(message);
//        }
    }
}
