package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

public class WolfSerlializer extends EntitySerializer {
    protected String color = null;
    protected String variant = null;

    @Override
    protected void serializer(Entity entity) {
        var wolf = (Wolf) entity;
        color = wolf.getCollarColor().name().toLowerCase();
        variant = wolf.getVariant().getKey().getKey();
    }

    @Override
    protected void deserializer(Entity entity) {
        var wolf = (Wolf) entity;

        wolf.setCollarColor(MiscUtils.getEnum(color, DyeColor.RED));

        var namespacedKey = NamespacedKey.fromString(variant);
        if (namespacedKey != null) {
            var variantObj = Registry.WOLF_VARIANT.get(namespacedKey);
            if (variantObj != null) {
                wolf.setVariant(variantObj);
            }
        }
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (color != null) {
            sb.insert(start, MiscUtils.niceName(color)).insert(start + color.length(), ' ');
        }
    }
}
