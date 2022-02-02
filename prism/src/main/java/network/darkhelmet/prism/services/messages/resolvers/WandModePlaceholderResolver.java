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

package network.darkhelmet.prism.services.messages.resolvers;

import com.google.inject.Inject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.util.Either;

import network.darkhelmet.prism.api.services.wands.WandMode;
import network.darkhelmet.prism.services.translation.TranslationService;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class WandModePlaceholderResolver implements
        IPlaceholderResolver<CommandSender, WandMode, Component> {
    /**
     * The translation service.
     */
    private final TranslationService translationService;

    /**
     * Construct a new translation string placeholder resolver.
     *
     * @param translationService The translation service
     */
    @Inject
    public WandModePlaceholderResolver(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public @Nullable Map<String, Either<ConclusionValue<? extends Component>, ContinuanceValue<?>>>
    resolve(
        final String placeholderName,
        final WandMode value,
        final CommandSender receiver,
        final Type owner,
        final Method method,
        final @Nullable Object[] parameters
    ) {
        String translated = translationService.messageOf(receiver, "wand-mode-"
            + value.toString().toLowerCase(Locale.ENGLISH));
        return Map.of(placeholderName, Either.left(ConclusionValue.conclusionValue(Component.text(translated))));
    }
}