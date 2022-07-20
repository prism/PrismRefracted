/*
 * This file is part of LuckPerms, licensed under the MIT License.
 * It has been modified for use in Prism.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package network.darkhelmet.prism.loader.services.dependencies;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import network.darkhelmet.prism.loader.services.dependencies.relocation.Relocation;
import network.darkhelmet.prism.loader.storage.StorageType;

public class DependencyRegistry {
    /**
     * Build a map of storage dependencies.
     */
    private static final ListMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableListMultimap
        .<StorageType, Dependency>builder()
        .putAll(StorageType.MYSQL, Dependency.HIKARI, Dependency.MYSQL_DRIVER)
        .build();

    /**
     * Get a list of dependencies for all platforms.
     *
     * @return Dependencies for all platforms.
     */
    public Set<Dependency> globalDependencies() {
        return EnumSet.of(
            Dependency.ADVENTURE,
            Dependency.ADVENTURE_KEY,
            Dependency.ADVENTURE_MINIMESSAGE,
            Dependency.ADVENTURE_NBT,
            Dependency.ADVENTURE_PLATFORM,
            Dependency.ADVENTURE_PLATFORM_FACET,
            Dependency.ADVENTURE_SERIALIZER_GSON,
            Dependency.ADVENTURE_SERIALIZER_GSON_LEGACY,
            Dependency.ADVENTURE_SERIALIZER_LEGACY,
            Dependency.ADVENTURE_SERIALIZER_PLAIN,
            Dependency.AOPALLIANCE,
            Dependency.APACHE_COLLECTIONS,
            Dependency.CONFIGURATE_CORE,
            Dependency.CONFIGURATE_HOCON,
            Dependency.EXAMINATION_API,
            Dependency.EXAMINATION_STRING,
            Dependency.EXPIRINGMAP,
            Dependency.FASTUTIL,
            Dependency.GEANTYREF,
            Dependency.GUICE,
            Dependency.GUICE_ASSISTED,
            Dependency.HOCON_CONFIG,
            Dependency.MOONSHINE_CORE,
            Dependency.MOONSHINE_INTERNAL,
            Dependency.MOONSHINE_STANDARD,
            Dependency.P6SPY
        );
    }

    /**
     * Resolves all storage dependencies.
     *
     * @return The storage dependencies
     */
    public Set<Dependency> storageDependencies() {
        return new LinkedHashSet<>(STORAGE_DEPENDENCIES.get(StorageType.MYSQL));
    }

    /**
     * Apply any custom relocation settings.
     *
     * @param dependency The dependency
     * @param relocations The relocations
     */
    public void applyRelocationSettings(Dependency dependency, List<Relocation> relocations) {}

    /**
     * Check if a dependency should auto-reload.
     *
     * @param dependency The dependency
     * @return True if should auto-reload
     */
    public boolean shouldAutoLoad(Dependency dependency) {
        return switch (dependency) {
            // all used within 'isolated' classloaders, and are therefore not
            // relocated.
            case ASM, ASM_COMMONS, JAR_RELOCATOR -> false;
            default -> true;
        };
    }
}