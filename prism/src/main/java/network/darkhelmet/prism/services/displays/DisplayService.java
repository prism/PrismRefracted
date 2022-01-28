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

package network.darkhelmet.prism.services.displays;

import com.google.inject.Inject;

import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.services.messages.MessageService;

import org.bukkit.command.CommandSender;

public class DisplayService {
    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Construct the display service.
     *
     * @param messageService The message service
     */
    @Inject
    public DisplayService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Display paginated results to a command sender.
     *
     * @param sender The command sender
     * @param results The paginated results
     */
    public void show(CommandSender sender, PaginatedResults<IActivity> results) {
        messageService.paginationHeader(sender, results);

        if (results.isEmpty()) {
            messageService.noResults(sender);
        } else {
            for (IActivity activity : results.results()) {
                messageService.listActivityRow(sender, activity);
            }
        }
    }
}
