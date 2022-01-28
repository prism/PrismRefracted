package network.darkhelmet.prism.services.messages.resolvers;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.util.Either;

import network.darkhelmet.prism.api.PaginatedResults;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

public class PaginatedResultsPlaceholderResolver
    implements IPlaceholderResolver<CommandSender, PaginatedResults<?>, Component> {
    @Override
    public @NonNull Map<String, Either<ConclusionValue<? extends Component>, ContinuanceValue<?>>> resolve(
        final String placeholderName,
        final PaginatedResults<?> value,
        final CommandSender receiver,
        final Type owner,
        final Method method,
        final @Nullable Object[] parameters) {
        Component perPage = Component.text("123");
        Component currentPage = Component.text("123");
        Component totalPages = Component.text("123");

        return Map.of(placeholderName + "PerPage", Either.left(ConclusionValue.conclusionValue(perPage)),
                placeholderName + "CurrentPage", Either.left(ConclusionValue.conclusionValue(currentPage)),
                placeholderName + "TotalPages", Either.left(ConclusionValue.conclusionValue(totalPages)));
    }
}