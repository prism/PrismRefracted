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

package network.darkhelmet.prism.services.lookup;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.translation.TranslationKey;
import network.darkhelmet.prism.services.translation.TranslationService;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class LookupService {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The storage adapter.
     */
    private final IStorageAdapter storageAdapter;

    /**
     * The translation service.
     */
    private final TranslationService translationService;

    /**
     * The bukkit audiences.
     */
    private final BukkitAudiences audiences;

    /**
     * Cache recent queries.
     */
    private final Map<CommandSender, ActivityQuery> queries = new HashMap<>();

    /**
     * Cache tasks for clearing expired queries.
     */
    private final Map<CommandSender, BukkitTask> tasks = new HashMap<>();

    /**
     * Construct the lookup service.
     *
     * @param configurationService The configuration service
     * @param messageService The message service
     * @param storageAdapter The storage adapter
     * @param translationService The transation service
     * @param audiences The audiences
     */
    @Inject
    public LookupService(
            ConfigurationService configurationService,
            MessageService messageService,
            IStorageAdapter storageAdapter,
            TranslationService translationService,
            BukkitAudiences audiences) {
        this.configurationService = configurationService;
        this.messageService = messageService;
        this.storageAdapter = storageAdapter;
        this.translationService = translationService;
        this.audiences = audiences;
    }

    /**
     * Get the last query for a command sender.
     *
     * @param sender The sender
     * @return The last query, if any
     */
    public Optional<ActivityQuery> lastQuery(CommandSender sender) {
        return Optional.ofNullable(queries.get(sender));
    }

    /**
     * Performs an async storage query and displays the results to the command sender in a paginated chat view.
     *
     * @param sender The command sender
     * @param query The activity query
     */
    public void lookup(CommandSender sender, ActivityQuery query) {
        // Cancel any expiration tasks
        if (tasks.containsKey(sender)) {
            tasks.remove(sender).cancel();
        }

        final long expireAfter = configurationService.prismConfig().lookupExpiration();
        Prism.newChain().async(() -> {
            try {
                show(sender, storageAdapter.queryActivitiesAsInformation(query));

                // Cache this senders most recent query
                queries.put(sender, query);

                // Forcefully invalidate old queries after a set time. Because it could be a long
                // time between queries we can't rely on a guava cache.
                BukkitTask task = Bukkit.getScheduler().runTaskLater(Prism.getInstance(), () -> {
                    queries.remove(sender);
                    tasks.remove(sender);
                }, expireAfter);

                tasks.put(sender, task);
            } catch (Exception ex) {
                messageService.error(sender, new TranslationKey("query-error"));
                Prism.getInstance().handleException(ex);
            }
        }).execute();
    }

    /**
     * Display paginated results to a command sender.
     *
     * @param sender The command sender
     * @param results The paginated results
     */
    private void show(CommandSender sender, PaginatedResults<IActivity> results) {
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
                    String cmd = "/pr page " + (results.currentPage() - 1);

                    Component hover = Component.text(translationService.messageOf(sender, "page-prev-hover"));
                    String temp = translationService.messageOf(sender, "page-prev");
                    prev = MiniMessage.get().parse(temp)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
                }

                Component next = Component.empty();
                if (results.hasNextPage()) {
                    String cmd = "/pr page " + (results.currentPage() + 1);

                    Component hover = Component.text(translationService.messageOf(sender, "page-next-hover"));
                    String temp = translationService.messageOf(sender, "page-next");
                    next = MiniMessage.get().parse(temp)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
                }

                audiences.sender(sender).sendMessage(prev.append(next));
            }
        }
    }
}
