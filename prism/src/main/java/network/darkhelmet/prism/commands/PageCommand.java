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

import java.util.Optional;

import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.lookup.LookupService;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.translation.TranslationKey;

import org.bukkit.command.CommandSender;

@Command(value = "prism", alias = {"pr"})
public class PageCommand extends BaseCommand {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The lookup service.
     */
    private final LookupService lookupService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Construct the page command.
     *
     * @param lookupService The lookup service
     */
    @Inject
    public PageCommand(
            ConfigurationService configurationService,
            LookupService lookupService,
            MessageService messageService) {
        this.configurationService = configurationService;
        this.lookupService = lookupService;
        this.messageService = messageService;
    }

    /**
     * Change pages of a recent lookup.
     *
     * @param sender The command sender
     * @param page The new page
     */
    @SubCommand(value = "page")
    public void onPage(CommandSender sender, Integer page) {
        Optional<ActivityQuery> optionalQuery = lookupService.lastQuery(sender);
        if (optionalQuery.isEmpty()) {
            messageService.error(sender, new TranslationKey("no-last-query"));
            return;
        }

        if (page < 1) {
            messageService.error(sender, new TranslationKey("invalid-page"));
            return;
        }

        int offset = configurationService.prismConfig().perPage() * (page - 1);

        final ActivityQuery query = optionalQuery.get().toBuilder().offset(offset).build();
        lookupService.lookup(sender, query);
    }
}
