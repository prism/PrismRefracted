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
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;

import io.leangen.geantyref.TypeToken;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.modifications.IModificationQueueService;
import network.darkhelmet.prism.api.services.recording.IRecordingService;
import network.darkhelmet.prism.api.services.wands.IWand;
import network.darkhelmet.prism.api.services.wands.WandMode;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.expectations.ExpectationService;
import network.darkhelmet.prism.services.filters.FilterService;
import network.darkhelmet.prism.services.messages.MessageRenderer;
import network.darkhelmet.prism.services.messages.MessageSender;
import network.darkhelmet.prism.services.messages.MessageService;
import network.darkhelmet.prism.services.messages.ReceiverResolver;
import network.darkhelmet.prism.services.messages.resolvers.ActivityPlaceholderResolver;
import network.darkhelmet.prism.services.messages.resolvers.PaginatedResultsPlaceholderResolver;
import network.darkhelmet.prism.services.messages.resolvers.StringPlaceholderResolver;
import network.darkhelmet.prism.services.messages.resolvers.TranslatableStringPlaceholderResolver;
import network.darkhelmet.prism.services.messages.resolvers.WandModePlaceholderResolver;
import network.darkhelmet.prism.services.modifications.ModificationQueueService;
import network.darkhelmet.prism.services.recording.RecordingService;
import network.darkhelmet.prism.services.translation.TranslationKey;
import network.darkhelmet.prism.services.translation.TranslationService;
import network.darkhelmet.prism.services.wands.InspectionWand;
import network.darkhelmet.prism.services.wands.RestoreWand;
import network.darkhelmet.prism.services.wands.RollbackWand;
import network.darkhelmet.prism.services.wands.WandService;
import network.darkhelmet.prism.storage.mysql.MysqlQueryBuilder;
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
     * @param prism Prism
     * @param logger The logger
     */
    public PrismModule(Prism prism, Logger logger) {
        this.logger = logger;
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
     * Get the configured storage adapter.
     *
     * @param configurationService The configuration service
     * @param storageMap The storage binding map
     * @return The storage adapter
     */
    @Provides
    public IStorageAdapter getStorageAdapter(
            ConfigurationService configurationService,
            Map<String, Provider<IStorageAdapter>> storageMap) {
        String datasource = configurationService.storageConfig().datasource().toLowerCase(Locale.ENGLISH);
        return storageMap.get(datasource).get();
    }

    /**
     * Get the message service.
     *
     * @param translationService The translation service
     * @param messageRenderer The message renderer
     * @param messageSender The message sender
     * @param activityPlaceholderResolver The activity placeholder resolver
     * @param translatableStringPlaceholderResolver The translatable string resolver
     * @param wandModePlaceholderResolver The wand mode resolver
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
            TranslatableStringPlaceholderResolver translatableStringPlaceholderResolver,
            WandModePlaceholderResolver wandModePlaceholderResolver) {
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
                .weightedPlaceholderResolver(WandMode.class, wandModePlaceholderResolver, 0)
                .weightedPlaceholderResolver(new TypeToken<>(){}, new PaginatedResultsPlaceholderResolver(), 0)
                .create(this.getClass().getClassLoader());
        } catch (UnscannableMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void configure() {
        // Base
        bind(Logger.class).toInstance(this.logger);
        bind(Path.class).toInstance(dataPath);

        // Actions
        bind(IActionRegistry.class).to(ActionRegistry.class).in(Singleton.class);

        // Service - Configuration
        bind(ConfigurationService.class).in(Singleton.class);

        // Service - Expectations
        bind(ExpectationService.class).in(Singleton.class);

        // Service - Filters
        bind(FilterService.class).in(Singleton.class);

        // Service - Modifications
        bind(IModificationQueueService.class).to(ModificationQueueService.class).in(Singleton.class);

        // Service - Recording
        bind(IRecordingService.class).to(RecordingService.class).in(Singleton.class);

        // Service - Messages
        bind(MessageRenderer.class).in(Singleton.class);
        bind(MessageSender.class).in(Singleton.class);
        bind(ActivityPlaceholderResolver.class).in(Singleton.class);
        bind(TranslatableStringPlaceholderResolver.class).in(Singleton.class);

        // Service - Translation
        bind(TranslationService.class).in(Singleton.class);

        // Service - Wands
        bind(WandService.class).in(Singleton.class);
        MapBinder<WandMode, IWand> wandBinder = MapBinder.newMapBinder(binder(), WandMode.class, IWand.class);
        wandBinder.addBinding(WandMode.INSPECT).to(InspectionWand.class);
        wandBinder.addBinding(WandMode.ROLLBACK).to(RollbackWand.class);
        wandBinder.addBinding(WandMode.RESTORE).to(RestoreWand.class);

        // Storage
        bind(MysqlQueryBuilder.class);
        bind(MysqlSchemaUpdater.class).in(Singleton.class);

        MapBinder<String, IStorageAdapter> storageBinder = MapBinder.newMapBinder(
            binder(), String.class, IStorageAdapter.class);
        storageBinder.addBinding("mysql").to(MysqlStorageAdapter.class).in(Singleton.class);
    }
}
