package network.darkhelmet.prism.actions;

import me.botsko.prism.Prism;
import me.botsko.prism.PrismLocalization;
import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Locale;

public class UseAction extends GenericAction {

    private static final EnumMap<Material, String> names = new EnumMap<>(Material.class);
    private final PrismLocalization prismLocalization;

    public UseAction() {
        prismLocalization = Prism.getInstance().getPrismLocalization();
    }

    static {
        names.put(Material.FLINT_AND_STEEL, "tnt");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNiceName() {
        Material material = getMaterial();

        if (prismLocalization.hasMaterialLocale(material.name())) {
            return prismLocalization.getMaterialLocale(material.name());
        }

        String customName = names.get(material);

        if (customName == null) {
            return material.name().toLowerCase(Locale.ENGLISH);
        }

        return customName;
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