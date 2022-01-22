package network.darkhelmet.prism.commands;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import network.darkhelmet.prism.Prism;

import org.bukkit.command.CommandSender;

@Command("prism")
@Alias("pr")
public class AboutCommand extends CommandBase {
    /**
     * Run the about command, or default to this if prism is run with no subcommand.
     *
     * @param sender The command sender
     */
    @Default
    @SubCommand("about")
    public void onAbout(final CommandSender sender) {
        String version = Prism.getInstance().getDescription().getVersion();

        Component message = Prism.getInstance().outputFormatter().prefix()
            .append(Component.text("v", NamedTextColor.GRAY))
            .append(Component.text(version, TextColor.fromCSSHexString("#ffd900")))
            .append(Component.text(" by ", NamedTextColor.GRAY))
            .append(Component.text("viveleroi"));

        Prism.getInstance().audiences().sender(sender).sendMessage(message);
    }
}
