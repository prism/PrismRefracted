package network.darkhelmet.prism.api.actions;

import org.bukkit.Location;
import org.bukkit.Material;

public record ActionData(
    Location location,
    Material material,
    String materialName,
    String materialData,
    String customData) {
}
