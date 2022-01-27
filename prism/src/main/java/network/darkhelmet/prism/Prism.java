package network.darkhelmet.prism;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.mattstudios.mf.base.CommandManager;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import network.darkhelmet.prism.actions.ActionRegistry;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.commands.AboutCommand;
import network.darkhelmet.prism.commands.NearCommand;
import network.darkhelmet.prism.commands.RestoreCommand;
import network.darkhelmet.prism.commands.RollbackCommand;
import network.darkhelmet.prism.config.Config;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.displays.DisplayManager;
import network.darkhelmet.prism.formatters.OutputFormatter;
import network.darkhelmet.prism.listeners.BlockBreakListener;
import network.darkhelmet.prism.listeners.PlayerDropItemListener;
import network.darkhelmet.prism.recording.RecordingManager;
import network.darkhelmet.prism.storage.mysql.MysqlStorageAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Prism extends JavaPlugin {
    /**
     * Cache static instance.
     */
    private static Prism instance;

    /**
     * The logger.
     */
    private static final Logger log = LogManager.getLogger("Minecraft");

    /**
     * Sets a numeric version we can use to handle differences between serialization formats.
     */
    protected short serializerVersion;

    /**
     * The task chain factory.
     */
    private static TaskChainFactory taskChainFactory;

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
    private IStorageAdapter storageAdapter;

    /**
     * The action registry.
     */
    private ActionRegistry actionRegistry = new ActionRegistry();

    /**
     * The recording manager.
     */
    private RecordingManager recordingManager;

    /**
     * The display manager.
     */
    private DisplayManager displayManager;

    /**
     * The translation system.
     */
    private I18n i18n;

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
     * On enable.
     */
    @Override
    public void onEnable() {
        pluginName = this.getDescription().getName();
        String pluginVersion = this.getDescription().getVersion();
        log(String.format("Initializing %s %s by viveleroi", pluginName, pluginVersion));

        serializerVersion = mcVersion();
        log(String.format("Serializer version: %d", serializerVersion));

        // Load the plugin configuration
        loadConfiguration();

        if (isEnabled()) {
            // Initialize the translation system
            try {
                i18n = new I18n(log, getDataFolder().toPath(), prismConfig.defaultLocale());
            } catch (IOException e) {
                handleException(e);
            }

            // Initialize some classes
            audiences = BukkitAudiences.create(this);
            outputFormatter = new OutputFormatter(config().outputs());
            recordingManager = new RecordingManager();
            displayManager = new DisplayManager();
            taskChainFactory = BukkitTaskChainFactory.create(this);

            // Register listeners
            getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);

            // Register command
            CommandManager commandManager = new CommandManager(this);
            commandManager.register(new AboutCommand());
            commandManager.register(new NearCommand());
            commandManager.register(new RestoreCommand());
            commandManager.register(new RollbackCommand());
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
        if (storageAdapter != null) {
            storageAdapter.close();
        }
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
     * Parses the mc version as a short. Fed to nbt serializers.
     *
     * @return The mc version as a number
     */
    protected Short mcVersion() {
        Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]+)");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());
        if (matcher.find()) {
            return Short.parseShort(matcher.group(1).replace(".", ""));
        }

        return null;
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
     * Get the storage configuration.
     *
     * @return The storage configuration
     */
    public StorageConfiguration storageConfig() {
        return storageConfig;
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
     * Get the action registry.
     *
     * @return The action registry.
     */
    public ActionRegistry actionRegistry() {
        return actionRegistry;
    }

    /**
     * Get the recording manager.
     *
     * @return The recording manager
     */
    public RecordingManager recordingManager() {
        return recordingManager;
    }

    /**
     * Get the display manager.
     *
     * @return The display manager
     */
    public DisplayManager displayManager() {
        return displayManager;
    }

    /**
     * Get the serializer version.
     *
     * @return The version
     */
    public short serializerVersion() {
        return serializerVersion;
    }

    /**
     * Get the translation system.
     *
     * @return The translation system
     */
    public I18n i18n() {
        return i18n;
    }

    /**
     * Create a new task chain.
     *
     * @param <T> The type
     * @return The chain
     */
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    /**
     * Log a message to console.
     *
     * @param message String
     */
    public void log(String message) {
        log.info("[{}]: {}", pluginName, message);
    }

    /**
     * Log a message to console.
     *
     * @param message String
     */
    public void error(String message) {
        log.warn("[{}]: {}", pluginName, message);
    }

    /**
     * Log a debug message to console.
     *
     * @param message String
     */
    public void debug(String message) {
        if (prismConfig.debug()) {
            log.info("[{}]: {}", pluginName, message);
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
