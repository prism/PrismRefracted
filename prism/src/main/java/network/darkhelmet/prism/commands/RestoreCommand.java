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

import java.util.List;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.services.modifications.IModificationQueue;
import network.darkhelmet.prism.api.services.modifications.IModificationQueueService;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.translation.TranslationKey;
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Command(value = "prism", alias = {"pr"})
public class RestoreCommand extends BaseCommand {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

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
     * Construct the near command.
     *
     * @param configurationService The configuration service
     * @param storageAdapter The storage adapter
     */
    @Inject
    public RestoreCommand(
            ConfigurationService configurationService,
            IStorageAdapter storageAdapter,
            MessageService messageService,
            IModificationQueueService modificationQueueService) {
        this.configurationService = configurationService;
        this.storageAdapter = storageAdapter;
        this.messageService = messageService;
        this.modificationQueueService = modificationQueueService;
    }

    /**
     * Run the restore command.
     *
     * @param player The player
     */
    @SubCommand(value = "restore", alias = {"rs"})
    public void onRestore(final Player player) {
        // Ensure a queue is free
        if (!modificationQueueService.queueAvailable()) {
            messageService.error(player, new TranslationKey("queue-not-free"));

            return;
        }

        Location loc = player.getLocation();
        int radius = configurationService.prismConfig().nearRadius();

        Vector minVector = LocationUtils.getMinVector(loc, radius);
        Vector maxVector = LocationUtils.getMaxVector(loc, radius);

        final ActivityQuery query = ActivityQuery.builder()
            .minVector(minVector).maxVector(maxVector).sort(ActivityQuery.Sort.ASCENDING).lookup(false).build();
        Prism.newChain().asyncFirst(() -> {
            try {
                return storageAdapter.queryActivitiesAsModification(query);
            } catch (Exception e) {
                messageService.error(player, new TranslationKey("query-error"));
                Prism.getInstance().handleException(e);
            }

            return null;
        }).abortIfNull().<List<IAction>>sync(results -> {
            IModificationQueue queue = modificationQueueService.newRestoreQueue(player, results);
            queue.apply();

            return null;
        }).execute();
    }
}