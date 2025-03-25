package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ZombieVillager;

public class ZombieVillagerSerlializer extends EntitySerializer {
    protected String profession = null;
    protected String type = null;

    @Override
    protected void serializer(Entity entity) {
        var zombieVillager = (ZombieVillager) entity;
        profession = zombieVillager.getVillagerProfession().getKey().getKey().toLowerCase();
        type = zombieVillager.getVillagerType().getKey().getKey().toLowerCase();
    }

    @Override
    protected void deserializer(Entity entity) {
        var zombieVillager = (ZombieVillager) entity;
        var namespacedProfessionKey = NamespacedKey.fromString(profession);
        if (namespacedProfessionKey != null) {
            var profession = Registry.VILLAGER_PROFESSION.get(namespacedProfessionKey);
            if (profession != null) {
                zombieVillager.setVillagerProfession(profession);
            }
        }

        var namespacedTypeKey = NamespacedKey.fromString(type);
        if (namespacedTypeKey != null) {
            var villagerType = Registry.VILLAGER_TYPE.get(namespacedTypeKey);
            if (villagerType != null) {
                zombieVillager.setVillagerType(villagerType);
            }
        }
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (profession != null) {
            sb.insert(start, MiscUtils.niceName(profession)).insert(start + profession.length(), ' ');
        }
    }
}
