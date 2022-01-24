package network.darkhelmet.prism.displays;

import net.kyori.adventure.audience.Audience;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.displays.DisplayFormatter;

import org.bukkit.command.CommandSender;

public class DisplayManager {
    /**
     * Display paginated results to a command sender.
     *
     * @param sender The command sender
     * @param results The paginated results
     */
    public <T> void show(DisplayFormatter<T> formatter, CommandSender sender, PaginatedResults<T> results) {
        Audience audience = Prism.getInstance().audiences().sender(sender);

        if (results.isEmpty()) {
            audience.sendMessage(formatter.noResults());
        } else {
            for (T row : results.results()) {
                audience.sendMessage(formatter.format(row));
            }
        }
    }
}
