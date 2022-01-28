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

package network.darkhelmet.prism.config;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;

import network.darkhelmet.prism.config.serializers.LocaleSerializerConfigurate;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public class Config {
    private Config() {}

    /**
     * Build a hocon configuration loader with locale support.
     *
     * @param file The config file
     * @return The config loader
     */
    public static ConfigurationLoader<?> configurationLoader(final Path file) {
        return HoconConfigurationLoader.builder()
            .prettyPrinting(true)
            .defaultOptions(opts -> {
                final ConfigurateComponentSerializer serializer =
                    ConfigurateComponentSerializer.configurate();

                return opts.shouldCopyDefaults(true).serializers(serializerBuilder ->
                    serializerBuilder.registerAll(serializer.serializers())
                        .register(Locale.class, new LocaleSerializerConfigurate())
                );
            })
            .path(file)
            .build();
    }

    /**
     * Get or create a configuration file.
     *
     * @param clz The configuration class.
     * @param file The file path we'll read/write to.
     * @param <T> The configuration class type.
     * @return The configuration class instance
     */
    public static <T> T getOrWriteConfiguration(Class<T> clz, File file) {
        return getOrWriteConfiguration(clz, file, null);
    }

    /**
     * Get or create a configuration file.
     *
     * @param clz The configuration class
     * @param file The file path we'll read/write to
     * @param config The existing config object to write
     * @param <T> The configuration class type
     * @return The configuration class instance
     */
    public static <T> T getOrWriteConfiguration(Class<T> clz, File file, T config) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        final var loader = configurationLoader(file.toPath());

        try {
            final ConfigurationNode root = loader.load();

            // If config is not provided, load it
            if (config == null) {
                config = root.get(clz);
            }

            root.set(clz, config);
            loader.save(root);

            return config;
        } catch (final ConfigurateException e) {
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }

        return null;
    }
}