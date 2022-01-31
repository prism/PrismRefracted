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