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

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.translation.TranslationService;

import org.bukkit.command.CommandSender;

public class DisplayService {
    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The translation service.
     */
    private final TranslationService translationService;

    /**
     * The bukkit audiences.
     */
    private final BukkitAudiences audiences;

    /**
     * Construct the display service.
     *
     * @param messageService The message service
     */
    @Inject
    public DisplayService(
            MessageService messageService,
            TranslationService translationService,
            BukkitAudiences audiences) {
        this.messageService = messageService;
        this.translationService = translationService;
        this.audiences = audiences;
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

            if (results.hasPrevPage() || results.hasNextPage()) {
                Component prev = Component.empty();
                if (results.hasPrevPage()) {
                    Component hover = Component.text(translationService.messageOf(sender, "page-prev-hover"));
                    String temp = translationService.messageOf(sender, "page-prev");
                    prev = MiniMessage.get().parse(temp)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/say test 1"));
                }

                Component next = Component.empty();
                if (results.hasNextPage()) {
                    Component hover = Component.text(translationService.messageOf(sender, "page-next-hover"));
                    String temp = translationService.messageOf(sender, "page-next");
                    next = MiniMessage.get().parse(temp)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/say test 2"));
                }

                audiences.sender(sender).sendMessage(prev.append(next));
            }
        }
    }
}
