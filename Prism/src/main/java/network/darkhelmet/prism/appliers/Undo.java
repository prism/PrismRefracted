package network.darkhelmet.prism.appliers;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.api.actions.Handler;
import org.bukkit.entity.Player;

import java.util.List;

public class Undo extends Preview {

    /**
     * Constructor.
     *
     * @param plugin Prism
     */
    public Undo(Prism plugin, Player player, List<Handler> results, QueryParameters parameters,
                ApplierCallback callback) {
        super(plugin, player, results, parameters, callback);
    }

    @Override
    public void preview() {
        Prism.messenger.sendMessage(player, Prism.messenger.playerError("You can't preview an undo."));
    }
}