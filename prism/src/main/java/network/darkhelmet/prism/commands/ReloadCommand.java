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

import java.io.IOException;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;

import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.translation.TranslationKey;
import network.darkhelmet.prism.services.translation.TranslationService;

import org.bukkit.command.CommandSender;

@Command("prism")
@Alias("pr")
public class ReloadCommand extends CommandBase {
    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The translation service.
     */
    private final TranslationService translationService;

    /**
     * Construct the reload command.
     *
     * @param messageService The message service
     */
    @Inject
    public ReloadCommand(MessageService messageService, TranslationService translationService) {
        this.messageService = messageService;
        this.translationService = translationService;
    }

    /**
     * Reload the locale files.
     *
     * @param sender The command sender
     */
    @SubCommand("reloadlocales")
    public void onReload(final CommandSender sender) {
        try {
            translationService.reloadTranslations();

            messageService.reloadedLocales(sender);
        } catch (IOException e) {
            messageService.error(sender, new TranslationKey("reload-locale-error"));
            e.printStackTrace();
        }
    }
}
