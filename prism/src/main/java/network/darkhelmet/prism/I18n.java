/*
 * CarbonChat
 *
 * Copyright (c) 2021 Josua Parks (Vicarious)
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

package network.darkhelmet.prism;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.kyori.adventure.translation.Translator;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class I18n {
    /**
     * The default locale.
     */
    private final Locale defaultLocale;

    /**
     * The data directory.
     */
    private final Path dataDirectory;

    /**
     * The logger.
     */
    private final Logger logger;

    /**
     * The plugin jar path.
     */
    private final Path pluginJar;

    /**
     * The translations bundles.
     */
    private final Map<Locale, Properties> locales = new HashMap<>();

    /**
     * Construct the translation system.
     *
     * @param logger The logger
     * @param dataDirectory The data directory
     * @param defaultLocale The default locale
     * @throws IOException IO Exception
     */
    public I18n(Logger logger, Path dataDirectory, Locale defaultLocale) throws IOException {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.defaultLocale = defaultLocale;

        this.pluginJar = pluginJar();

        this.reloadTranslations();
    }

    /**
     * Get a translation by key.
     *
     * @param key The key
     * @return The translation
     */
    public String messageOf(final String key) {
        final String value = this.locales.get(this.defaultLocale).getProperty(key);

        if (value == null) {
            throw new IllegalStateException("No message mapping for key " + key);
        }

        return value;
    }

    /**
     * Get the plugin jar path.
     *
     * @return The plugin jar path
     */
    private static Path pluginJar() {
        try {
            URL sourceUrl = I18n.class.getProtectionDomain().getCodeSource().getLocation();
            // Some class loaders give the full url to the class, some give the URL to its jar.
            // We want the containing jar, so we will unwrap jar-schema code sources.
            if (sourceUrl.getProtocol().equals("jar")) {
                final int exclamationIdx = sourceUrl.getPath().lastIndexOf('!');
                if (exclamationIdx != -1) {
                    sourceUrl = new URL(sourceUrl.getPath().substring(0, exclamationIdx));
                }
            }
            return Paths.get(sourceUrl.toURI());
        } catch (final URISyntaxException | MalformedURLException ex) {
            throw new RuntimeException("Could not locate plugin jar", ex);
        }
    }

    /**
     * Reload translations.
     *
     * @throws IOException IO Exception
     */
    private void reloadTranslations() throws IOException {
        final Path localeDirectory = this.dataDirectory.resolve("locale");

        // Create locale directory
        if (!Files.exists(localeDirectory)) {
            Files.createDirectories(localeDirectory);
        }

        this.walkPluginJar(stream -> stream.filter(Files::isRegularFile)
            .filter(it -> {
                final String pathString = it.toString();
                return pathString.startsWith("/locale/messages-")
                        && pathString.endsWith(".properties");
            })
            .forEach(localeFile -> {
                final String localeString = localeFile.getFileName().toString().substring("messages-".length())
                    .replace(".properties", "");
                // MC uses no_NO when the player selects nb_NO...
                final @Nullable Locale locale = Translator.parseLocale(localeString
                    .replace("nb_NO", "no_NO"));

                if (locale == null) {
                    this.logger.warn("Unknown locale '{}'?", localeString);
                    return;
                }

                this.logger.info("Found locale {} ({}) in: {}", locale.getDisplayName(), locale, localeFile);

                final Properties properties = new Properties();

                try {
                    this.loadProperties(properties, localeDirectory, localeFile);
                    this.locales.put(locale, properties);

                    this.logger.info("Successfully loaded locale {} ({})", locale.getDisplayName(), locale);
                } catch (final IOException ex) {
                    this.logger.warn("Unable to load locale {} ({}) from source: {}",
                        locale.getDisplayName(), locale, localeFile, ex);
                }
            }));
    }

    /**
     * Walk files in the plugin.
     *
     * @param user The consumer
     * @throws IOException IO Exception
     */
    private void walkPluginJar(final Consumer<Stream<Path>> user) throws IOException {
        if (Files.isDirectory(this.pluginJar)) {
            try (final var stream = Files.walk(this.pluginJar)) {
                user.accept(stream.map(path -> path.relativize(this.pluginJar)));
            }
            return;
        }
        try (final FileSystem jar = FileSystems.newFileSystem(this.pluginJar, this.getClass().getClassLoader())) {
            final Path root = jar.getRootDirectories()
                    .iterator()
                    .next();
            try (final var stream = Files.walk(root)) {
                user.accept(stream);
            }
        }
    }

    /**
     * Load a properties file.
     *
     * @param properties The properties object
     * @param localeDirectory The locale directory
     * @param localeFile The locale file
     * @throws IOException IO Exception
     */
    private void loadProperties(
        final Properties properties,
        final Path localeDirectory,
        final Path localeFile
    ) throws IOException {
        final Path savedFile = localeDirectory.resolve(localeFile.getFileName().toString());

        // If the file in the localeDirectory exists, read it to the properties
        if (Files.isRegularFile(savedFile)) {
            final InputStream inputStream = Files.newInputStream(savedFile);
            try (final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        }

        boolean write = !Files.isRegularFile(savedFile);

        // Read the file in the jar and add missing entries
        try (final Reader reader = new InputStreamReader(Files.newInputStream(localeFile), StandardCharsets.UTF_8)) {
            final Properties packaged = new Properties();
            packaged.load(reader);

            for (final Map.Entry<Object, Object> entry : packaged.entrySet()) {
                write |= properties.putIfAbsent(entry.getKey(), entry.getValue()) == null;
            }
        }

        // Write properties back to file
        if (write) {
            try (final Writer outputStream = Files.newBufferedWriter(savedFile)) {
                properties.store(outputStream, null);
            }
        }
    }
}