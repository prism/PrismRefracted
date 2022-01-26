package network.darkhelmet.prism.commands;

import java.util.List;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Command("prism")
@Alias("pr")
public class RollbackCommand extends CommandBase {
    /**
     * Run the rollback command.
     *
     * @param player The player
     */
    @SubCommand("rollback")
    @Alias("rb")
    public void onRollback(final Player player) {
        Location loc = player.getLocation();
        int radius = Prism.getInstance().config().nearRadius();

        Vector minVector = LocationUtils.getMinVector(loc, radius);
        Vector maxVector = LocationUtils.getMaxVector(loc, radius);

        final ActivityQuery query = ActivityQuery.builder().minVector(minVector).maxVector(maxVector).build();
        Prism.newChain().asyncFirst(() -> {
            try {
                return Prism.getInstance().storageAdapter().queryActivitiesAsActions(query);
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