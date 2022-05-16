package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class VillagerSerializer extends MerchantSerializer {
    protected String profession = null;
    protected String type = null;
    protected int level = -1;
    protected int experience = -1;

    @Override
    protected void serializer(Entity entity) {
        Villager villager = (Villager) entity;
        profession = villager.getProfession().name().toLowerCase();
        type = villager.getVillagerType().name().toLowerCase();
        level = villager.getVillagerLevel();
        experience = villager.getVillagerExperience();
        super.serializer(entity);
    }

    @Override
    protected void deserializer(Entity entity) {
        super.deserializer(entity);
        Villager villager = (Villager) entity;
        villager.setProfession(MiscUtils.getEnum(profession, Profession.FARMER));
        villager.setVillagerType(MiscUtils.getEnum(type, Villager.Type.PLAINS));
        if (level != -1) {
            villager.setVillagerLevel(level);
        }
        if (experience != -1) {
            villager.setVillagerExperience(experience);
        }
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (profession != null) {
            sb.insert(start, MiscUtils.niceName(profession)).insert(start + profession.length(), ' ');
        }
    }
}
