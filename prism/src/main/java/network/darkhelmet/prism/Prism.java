package network.darkhelmet.prism;

import java.io.File;
import java.util.logging.Logger;

import me.mattstudios.mf.base.CommandManager;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import network.darkhelmet.prism.commands.AboutCommand;
import network.darkhelmet.prism.config.Config;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.formatters.OutputFormatter;

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
     * Cache the plugin name.
     */
    private String pluginName;

    /**
     * Cache the plugin version.
     */
    private String pluginVersion;

    /**
     * The bukkit audience.
     */
    private BukkitAudiences audiences;

    /**
     * The output formatter.
     */
    private OutputFormatter outputFormatter;

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
        pluginVersion = this.getDescription().getVersion();
        log(String.format("Initializing %s %s by viveleroi", pluginName, pluginVersion));

        // Load the plugin configuration
        loadConfigurations();

        if (isEnabled()) {
            audiences = BukkitAudiences.create(this);
            outputFormatter = new OutputFormatter(config().outputs());

            CommandManager commandManager = new CommandManager(this);
            commandManager.register(new AboutCommand());
        }
    }

    /**
     * Reloads all configuration files.
     */
    public void loadConfigurations() {
        // Load the main config
        File prismConfigFile = new File(getDataFolder(), "prism.conf");
        prismConfig = Config.getOrWriteConfiguration(PrismConfiguration.class, prismConfigFile);
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
