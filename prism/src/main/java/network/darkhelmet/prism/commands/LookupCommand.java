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
import dev.triumphteam.cmd.core.annotation.NamedArguments;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.argument.named.Arguments;

import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.lookup.LookupService;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.query.QueryService;
import network.darkhelmet.prism.services.translation.TranslationKey;

import org.bukkit.entity.Player;

@Command(value = "prism", alias = {"pr"})
public class LookupCommand extends BaseCommand {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The query service.
     */
    private final QueryService queryService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The storage adapter.
     */
    private final IStorageAdapter storageAdapter;

    /**
     * The lookup service.
     */
    private final LookupService lookupService;

    /**
     * Construct the lookup command.
     *
     * @param configurationService The configuration service
     * @param queryService The query service
     * @param messageService The message service
     * @param storageAdapter The storage adapter
     * @param lookupService The lookup service
     */
    @Inject
    public LookupCommand(
            ConfigurationService configurationService,
            QueryService queryService,
            MessageService messageService,
            IStorageAdapter storageAdapter,
            LookupService lookupService) {
        this.configurationService = configurationService;
        this.queryService = queryService;
        this.messageService = messageService;
        this.storageAdapter = storageAdapter;
        this.lookupService = lookupService;
    }

    /**
     * Run a lookup.
     *
     * @param player The player

     */
    @NamedArguments("params")
    @SubCommand(value = "lookup", alias = {"l"})
    public void onLookup(final Player player, Arguments arguments) {
        try {
            ActivityQuery.Builder builder = queryService.queryFromArguments(player.getLocation(), arguments);

            final ActivityQuery query = builder.limit(configurationService.prismConfig().perPage()).build();
            lookupService.lookup(player, query);
        } catch (IllegalArgumentException ex) {
            messageService.error(player, new TranslationKey(ex.getMessage()));
        }
    }
}