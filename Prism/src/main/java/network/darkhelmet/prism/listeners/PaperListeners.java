package network.darkhelmet.prism.listeners;

//import io.papermc.paper.event.block.TargetHitEvent;
//import io.papermc.paper.event.player.PlayerTradeEvent;
import network.darkhelmet.prism.Prism;
import org.bukkit.event.Listener;

/**
 * This class carries listeners that will only work currently with paper.
 *
 * @author Narimm on 1/01/2021.
 */
public class PaperListeners implements Listener {

    Prism plugin;

    public PaperListeners(Prism plugin) {
        this.plugin = plugin;
    }

//    /**
//     * React to a target hit event.
//     * @param event the TargetHitEvent.
//     */
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onTargetHitEvent(TargetHitEvent event) {
//        Projectile projectile = event.getEntity();
//        ProjectileSource shooter = projectile.getShooter();
//        if (shooter instanceof Player) {
//            if (!Prism.getIgnore().event("target-hit", (Player) shooter)) {
//                return;
//            }
//            Block block = event.getHitBlock();
//            RecordingQueue.addToQueue(ActionFactory.createBlock("target-hit",block,(Player) shooter));
//        }
//    }
//
//    /**
//     * TradeEvent - Paper Only.
//     * @param event PlayerTradeEvent
//     */
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onPlayerTrade(PlayerTradeEvent event) {
//        Player player = event.getPlayer();
//        if (!Prism.getIgnore().event("player-trade", player)) {
//            return;
//        }
//        RecordingQueue.addToQueue(ActionFactory.createEntity("player-trade",event.getVillager(),player));
//        ItemStack result = event.getTrade().getResult();
//        RecordingQueue.addToQueue(ActionFactory.createItemStack("item-receive",result,result.getAmount(),
//                -1,result.getEnchantments(),event.getVillager().getLocation(),player));
//
//    }

}
