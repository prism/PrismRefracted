package network.darkhelmet.prism.utils;

import network.darkhelmet.prism.Prism;
import org.bukkit.Material;

public class VersionAdapter {

    private VersionAdapter() {
        // private
    }

    public static final Material MATERIAL_GRASS_BLOCK;

    static {
        if ((Prism.getInstance().getServerMajorVersion() == 20 && Prism.getInstance().getServerMinorVersion() >= 3)
                || Prism.getInstance().getServerMajorVersion() > 20) {
            MATERIAL_GRASS_BLOCK = Material.GRASS_BLOCK;
        } else {
            MATERIAL_GRASS_BLOCK = Material.valueOf("GRASS");
        }
    }

}
