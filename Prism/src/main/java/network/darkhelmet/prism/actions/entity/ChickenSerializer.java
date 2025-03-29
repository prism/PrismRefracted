package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;

public class ChickenSerializer extends EntitySerializer {
    protected String variant = null;

    @Override
    protected void serializer(Entity entity) {
        variant = ((Chicken) entity).getVariant().getKey().getKey();
    }

    @Override
    protected void deserializer(Entity entity) {
        var chicken = (Chicken) entity;
        var namespacedKey = NamespacedKey.fromString(variant);
        if (namespacedKey != null) {
            var variantObj = Registry.CHICKEN_VARIANT.get(namespacedKey);
            if (variantObj != null) {
                chicken.setVariant(variantObj);
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
