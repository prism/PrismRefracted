package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;

public class PigSerializer extends EntitySerializer {
    protected String variant = null;

    @Override
    protected void serializer(Entity entity) {
        variant = ((Pig) entity).getVariant().getKey().getKey();
    }

    @Override
    protected void deserializer(Entity entity) {
        var pig = (Pig) entity;
        var namespacedKey = NamespacedKey.fromString(variant);
        if (namespacedKey != null) {
            var variantObj = Registry.PIG_VARIANT.get(namespacedKey);
            if (variantObj != null) {
                pig.setVariant(variantObj);
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
