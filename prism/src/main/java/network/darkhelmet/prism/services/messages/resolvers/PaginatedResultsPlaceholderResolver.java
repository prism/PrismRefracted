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
        int showing = value.totalResults() < value.perPage() ? value.totalResults() : value.perPage();

        Component perPage = Component.text(showing);
        Component currentPage = Component.text(value.currentPage());
        Component totalPages = Component.text(value.totalPages());
        Component totalResults = Component.text(value.totalResults());

        return Map.of(placeholderName + "_per_page", Either.left(ConclusionValue.conclusionValue(perPage)),
                placeholderName + "_total_results", Either.left(ConclusionValue.conclusionValue(totalResults)),
                placeholderName + "_current_page", Either.left(ConclusionValue.conclusionValue(currentPage)),
                placeholderName + "_total_pages", Either.left(ConclusionValue.conclusionValue(totalPages)));
    }
}