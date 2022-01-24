package network.darkhelmet.prism.commands;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.actions.Action;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.storage.models.ActivityRow;
import network.darkhelmet.prism.formatters.ActivityFormatter;
import network.darkhelmet.prism.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

@Command("prism")
@Alias("pr")
public class RollbackCommand extends CommandBase {
    /**
     * Run the rollback command.
     *
     * @param player The player
     */
    @SubCommand("rb")
    public void onRollback(final Player player) {
        Location loc = player.getLocation();
        int radius = Prism.getInstance().config().nearRadius();

        Vector minVector = LocationUtils.getMinVector(loc, radius);
        Vector maxVector = LocationUtils.getMaxVector(loc, radius);

        final ActivityQuery query = ActivityQuery.builder().minVector(minVector).maxVector(maxVector).build();
        Prism.newChain().async(() -> {
            try {
                List<Action> results = Prism.getInstance().storageAdapter().queryActivitiesAsActions(query);
                for (Action action : results) {
                    System.out.println(action);
                }
            } catch (Exception e) {
                Prism.getInstance().handleException(e);
            }
        }).execute();
    }
}