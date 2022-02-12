package network.darkhelmet.prism;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import network.darkhelmet.prism.bridge.PrismBlockEditHandler;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

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
                Prism.log("发现 WorldEdit. 相关功能已启用.");
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
    
    private static boolean disableWorldEditHook() {
        if (worldEditPlugin != null) {
            try {
                WorldEdit.getInstance().getEventBus().unregister(new PrismBlockEditHandler());
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
