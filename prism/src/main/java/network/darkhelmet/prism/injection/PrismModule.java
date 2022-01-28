package network.darkhelmet.prism.injection;

import com.google.inject.AbstractModule;

import com.google.inject.Singleton;

import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.recording.IRecordingService;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.recording.RecordingManager;
import network.darkhelmet.prism.storage.mysql.MysqlSchemaUpdater;
import network.darkhelmet.prism.storage.mysql.MysqlStorageAdapter;

import org.apache.logging.log4j.Logger;

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
     * Construct the module.
     *
     * @param logger The logger
     * @param prismConfig The prism configuration
     */
    public PrismModule(
            Logger logger,
            PrismConfiguration prismConfig,
            StorageConfiguration storageConfig) {
        this.logger = logger;
        this.prismConfig = prismConfig;
        this.storageConfig = storageConfig;
    }

    @Override
    public void configure() {
        bind(Logger.class).toInstance(this.logger);
        bind(PrismConfiguration.class).toInstance(prismConfig);
        bind(StorageConfiguration.class).toInstance(storageConfig);
        bind(IActionRegistry.class).to(ActionRegistry.class).in(Singleton.class);
        bind(IRecordingService.class).to(RecordingManager.class).in(Singleton.class);

        if (storageConfig.datasource().equalsIgnoreCase("mysql")) {
            bind(MysqlSchemaUpdater.class).in(Singleton.class);
            bind(IStorageAdapter.class).to(MysqlStorageAdapter.class).in(Singleton.class);
        }
    }
}
