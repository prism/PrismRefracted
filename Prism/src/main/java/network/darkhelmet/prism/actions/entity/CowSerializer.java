package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;

public class CowSerializer extends EntitySerializer {
    protected String variant = null;

    @Override
    protected void serializer(Entity entity) {
        var cow = (Cow) entity;
        variant = cow.getVariant().getKey().getKey();
    }

    @Override
    protected void deserializer(Entity entity) {
        var cow = (Cow) entity;
        var namespacedKey = NamespacedKey.fromString(variant);
        if (namespacedKey != null) {
            var variantObj = Registry.COW_VARIANT.get(namespacedKey);
            if (variantObj != null) {
                cow.setVariant(variantObj);
            }
        }
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (variant != null) {
            sb.insert(start, MiscUtils.niceName(variant)).insert(start + variant.length(), ' ');
        }
    }
}
