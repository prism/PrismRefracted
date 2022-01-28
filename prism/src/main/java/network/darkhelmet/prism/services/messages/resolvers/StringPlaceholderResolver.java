package network.darkhelmet.prism.services.messages.resolvers;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.util.Either;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class StringPlaceholderResolver implements IPlaceholderResolver<CommandSender, String, Component> {
    @Override
    public @Nullable Map<String, Either<ConclusionValue<? extends Component>, ContinuanceValue<?>>>
    resolve(
        final String placeholderName,
        final String value,
        final CommandSender receiver,
        final Type owner,
        final Method method,
        final @Nullable Object[] parameters
    ) {
        return Map.of(placeholderName, Either.left(ConclusionValue.conclusionValue(Component.text(value))));
    }
}