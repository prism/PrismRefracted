package network.darkhelmet.prism;

import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;

import me.mattstudios.mf.base.CommandManager;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.api.storage.cache.IStorageCache;
import network.darkhelmet.prism.api.storage.models.WorldModel;
import network.darkhelmet.prism.commands.AboutCommand;
import network.darkhelmet.prism.config.Config;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.formatters.OutputFormatter;
import network.darkhelmet.prism.listeners.WorldLoadListener;
import network.darkhelmet.prism.storage.StorageCache;
import network.darkhelmet.prism.storage.mysql.MysqlStorageAdapter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Prism extends JavaPlugin {
    /**
     * Cache static instance.
     */
    private static Prism instance;

    /**
     * The logger.
     */
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * The config.
     */
    private PrismConfiguration prismConfig;

    /**
     * The storage configuration.
     */
    private StorageConfiguration storageConfig;

    /**
     * Cache the plugin name.
     */
    private String pluginName;

    /**
     * The bukkit audience.
     */
    private BukkitAudiences audiences;

    /**
     * The output formatter.
     */
    private OutputFormatter outputFormatter;

    /**
     * The storage adapter.
     */
    IStorageAdapter storageAdapter;

    /**
     * The storage cache.
     */
    IStorageCache storageCache;

    /**
     * Get this instance.
     *
     * @return The plugin instance
     */
    public static Prism getInstance() {
        return instance;
    }

    /**
     * Constructor.
     */
    public Prism() {
        instance = this;
    }

    /**
     * On Enable.
     */
    @Override
    public void onEnable() {
        pluginName = this.getDescription().getName();
        String pluginVersion = this.getDescription().getVersion();
        log(String.format("Initializing %s %s by viveleroi", pluginName, pluginVersion));

        // Load the plugin configuration
        loadConfiguration();

        if (isEnabled()) {
            // Initialize some classes
            audiences = BukkitAudiences.create(this);
            outputFormatter = new OutputFormatter(config().outputs());
            storageCache = new StorageCache();

            // Load or register worlds in storage
            // Note: WorldLoadEvent doesn't appear to fire on server boot.
            for (World world : Bukkit.getServer().getWorlds()) {
                Optional<WorldModel> optionalWorldModel = storageAdapter.getOrRegisterWorld(world);
                if (optionalWorldModel.isPresent()) {
                    storageCache.cacheWorldModel(optionalWorldModel.get());
                } else {
                    String msg = "Failed to create or identify a world from the database. World: %s UUID: %s";
                    error(String.format(msg, world.getName(), world.getUID()));
                }
            }

            // Register listeners
            getServer().getPluginManager().registerEvents(new WorldLoadListener(), this);

            // Register command
            CommandManager commandManager = new CommandManager(this);
            commandManager.register(new AboutCommand());
        }
    }

    /**
     * Disable the plugin.
     */
    protected void disable() {
        Bukkit.getPluginManager().disablePlugin(Prism.getInstance());

        error("Prism has to disable due to a fatal error.");
    }

    @Override
    public void onDisable() {
        storageAdapter.close();
    }

    /**
     * Reloads all configuration files.
     */
    public void loadConfiguration() {
        // Load the main config
        File prismConfigFile = new File(getDataFolder(), "prism.conf");
        prismConfig = Config.getOrWriteConfiguration(PrismConfiguration.class, prismConfigFile);

        File storageConfigFile = new File(getDataFolder(), "storage.conf");
        storageConfig = Config.getOrWriteConfiguration(StorageConfiguration.class, storageConfigFile);

        if (storageConfig.datasource().equalsIgnoreCase("mysql")) {
            storageAdapter = new MysqlStorageAdapter(storageConfig);

            if (!storageAdapter.ready()) {
                disable();
            }
        }
    }

    /**
     * Get the audiences.
     *
     * @return The audiences
     */
    public BukkitAudiences audiences() {
        return audiences;
    }
   
    /**
     * Get the configuration.
     *
     * @return The configuration
     */
    public PrismConfiguration config() {
        return prismConfig;
    }

    /**
     * Get the output formatter.
     *
     * @return The output formatter
     */
    public OutputFormatter outputFormatter() {
        return outputFormatter;
    }

    /**
     * Get the storage adapter.
     *
     * @return The storage adapter
     */
    public IStorageAdapter storageAdapter() {
        return storageAdapter;
    }

    /**
     * Get the storage cache.
     *
     * @return The storage cache
     */
    public IStorageCache storageCache() {
        return storageCache;
    }

    /**
     * Log a message to console.
     *
     * @param message String
     */
    public void log(String message) {
        log.info(String.format("[%s]: %s", pluginName, message));
    }

    /**
     * Log a message to console.
     *
     * @param message String
     */
    public void error(String message) {
        log.warning(String.format("[%s]: %s", pluginName, message));
    }

    /**
     * Log a debug message to console.
     *
     * @param message String
     */
    public void debug(String message) {
        if (prismConfig.debug()) {
            log.info(String.format("[%s]: %s", pluginName, message));
        }
    }

    /**
     * Handle exceptions.
     *
     * @param e The exception
     */
    public void handleException(Exception e) {
        e.printStackTrace();
    }
}
