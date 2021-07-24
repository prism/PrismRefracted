package me.botsko.prism.wands;

import me.botsko.prism.Prism;
import me.botsko.prism.PrismLocalization;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.api.actions.PrismProcessType;
import me.botsko.prism.appliers.Previewable;
import me.botsko.prism.appliers.PrismApplierCallback;
import me.botsko.prism.appliers.Restore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RestoreWand extends QueryWandBase {

    /**
     * Constructor.
     * @param plugin Prism
     */
    public RestoreWand(Prism plugin) {
        super(plugin);
    }

    @Override
    public void playerLeftClick(Player player, Location loc) {
        if (loc != null) {
            restore(player, loc);
        }
    }

    @Override
    public void playerRightClick(Player player, Location loc) {
        if (loc != null) {
            restore(player, loc);
        }
    }

    @Override
    public void playerRightClick(Player player, Entity entity) {
    }

    protected void restore(Player player, Location loc) {

        final Block block = loc.getBlock();
        QueryParameters params = checkQueryParams(block, parameters, player);
        params.setProcessType(PrismProcessType.RESTORE);
        final QueryResult results = getResult(params, player);
        if (!results.getActionResults().isEmpty()) {
            final Previewable rb = new Restore(plugin, player, results.getActionResults(), params,
                    new PrismApplierCallback());
            rb.apply();
        } else {
            final String space_name = (block.getType().equals(Material.AIR) ? "空方块"
                    : prismLocalization.hasMaterialLocale(block.getType().name())?
                    prismLocalization.getMaterialLocale(block.getType().name())
                    : block.getType().toString().replaceAll("_", " ").toLowerCase()
                    + (block.getType().toString().endsWith("BLOCK") ? "" : " 方块"));
            Prism.messenger.sendMessage(player,
                    Prism.messenger.playerError("没有在这个 " + space_name + " 处查找到任何历史数据."));
        }
    }

}
