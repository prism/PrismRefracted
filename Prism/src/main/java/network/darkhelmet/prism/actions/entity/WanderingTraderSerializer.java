package network.darkhelmet.prism.actions.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.WanderingTrader;

public class WanderingTraderSerializer extends MerchantSerializer {
    protected int despawnDelay;

    @Override
    protected void serializer(Entity entity) {
        super.serializer(entity);
        WanderingTrader wanderingTrader = (WanderingTrader) entity;
        despawnDelay = wanderingTrader.getDespawnDelay();
    }

    @Override
    protected void deserializer(Entity entity) {
        super.deserializer(entity);
        WanderingTrader wanderingTrader = (WanderingTrader) entity;
        wanderingTrader.setDespawnDelay(despawnDelay);
    }
}
