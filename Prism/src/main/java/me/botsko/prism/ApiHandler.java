package me.botsko.prism;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.bridge.PrismBlockEditHandler;
import me.botsko.prism.measurement.QueueStats;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created for the Prism Project.
 * Created by Narimm on 20/05/2020.
 */
public class ApiHandler {
    private static final Collection<String> enabledPlugins = new ArrayList<>();
    public static WorldEditPlugin worldEditPlugin = null;

    private ApiHandler() {
    }

    static void hookWorldEdit() {
        final Plugin we = Prism.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");
        if (we != null) {
            worldEditPlugin = (WorldEditPlugin) we;
            enabledPlugins.add(we.getName());
            // Easier and foolproof way.
            try {
                WorldEdit.getInstance().getEventBus().register(new PrismBlockEditHandler());
                Prism.log("WorldEdit found. Associated features enabled.");
            } catch (Throwable error) {
                Prism.log("Required WorldEdit version is 7.1.0 or greater!"
                        + " Certain optional features of Prism disabled.");
                Prism.debug(error.getMessage());
            }

        } else {
            Prism.log("WorldEdit not found. Certain optional features of Prism disabled.");
        }
    }

    static boolean checkDependency(String pluginName) {
        return ApiHandler.enabledPlugins.contains(pluginName);
    }
    
    private static boolean disableWorldEditHook() {
        if (worldEditPlugin != null) {
            try {
                WorldEdit.getInstance().getEventBus().unregister(new PrismBlockEditHandler());
                Prism.log("WorldEdit unhooked");
                enabledPlugins.remove(worldEditPlugin.getName());
                worldEditPlugin = null;
                return true;
            } catch (Throwable error) {
                Prism.log("We could not unhook worldEdit...was it enabled???");
                Prism.debug(error.getMessage());
                return false;
            }
        } else {
            return true;
        }
    }
}
