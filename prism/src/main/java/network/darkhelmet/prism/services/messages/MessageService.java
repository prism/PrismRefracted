package network.darkhelmet.prism.services.messages;

import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;

import network.darkhelmet.prism.api.PaginatedResults;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.services.translation.TranslationKey;

import org.bukkit.command.CommandSender;

public interface MessageService {
    @Message("about")
    void about(CommandSender receiver, @Placeholder String version);

    @Message("error")
    void error(CommandSender receiver, @Placeholder TranslationKey message);

    @Message("activity-row")
    void listActivityRow(CommandSender receiver, @Placeholder IActivity activity);

    @Message("pagination-header")
    void paginationHeader(CommandSender receiver, @Placeholder PaginatedResults<?> pagination);

    @Message("no-results")
    void noResults(CommandSender receiver);
}
