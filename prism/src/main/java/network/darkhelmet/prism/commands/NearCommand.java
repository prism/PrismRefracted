package network.darkhelmet.prism.commands;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.activities.ActivityQuery;

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

        // @todo move to configs
        int radius = 5;

        // @todo move to helper
        Vector minVector = new Vector(loc.getX() - radius, loc.getY() - radius, loc.getZ() - radius);
        Vector maxVector = new Vector(loc.getX() + radius, loc.getY() + radius, loc.getZ() + radius);

        final ActivityQuery query = ActivityQuery.builder().minVector(minVector).maxVector(maxVector).build();
        Prism.newChain().async(() -> {
            try {
                Prism.getInstance().storageAdapter().queryActivities(query);
            } catch (Exception e) {
                Prism.getInstance().handleException(e);
            }
        }).execute();
    }
}