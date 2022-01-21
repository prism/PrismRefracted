package network.darkhelmet.prism.appliers;

import network.darkhelmet.prism.api.objects.ApplierResult;
import org.bukkit.command.CommandSender;

public interface ApplierCallback {
    void handle(CommandSender sender, ApplierResult result);
}
