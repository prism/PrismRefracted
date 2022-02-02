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

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.darkhelmet.prism.api.actions.IActionRegistry;
import network.darkhelmet.prism.api.actions.types.IActionType;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.commands.AboutCommand;
import network.darkhelmet.prism.commands.LookupCommand;
import network.darkhelmet.prism.commands.NearCommand;
import network.darkhelmet.prism.commands.ReloadCommand;
import network.darkhelmet.prism.commands.RestoreCommand;
import network.darkhelmet.prism.commands.RollbackCommand;
import network.darkhelmet.prism.commands.WandCommand;
import network.darkhelmet.prism.injection.PrismModule;
import network.darkhelmet.prism.listeners.BlockBreakListener;
import network.darkhelmet.prism.listeners.BlockPlaceListener;
import network.darkhelmet.prism.listeners.EntityDeathListener;
import network.darkhelmet.prism.listeners.HangingBreakListener;
import network.darkhelmet.prism.listeners.PlayerDropItemListener;
import network.darkhelmet.prism.listeners.PlayerInteractListener;
import network.darkhelmet.prism.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.recording.RecordingService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
     * The configuration service.
     */
    private ConfigurationService configurationService;

    /**
     * The action registry.
     */
    private IActionRegistry actionRegistry;

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
        this.injector = Guice.createInjector(
            new PrismModule(this, logger));

        // Load the configuration service (and files)
        configurationService = injector.getInstance(ConfigurationService.class);
        actionRegistry = injector.getInstance(IActionRegistry.class);

        // Choose and initialize the datasource
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
            injector.getInstance(RecordingService.class);
            taskChainFactory = BukkitTaskChainFactory.create(this);

            // Register listeners
            getServer().getPluginManager().registerEvents(injector.getInstance(BlockBreakListener.class), this);
            getServer().getPluginManager().registerEvents(injector.getInstance(BlockPlaceListener.class), this);
            getServer().getPluginManager().registerEvents(injector.getInstance(EntityDeathListener.class), this);
            getServer().getPluginManager().registerEvents(injector.getInstance(HangingBreakListener.class), this);
            getServer().getPluginManager().registerEvents(injector.getInstance(PlayerDropItemListener.class), this);
            getServer().getPluginManager().registerEvents(injector.getInstance(PlayerInteractListener.class), this);

            // Register commands
            BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

            // Register action types auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("actions"), (sender, context) -> {
                List<String> actionFamilies = new ArrayList<>();
                for (IActionType actionType : actionRegistry.actionTypes()) {
                    actionFamilies.add(actionType.familyKey());
                }

                return actionFamilies;
            });

            // Register materials auto-suggest
            // @todo remove when the command lib implements this
            commandManager.registerSuggestion(SuggestionKey.of("materials"), (sender, context) -> {
                List<String> materials = new ArrayList<>();
                for (Material material : Material.values()) {
                    materials.add(material.toString().toLowerCase(Locale.ENGLISH));
                }

                return materials;
            });

            // Register entity types auto-suggest
            // @todo remove when the command lib implements this
            commandManager.registerSuggestion(SuggestionKey.of("entityTypes"), (sender, context) -> {
                List<String> entityTypes = new ArrayList<>();
                for (EntityType entityType : EntityType.values()) {
                    entityTypes.add(entityType.toString().toLowerCase(Locale.ENGLISH));
                }

                return entityTypes;
            });

            // Register online player auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> {
                List<String> players = new ArrayList<>();
                for (Player player : getServer().getOnlinePlayers()) {
                    players.add(player.getName());
                }

                return players;
            });

            // Register "in" parameter
            commandManager.registerSuggestion(SuggestionKey.of("ins"), (sender, context) ->
                Arrays.asList("chunk", "world"));

            commandManager.registerCommand(injector.getInstance(AboutCommand.class));
            commandManager.registerCommand(injector.getInstance(LookupCommand.class));
            commandManager.registerCommand(injector.getInstance(NearCommand.class));
            commandManager.registerCommand(injector.getInstance(ReloadCommand.class));
            commandManager.registerCommand(injector.getInstance(RestoreCommand.class));
            commandManager.registerCommand(injector.getInstance(RollbackCommand.class));
            commandManager.registerCommand(injector.getInstance(WandCommand.class));
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
        if (configurationService.prismConfig().debug()) {
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
