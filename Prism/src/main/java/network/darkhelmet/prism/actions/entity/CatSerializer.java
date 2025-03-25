package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;

public class CatSerializer extends EntitySerializer {
    protected String variant = null;

    @Override
    protected void serializer(Entity entity) {
        variant = ((Cat) entity).getCatType().name().toLowerCase();
    }

    @Override
    protected void deserializer(Entity entity) {
        var namespacedTypeKey = NamespacedKey.fromString(variant);
        if (namespacedTypeKey != null) {
            var catVariant = Registry.CAT_VARIANT.get(namespacedTypeKey);
            if (catVariant != null) {
                ((Cat) entity).setCatType(catVariant);
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
