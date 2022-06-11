package network.darkhelmet.prism.actions;

import io.github.rothes.prismcn.CNLocalization;
import org.bukkit.Material;

import java.util.EnumMap;

public class UseAction extends GenericAction {

    private static final EnumMap<Material, String> names = new EnumMap<>(Material.class);

    static {
        names.put(Material.FLINT_AND_STEEL, "tnt");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNiceName() {
        Material material = getMaterial();

        return CNLocalization.getMaterialLocale(material);

        // Removed in Chinese Edition

//        String customName = names.get(material);
//
//        if (customName == null) {
//            return material.name().toLowerCase(Locale.ENGLISH);
//        }
//
//        return customName;
    }

    @Override
    public boolean hasExtraData() {
        return false;
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public void deserialize(String data) {
    }
}