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

package network.darkhelmet.prism;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.mattstudios.mf.base.CommandManager;

import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.commands.AboutCommand;
import network.darkhelmet.prism.commands.NearCommand;
import network.darkhelmet.prism.commands.RestoreCommand;
import network.darkhelmet.prism.commands.RollbackCommand;
import network.darkhelmet.prism.config.Config;
import network.darkhelmet.prism.config.PrismConfiguration;
import network.darkhelmet.prism.config.StorageConfiguration;
import network.darkhelmet.prism.injection.PrismModule;
import network.darkhelmet.prism.listeners.BlockBreakListener;
import network.darkhelmet.prism.listeners.PlayerDropItemListener;
import network.darkhelmet.prism.recording.RecordingManager;

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
    private static final Logger logger = LogManager.getLogger("Prism");

    /**
     * The injector.
     */
    private Injector injector;

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
     * The storage adapter.
     */
    private IStorageAdapter storageAdapter;

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

    @Override
    public void onLoad() {
        // Load the plugin configuration
        loadConfiguration();

        this.injector = Guice.createInjector(new PrismModule(logger, prismConfig, storageConfig));

        storageAdapter = injector.getInstance(IStorageAdapter.class);
        if (!storageAdapter.ready()) {
            disable();
        }
    }

    /**
     * On enable.
     */
    @Override
    public void onEnable() {
        String pluginName = this.getDescription().getName();
        String pluginVersion = this.getDescription().getVersion();
        logger.info("Initializing {} {} by viveleroi", pluginName, pluginVersion);

        serializerVersion = mcVersion();
        logger.info("Serializer version: {}", serializerVersion);

        if (isEnabled()) {
            // Initialize some classes
            injector.getInstance(RecordingManager.class);
            taskChainFactory = BukkitTaskChainFactory.create(this);

            // Register listeners
            getServer().getPluginManager().registerEvents(injector.getInstance(BlockBreakListener.class), this);
            getServer().getPluginManager().registerEvents(injector.getInstance(PlayerDropItemListener.class), this);

            // Register command
            CommandManager commandManager = new CommandManager(this);
            commandManager.register(new AboutCommand());
            commandManager.register(injector.getInstance(NearCommand.class));
            commandManager.register(injector.getInstance(RestoreCommand.class));
            commandManager.register(injector.getInstance(RollbackCommand.class));
        }
    }

    /**
     * Disable the plugin.
     */
    protected void disable() {
        Bukkit.getPluginManager().disablePlugin(Prism.getInstance());

        logger.error("Prism has to disable due to a fatal error.");
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
     * Get the serializer version.
     *
     * @return The version
     */
    public short serializerVersion() {
        return serializerVersion;
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
     * Log a debug message to console.
     *
     * @param message String
     */
    public void debug(String message) {
        if (prismConfig.debug()) {
            logger.info(message);
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
