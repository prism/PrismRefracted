/*
 * Prism (Refracted)
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.prism.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.translation.TranslationKey;

import org.bukkit.command.CommandSender;

@Command("prism")
@Alias("pr")
public class AboutCommand extends CommandBase {
    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The version.
     */
    private final String version;

    /**
     * Construct the about command.
     *
     * @param messageService The message service
     * @param version The prism version
     */
    @Inject
    public AboutCommand(MessageService messageService, @Named("version") String version) {
        this.messageService = messageService;
        this.version = version;
    }

    /**
     * Run the about command, or default to this if prism is run with no subcommand.
     *
     * @param sender The command sender
     */
    @Default
    @SubCommand("about")
    public void onAbout(final CommandSender sender) {
        messageService.about(sender, version);
        messageService.error(sender, new TranslationKey("query-error"));
    }
}
