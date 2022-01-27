package network.darkhelmet.prism.api.actions;

import org.bukkit.Material;

public record ActionData(
    Material material,
    String materialName,
    String materialData,
    String customData,
    Short customDataVersion) {
}
