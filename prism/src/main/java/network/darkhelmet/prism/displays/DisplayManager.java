package network.darkhelmet.prism.displays;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

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
        // Cache all message so we can send once formatted
        List<Component> messages = new ArrayList<>();

        // Heading
        messages.add(formatter.heading());

        // No results
        if (results.isEmpty()) {
            messages.add(formatter.noResults());
        } else {
            for (T row : results.results()) {
                messages.add(formatter.format(row));
            }
        }

        // Send all messages!
        Audience audience = Prism.getInstance().audiences().sender(sender);
        for (Component message : messages) {
            audience.sendMessage(message);
        }
    }
}
