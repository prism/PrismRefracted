package network.darkhelmet.prism.actions;

import network.darkhelmet.prism.api.ChangeResult;
import network.darkhelmet.prism.api.ChangeResultType;
import network.darkhelmet.prism.api.PrismParameters;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlowerPotChangeAction extends BlockChangeAction {

    @Override
    public ChangeResult applyRollback(Player player, PrismParameters parameters, boolean isPreview) {
        ChangeResult changeResult = super.applyRollback(player, parameters, isPreview);
        if (changeResult.getType() == ChangeResultType.APPLIED && !isPreview) {
            placeFlowerItem(getMaterial(), getOldMaterial());
        }
        return changeResult;
    }

    @Override
    public ChangeResult applyRestore(Player player, PrismParameters parameters, boolean isPreview) {
        ChangeResult changeResult = super.applyRollback(player, parameters, isPreview);
        if (changeResult.getType() == ChangeResultType.APPLIED && !isPreview) {
            placeFlowerItem(getOldMaterial(), getMaterial());
        }
        return changeResult;
    }

    private void placeFlowerItem(Material from, Material to) {
        Player player = Bukkit.getPlayer(getUuid());
        if (player == null) {
            return;
        }
        if (from == Material.FLOWER_POT) {
            ItemStack flower = new ItemStack(Material.valueOf(to.name().substring(7)));
            player.getInventory().removeItem(flower);
        } else {
            ItemStack flower = new ItemStack(Material.valueOf(from.name().substring(7)));
            player.getInventory().addItem(flower);
        }
    }

    @Override
    public String getNiceName() {
        return getOldMaterial().name().toLowerCase().replace('_', ' ')
                + " to "
                + getMaterial().name().toLowerCase().replace('_', ' ');
    }

}
