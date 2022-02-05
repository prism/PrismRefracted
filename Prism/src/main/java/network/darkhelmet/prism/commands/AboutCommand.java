package network.darkhelmet.prism.commands;

import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.commandlibs.CallInfo;
import network.darkhelmet.prism.commandlibs.SubHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.regex.Pattern;

public class AboutCommand implements SubHandler {

    private final Prism plugin;

    public AboutCommand(Prism plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the command.
     */
    @Override
    public void handle(CallInfo call) {
        Prism.messenger.sendMessage(call.getSender(),
                Prism.messenger.playerHeaderMsg(
                        Il8nHelper.getMessage("about-header")
                                .replaceText(Pattern.compile("<version>"),
                                    builder -> Component.text().content(plugin.getPrismVersion()))));
        Prism.messenger.sendMessage(call.getSender(), Prism.messenger.playerSubduedHeaderMsg(
                Component.text("指令帮助: ")
                        .append(Component.text("/pr ?")
                                .color(NamedTextColor.WHITE))));
        Prism.messenger.sendMessage(call.getSender(),
                Prism.messenger.playerSubduedHeaderMsg(
                        Component.text().content("Discord: ")
                                .append(Component.text("https://discord.gg/7FxZScH4EJ")
                                        .color(NamedTextColor.WHITE))
                                .clickEvent(ClickEvent.openUrl("https://discord.gg/7FxZScH4EJ"))
                                .build()));
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-about")};
    }

    @Override
    public String getRef() {
        return ".html";
    }
}