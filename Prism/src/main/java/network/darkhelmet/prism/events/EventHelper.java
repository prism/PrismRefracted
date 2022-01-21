package network.darkhelmet.prism.events;

import network.darkhelmet.prism.api.BlockStateChange;
import network.darkhelmet.prism.api.PrismApi;
import network.darkhelmet.prism.api.PrismParameters;
import network.darkhelmet.prism.api.objects.ApplierResult;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EventHelper {

    public static PrismRollBackEvent createRollBackEvent(List<BlockStateChange> blockStateChanges,
                                                         Player onBehalfOf, PrismParameters parameters,
                                                         ApplierResult result) {
        return new PrismRollBackEvent(blockStateChanges, onBehalfOf, parameters, result);
    }

    public static PrismLoadedEvent createLoadEvent(PrismApi api) {
        return new PrismLoadedEvent(api);
    }

    public static PrismUnloadEvent createUnLoadEvent() {
        return new PrismUnloadEvent();
    }

    public static PrismDrainEvent createDrainEvent(ArrayList<BlockStateChange> blockStateChanges,
                                                   Player onBehalfOf, int radius) {
        return new PrismDrainEvent(blockStateChanges, onBehalfOf, radius);
    }

    public static PrismExtinguishEvent createExtinguishEvent(ArrayList<BlockStateChange> blockStateChanges,
                                                             Player onBehalfOf, int radius) {
        return new PrismExtinguishEvent(blockStateChanges,onBehalfOf,radius);
    }
}
