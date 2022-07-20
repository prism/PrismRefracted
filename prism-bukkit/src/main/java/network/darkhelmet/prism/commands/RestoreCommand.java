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

import java.util.List;

import network.darkhelmet.prism.PrismBukkit;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.services.modifications.IModificationQueue;
import network.darkhelmet.prism.api.services.modifications.IModificationQueueService;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.providers.TaskChainProvider;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.query.QueryService;
import network.darkhelmet.prism.services.translation.TranslationKey;

import org.bukkit.entity.Player;

@Command(value = "prism", alias = {"pr"})
public class RestoreCommand extends BaseCommand {
    /**
     * The storage adapter.
     */
    private final IStorageAdapter storageAdapter;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The modification queue service.
     */
    private final IModificationQueueService modificationQueueService;

    /**
     * The query service.
     */
    private final QueryService queryService;

    /**
     * The task chain provider.
     */
    private final TaskChainProvider taskChainProvider;

    /**
     * Construct the restore command.
     *
     * @param storageAdapter The storage adapter
     * @param messageService The message service
     * @param modificationQueueService The modification queue service
     * @param queryService The query service
     * @param taskChainProvider The task chain provider
     */
    @Inject
    public RestoreCommand(
            IStorageAdapter storageAdapter,
            MessageService messageService,
            IModificationQueueService modificationQueueService,
            QueryService queryService,
            TaskChainProvider taskChainProvider) {
        this.storageAdapter = storageAdapter;
        this.messageService = messageService;
        this.modificationQueueService = modificationQueueService;
        this.queryService = queryService;
        this.taskChainProvider = taskChainProvider;
    }

    /**
     * Run the restore command.
     *
     * @param player The player
     */
    @NamedArguments("params")
    @SubCommand(value = "restore", alias = {"rs"})
    public void onRestore(final Player player, final Arguments arguments) {
        // Ensure a queue is free
        if (!modificationQueueService.queueAvailable()) {
            messageService.error(player, new TranslationKey("queue-not-free"));

            return;
        }

        final ActivityQuery query = queryService
            .queryFromArguments(player.getLocation(), arguments).modification().build();
        taskChainProvider.newChain().asyncFirst(() -> {
            try {
                return storageAdapter.queryActivities(query);
            } catch (Exception e) {
                messageService.error(player, new TranslationKey("query-error"));
                PrismBukkit.getInstance().handleException(e);
            }

            return null;
        }).abortIfNull().<List<IAction>>sync(results -> {
            if (results.isEmpty()) {
                messageService.noResults(player);

                return null;
            }

            IModificationQueue queue = modificationQueueService.newRestoreQueue(player, results);
            queue.apply();

            return null;
        }).execute();
    }
}