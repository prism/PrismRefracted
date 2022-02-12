package network.darkhelmet.prism;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import network.darkhelmet.prism.bridge.PrismBlockEditHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

public class ApiHandler {

    public enum WEType {
        WORLDEDIT("WorldEdit"),
        ASYNC_WORLDEDIT("AsyncWorldEdit"),
        FAST_ASYNC_WORLDEDIT("FastAsyncWorldEdit");

        private final String pluginId;

        WEType(String pluginId) {
            this.pluginId = pluginId;
        }

        public String getPluginId() {
            return pluginId;
        }
    }

    private static final Collection<String> enabledPlugins = new ArrayList<>();
    public static WorldEditPlugin worldEditPlugin = null;
    private static PrismBlockEditHandler handler;

    private ApiHandler() {
    }

    static void hookWorldEdit() {
        WEType weType = null;
        if (Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null) {
            weType = WEType.FAST_ASYNC_WORLDEDIT;
        } else if (Bukkit.getServer().getPluginManager().getPlugin("AsyncWorldEdit") != null) {
            weType = WEType.ASYNC_WORLDEDIT;
        } else if (Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            weType = WEType.WORLDEDIT;
        }

        final Plugin we = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (we != null) {
            worldEditPlugin = (WorldEditPlugin) we;
            enabledPlugins.add(we.getName());
            // Easier and foolproof way.
            try {
                handler = new PrismBlockEditHandler(weType);
                WorldEdit.getInstance().getEventBus().register(handler);
                Prism.log("发现 " + weType.pluginId + ". 相关功能已启用.");
            } catch (Throwable error) {
                Prism.log("需要 WorldEdit v7.1.0 或更高版本!"
                        + " Prism 已禁用的相关可选功能.");
                Prism.debug(error.getMessage());
            }

        } else {
            Prism.log("未发现 WorldEdit. Prism 已禁用的相关可选功能.");
        }
    }

    static boolean checkDependency(String pluginName) {
        return ApiHandler.enabledPlugins.contains(pluginName);
    }
    
    static boolean disableWorldEditHook() {
        if (worldEditPlugin != null) {
            try {
                WorldEdit.getInstance().getEventBus().unregister(handler);
                Prism.log("已取消 WorldEdit 挂钩");
                enabledPlugins.remove(worldEditPlugin.getName());
                worldEditPlugin = null;
                return true;
            } catch (Throwable error) {
                Prism.log("我们无法取消 worldEdit 的挂钩... 它是否在启用状态中???");
                Prism.debug(error.getMessage());
                return false;
            }
        } else {
            return true;
        }
    }
}
