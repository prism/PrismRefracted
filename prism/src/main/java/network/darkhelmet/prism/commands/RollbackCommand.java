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

import java.util.List;

import com.google.inject.Inject;

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
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Command("prism")
@Alias("pr")
public class RollbackCommand extends CommandBase {
    /**
     * The prism configuration.
     */
    private PrismConfiguration prismConfig;

    /**
     * The storage adapter.
     */
    private IStorageAdapter storageAdapter;

    /**
     * Construct the near command.
     *
     * @param prismConfig The prism configuration
     * @param storageAdapter The storage adapter
     */
    @Inject
    public RollbackCommand(PrismConfiguration prismConfig, IStorageAdapter storageAdapter) {
        this.prismConfig = prismConfig;
        this.storageAdapter = storageAdapter;
    }

    /**
     * Run the rollback command.
     *
     * @param player The player
     */
    @SubCommand("rollback")
    @Alias("rb")
    public void onRollback(final Player player) {
        Location loc = player.getLocation();
        int radius = prismConfig.nearRadius();

        Vector minVector = LocationUtils.getMinVector(loc, radius);
        Vector maxVector = LocationUtils.getMaxVector(loc, radius);

        final ActivityQuery query = ActivityQuery.builder().minVector(minVector).maxVector(maxVector).build();
        Prism.newChain().asyncFirst(() -> {
            try {
                return storageAdapter.queryActivities(query);
            } catch (Exception e) {
                Prism.getInstance().handleException(e);
            }

            return null;
        }).abortIfNull().<List<IAction>>sync(results -> {
            for (IActivity activity : results) {
                activity.action().applyRollback(activity);
            }

            return null;
        }).execute();
    }
}