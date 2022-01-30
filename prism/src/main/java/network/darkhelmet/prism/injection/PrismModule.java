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

package network.darkhelmet.prism.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import io.leangen.geantyref.TypeToken;

import java.nio.file.Path;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.modifications.IModificationQueueService;
import network.darkhelmet.prism.api.recording.IRecordingService;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.modifications.ModificationQueueService;
import network.darkhelmet.prism.services.expectations.ExpectationService;
import network.darkhelmet.prism.services.messages.MessageRenderer;
import network.darkhelmet.prism.services.messages.MessageSender;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.messages.ReceiverResolver;
import network.darkhelmet.prism.services.messages.resolvers.ActivityPlaceholderResolver;
import network.darkhelmet.prism.services.messages.resolvers.PaginatedResultsPlaceholderResolver;
import network.darkhelmet.prism.services.messages.resolvers.StringPlaceholderResolver;
import network.darkhelmet.prism.services.messages.resolvers.TranslatableStringPlaceholderResolver;
import network.darkhelmet.prism.services.recording.RecordingService;
import network.darkhelmet.prism.services.translation.TranslationKey;
import network.darkhelmet.prism.services.translation.TranslationService;
import network.darkhelmet.prism.storage.mysql.MysqlSchemaUpdater;
import network.darkhelmet.prism.storage.mysql.MysqlStorageAdapter;

import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;

public class PrismModule extends AbstractModule {
    /**
     * The logger.
     */
    private final Logger logger;

    /**
     * The prism configuration.
     */
    private final PrismConfiguration prismConfig;

    /**
     * The storage configuration.
     */
    private final StorageConfiguration storageConfig;

    /**
     * The data path.
     */
    private final Path dataPath;

    /**
     * The version.
     */
    private final String version;

    /**
     * Construct the module.
     *
     * @param logger The logger
     * @param prismConfig The prism configuration
     */
    public PrismModule(
            Prism prism,
            Logger logger,
            PrismConfiguration prismConfig,
            StorageConfiguration storageConfig) {
        this.logger = logger;
        this.prismConfig = prismConfig;
        this.storageConfig = storageConfig;
        this.dataPath = prism.getDataFolder().toPath();
        this.version = prism.getDescription().getVersion();
    }

    @Provides
    @Named("version")
    String getVersion() {
        return version;
    }

    /**
     * Get the bukkit audiences.
     *
     * @return The bukkit audiences
     */
    @Provides
    @Singleton
    public BukkitAudiences getAudience() {
        return BukkitAudiences.create(Prism.getInstance());
    }

    /**
     * Get the message service.
     *
     * @param translationService The translation service.
     * @return The message service
     */
    @Provides
    @Singleton
    @Inject
    public MessageService getMessageService(
            TranslationService translationService,
            MessageRenderer messageRenderer,
            MessageSender messageSender,
            ActivityPlaceholderResolver activityPlaceholderResolver,
            TranslatableStringPlaceholderResolver translatableStringPlaceholderResolver) {
        try {
            return Moonshine.<MessageService, CommandSender>builder(
                    TypeToken.get(MessageService.class))
                .receiverLocatorResolver(new ReceiverResolver(), 0)
                .sourced(translationService)
                .rendered(messageRenderer)
                .sent(messageSender)
                .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(
                    new StandardSupertypeThenInterfaceSupertypeStrategy(false)))
                .weightedPlaceholderResolver(TranslationKey.class, translatableStringPlaceholderResolver, 0)
                .weightedPlaceholderResolver(String.class, new StringPlaceholderResolver(), 0)
                .weightedPlaceholderResolver(IActivity.class, activityPlaceholderResolver, 0)
                .weightedPlaceholderResolver(new TypeToken<>(){}, new PaginatedResultsPlaceholderResolver(), 0)
                .create(this.getClass().getClassLoader());
        } catch (UnscannableMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void configure() {
        bind(Logger.class).toInstance(this.logger);
        bind(PrismConfiguration.class).toInstance(prismConfig);
        bind(StorageConfiguration.class).toInstance(storageConfig);
        bind(Path.class).toInstance(dataPath);
        bind(IActionRegistry.class).to(ActionRegistry.class).in(Singleton.class);
        bind(IRecordingService.class).to(RecordingService.class).in(Singleton.class);
        bind(IModificationQueueService.class).to(ModificationQueueService.class).in(Singleton.class);
        bind(MessageRenderer.class).in(Singleton.class);
        bind(MessageSender.class).in(Singleton.class);
        bind(TranslationService.class).in(Singleton.class);
        bind(ActivityPlaceholderResolver.class).in(Singleton.class);
        bind(TranslatableStringPlaceholderResolver.class).in(Singleton.class);
        bind(ExpectationService.class).in(Singleton.class);

        if (storageConfig.datasource().equalsIgnoreCase("mysql")) {
            bind(MysqlSchemaUpdater.class).in(Singleton.class);
            bind(IStorageAdapter.class).to(MysqlStorageAdapter.class).in(Singleton.class);
        }
    }
}
