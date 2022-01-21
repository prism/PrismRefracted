package network.darkhelmet.prism.commands;

import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.commands.Flag;
import network.darkhelmet.prism.commandlibs.CallInfo;
import network.darkhelmet.prism.commandlibs.SubHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FlagsCommand implements SubHandler {

    /**
     * Handle the command.
     */
    @Override
    public void handle(CallInfo call) {
        help(call.getSender());
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-flag-list")};
    }

    @Override
    public String getRef() {
        return "/flags.html";
    }

    /**
     * Display param help.
     *
     * @param s CommandSender
     */
    private void help(CommandSender s) {
        Prism.messenger.sendMessage(s, Prism.messenger.playerHeaderMsg(
                Il8nHelper.getMessage("flag-help-header").color(NamedTextColor.GOLD)));
        Prism.messenger.sendMessage(s, Prism.messenger.playerMsg(
                Il8nHelper.getMessage("flag-help-1").color(NamedTextColor.GRAY)));
        Prism.messenger.sendMessage(s, Prism.messenger
                .playerMsg(Il8nHelper.getMessage("flag-help-2")));
        for (final Flag flag : Flag.values()) {
            Prism.messenger.sendMessage(s,Prism.messenger.playerMsg(
                    Component.text(flag.getUsage().replace("_", "-"))
                            .color(NamedTextColor.LIGHT_PURPLE)
                            .append(Component.text(" " + Il8nHelper.getRawMessage(flag.getDescription()))
                                    .color(NamedTextColor.GRAY))));
        }
    }
}