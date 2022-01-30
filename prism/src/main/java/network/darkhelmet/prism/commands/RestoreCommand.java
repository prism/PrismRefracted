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

import java.util.List;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.translation.TranslationKey;
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Command("prism")
@Alias("pr")
public class RestoreCommand extends CommandBase {
    /**
     * The prism configuration.
     */
    private final PrismConfiguration prismConfig;

    /**
     * The storage adapter.
     */
    private final IStorageAdapter storageAdapter;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Construct the near command.
     *
     * @param prismConfig The prism configuration
     * @param storageAdapter The storage adapter
     */
    @Inject
    public RestoreCommand(
            PrismConfiguration prismConfig,
            IStorageAdapter storageAdapter,
            MessageService messageService) {
        this.prismConfig = prismConfig;
        this.storageAdapter = storageAdapter;
        this.messageService = messageService;
    }

    /**
     * Run the restore command.
     *
     * @param player The player
     */
    @SubCommand("restore")
    @Alias("rs")
    public void onRestore(final Player player) {
        Location loc = player.getLocation();
        int radius = prismConfig.nearRadius();

        Vector minVector = LocationUtils.getMinVector(loc, radius);
        Vector maxVector = LocationUtils.getMaxVector(loc, radius);

        final ActivityQuery query = ActivityQuery.builder()
            .minVector(minVector).maxVector(maxVector).sort(ActivityQuery.Sort.ASCENDING).setLookup(false).build();
        Prism.newChain().asyncFirst(() -> {
            try {
                return storageAdapter.queryActivities(query);
            } catch (Exception e) {
                messageService.error(player, new TranslationKey("query-error"));
                Prism.getInstance().handleException(e);
            }

            return null;
        }).abortIfNull().<List<IAction>>sync(results -> {
            for (IActivity activity : results) {
                activity.action().applyRestore(activity);
            }

            return null;
        }).execute();
    }
}