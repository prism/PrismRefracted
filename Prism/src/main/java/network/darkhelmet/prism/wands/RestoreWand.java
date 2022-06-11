package network.darkhelmet.prism.wands;

import io.github.rothes.prismcn.CNLocalization;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.actionlibs.QueryResult;
import network.darkhelmet.prism.api.actions.PrismProcessType;
import network.darkhelmet.prism.appliers.Previewable;
import network.darkhelmet.prism.appliers.PrismApplierCallback;
import network.darkhelmet.prism.appliers.Restore;
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
                    : CNLocalization.getMaterialLocale(block.getType())
                    + (block.getType().toString().endsWith("BLOCK") ? "" : " 方块"));
            Prism.messenger.sendMessage(player,
                    Prism.messenger.playerError("没有在这个 " + space_name + " 处查找到任何历史数据."));
        }
    }

}
