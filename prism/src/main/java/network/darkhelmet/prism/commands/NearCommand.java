package network.darkhelmet.prism.commands;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import net.kyori.adventure.text.Component;

import network.darkhelmet.prism.I18n;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.ActivityQuery;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.formatters.ActivityFormatter;
import network.darkhelmet.prism.utils.LocationUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Command("prism")
@Alias("pr")
public class NearCommand extends CommandBase {
    /**
     * Run the near command. Searches for records nearby the player.
     *
     * @param player The player
     */
    @SubCommand("near")
    public void onNear(final Player player) {
        Location loc = player.getLocation();
        int radius = Prism.getInstance().config().nearRadius();

        Vector minVector = LocationUtils.getMinVector(loc, radius);
        Vector maxVector = LocationUtils.getMaxVector(loc, radius);

        final ActivityQuery query = ActivityQuery.builder().minVector(minVector).maxVector(maxVector).build();
        Prism.newChain().async(() -> {
            ActivityFormatter formatter = new ActivityFormatter();

            try {
                PaginatedResults<IActivity> paginatedResults = Prism.getInstance()
                    .storageAdapter().queryActivitiesPaginated(query);

                Prism.getInstance().displayManager().show(formatter, player, paginatedResults);
            } catch (Exception e) {
                Component error = Prism.getInstance().outputFormatter().error(I18n.translateStr("query-error"));
                Prism.getInstance().audiences().player(player).sendMessage(error);

                Prism.getInstance().handleException(e);
            }
        }).execute();
    }
}