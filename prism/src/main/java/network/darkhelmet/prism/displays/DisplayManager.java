package network.darkhelmet.prism.displays;

import net.kyori.adventure.text.Component;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.storage.models.ActivityRow;

import org.bukkit.command.CommandSender;

public class DisplayManager {
    /**
     * Display paginated results to a command sender.
     * @param sender The command sender
     * @param activities The paginated results
     */
    // @todo make this generic
    public void show(CommandSender sender, PaginatedResults<ActivityRow> activities) {
        if (activities.isEmpty()) {
            Component error = Prism.getInstance().outputFormatter().error("no results");
            Prism.getInstance().audiences().sender(sender).sendMessage(error);
        } else {
            for (ActivityRow row : activities.results()) {
                sender.sendMessage(row.action());
            }
        }
    }
}
