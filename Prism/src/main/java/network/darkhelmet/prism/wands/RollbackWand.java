package network.darkhelmet.prism.wands;

import io.github.rothes.prismcn.CNLocalization;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.actionlibs.QueryResult;
import network.darkhelmet.prism.api.actions.PrismProcessType;
import network.darkhelmet.prism.appliers.PrismApplierCallback;
import network.darkhelmet.prism.appliers.Rollback;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RollbackWand extends QueryWandBase {

    /**
     * Constructor.
     * @param plugin Prism
     */
    public RollbackWand(Prism plugin) {
        super(plugin);
    }

    @Override
    public void playerLeftClick(Player player, Location loc) {
        if (loc != null) {
            rollback(player, loc);
        }
    }

    @Override
    public void playerRightClick(Player player, Location loc) {
        if (loc != null) {
            rollback(player, loc);
        }
    }

    @Override
    public void playerRightClick(Player player, Entity entity) {
    }

    protected void rollback(Player player, Location loc) {

        final Block block = loc.getBlock();
        QueryParameters params = checkQueryParams(block,parameters,player);
        params.setProcessType(PrismProcessType.ROLLBACK);
        final QueryResult results = getResult(params, player);
        if (!results.getActionResults().isEmpty()) {
            final Rollback rb = new Rollback(plugin, player, results.getActionResults(), params,
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

    @Override
    public void setItemWasGiven(boolean given) {
        this.itemGiven = given;
    }

    @Override
    public boolean itemWasGiven() {
        return itemGiven;
    }
}