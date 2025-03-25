package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

public class VillagerSerializer extends MerchantSerializer {
    protected String profession = null;
    protected String type = null;
    protected int level = -1;
    protected int experience = -1;

    @Override
    protected void serializer(Entity entity) {
        Villager villager = (Villager) entity;
        profession = villager.getProfession().getKey().getKey().toLowerCase();
        type = villager.getVillagerType().getKey().getKey().toLowerCase();
        level = villager.getVillagerLevel();
        experience = villager.getVillagerExperience();
        super.serializer(entity);
    }

    @Override
    protected void deserializer(Entity entity) {
        Villager villager = (Villager) entity;

        var namespacedProfessionKey = NamespacedKey.fromString(profession);
        if (namespacedProfessionKey != null) {
            var profession = Registry.VILLAGER_PROFESSION.get(namespacedProfessionKey);
            if (profession != null) {
                villager.setProfession(profession);
            }
        }

        var namespacedTypeKey = NamespacedKey.fromString(type);
        if (namespacedTypeKey != null) {
            var villagerType = Registry.VILLAGER_TYPE.get(namespacedTypeKey);
            if (villagerType != null) {
                villager.setVillagerType(villagerType);
            }
        }

        if (level != -1) {
            villager.setVillagerLevel(level);
        }
        if (experience != -1) {
            villager.setVillagerExperience(experience);
        }
        super.deserializer(entity);
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (profession != null) {
            sb.insert(start, MiscUtils.niceName(profession)).insert(start + profession.length(), ' ');
        }
    }
}
