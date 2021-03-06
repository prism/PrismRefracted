package network.darkhelmet.prism.listeners;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.ActionFactory;
import network.darkhelmet.prism.actionlibs.RecordingQueue;
import network.darkhelmet.prism.utils.MaterialTag;
import network.darkhelmet.prism.utils.block.Utilities;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class BaseListener implements Listener {

    protected final Prism plugin;

    protected BaseListener(Prism plugin) {
        this.plugin = plugin;
    }

    protected void contructBlockEvent(final String parentAction, final String cause, final List<Block> blockList) {
        final PrismBlockEvents be = new PrismBlockEvents(plugin); //todo is this necessary?
        for (Block block : blockList) {
            // don't bother record upper doors.
            if (MaterialTag.DOORS.isTagged(block.getType())
                    && ((Door) block.getState().getBlockData()).getHalf() == Bisected.Half.TOP) {
                continue;
            }

            // Change handling a bit if it's a long block
            final Block sibling = Utilities.getSiblingForDoubleLengthBlock(block);
            if (sibling != null && !block.getType().equals(Material.CHEST)
                    && !block.getType().equals(Material.TRAPPED_CHEST)) {
                block = sibling;
            }

            // log items removed from container
            // note: done before the container so a "rewind" for rollback will
            // work properly
            final Block b2 = block;
            be.forEachItem(block, (i, s) -> RecordingQueue.addToQueue(ActionFactory.createItemStack("item-remove",
                    i, i.getAmount(), 0, null, b2.getLocation(), cause)));
            // be.logItemRemoveFromDestroyedContainer( name, block );
            RecordingQueue.addToQueue(ActionFactory.createBlock(parentAction, block, cause));
            // look for relationships
            be.logBlockRelationshipsForBlock(cause, block);

        }
    }

}
