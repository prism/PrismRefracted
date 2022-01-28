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

package network.darkhelmet.prism.services.translation;

import com.google.inject.Inject;

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
import net.kyori.moonshine.message.IMessageSource;

import network.darkhelmet.prism.config.PrismConfiguration;

import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TranslationService implements IMessageSource<CommandSender, String> {
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
     * @param prismConfiguration The default locale
     * @throws IOException IO Exception
     */
    @Inject
    public TranslationService(
        Logger logger, Path dataDirectory, PrismConfiguration prismConfiguration) throws IOException {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.defaultLocale = prismConfiguration.defaultLocale();
        this.pluginJar = pluginJar();

        this.reloadTranslations();
    }

    @Override
    public String messageOf(final CommandSender receiver, final String messageKey) {
        if (receiver instanceof Player player) {
            return this.forPlayer(messageKey, player);
        }

        return this.forAudience(messageKey);
    }

    /**
     * Get the translation for a key for specific player locale.
     *
     * @param messageKey The message key
     * @param player The player
     * @return The translation
     */
    private String forPlayer(final String messageKey, final Player player) {
        Locale locale = getLocaleFromString(player.getLocale());
        final Properties properties = this.locales.get(locale);

        if (properties != null) {
            final var message = properties.getProperty(messageKey);

            if (message != null) {
                return message;
            }
        }

        return forAudience(messageKey);
    }

    /**
     * Get a translation by key.
     *
     * @param messageKey The key
     * @return The translation
     */
    private String forAudience(final String messageKey) {
        final String value = this.locales.get(this.defaultLocale).getProperty(messageKey);

        if (value == null) {
            throw new IllegalStateException("No message mapping for key " + messageKey);
        }

        return value;
    }

    /**
     * Convert a string based locale into a Locale Object.
     *
     * <p>Assumes the string has form "{language}_{country}_{variant}".
     * Examples: "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr_MAC"</p>
     *
     * @param localeString The String
     * @return the Locale
     */
    private static Locale getLocaleFromString(String localeString) {
        if (localeString == null) {
            return null;
        }

        localeString = localeString.trim();
        if (localeString.equalsIgnoreCase("default")) {
            return Locale.getDefault();
        }

        // Extract language
        int languageIndex = localeString.indexOf('_');
        String language = null;
        if (languageIndex == -1) {
            // No further "_" so is "{language}" only
            return new Locale(localeString, "");
        } else {
            language = localeString.substring(0, languageIndex);
        }

        // Extract country
        int countryIndex = localeString.indexOf('_', languageIndex + 1);
        String country = null;
        if (countryIndex == -1) {
            // No further "_" so is "{language}_{country}"
            country = localeString.substring(languageIndex + 1);
            return new Locale(language, country);
        } else {
            // Assume all remaining is the variant so is "{language}_{country}_{variant}"
            country = localeString.substring(languageIndex + 1, countryIndex);
            String variant = localeString.substring(countryIndex + 1);
            return new Locale(language, country, variant);
        }
    }

    /**
     * Get the plugin jar path.
     *
     * @return The plugin jar path
     */
    private static Path pluginJar() {
        try {
            URL sourceUrl = TranslationService.class.getProtectionDomain().getCodeSource().getLocation();
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
            final Path root = jar.getRootDirectories().iterator().next();
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