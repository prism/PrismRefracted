package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Panda;

public class PandaSerializer extends EntitySerializer {
    protected String mainGene = null;
    protected String hiddenGene = null;

    @Override
    protected void serializer(Entity entity) {
        mainGene = ((Panda) entity).getMainGene().name().toLowerCase();
        hiddenGene = ((Panda) entity).getHiddenGene().name().toLowerCase();
    }

    @Override
    protected void deserializer(Entity entity) {
        Panda.Gene mainPandaGene = MiscUtils.getEnum(mainGene, Panda.Gene.NORMAL);
        Panda.Gene hiddenPandaGene = MiscUtils.getEnum(hiddenGene, Panda.Gene.NORMAL);
        ((Panda) entity).setMainGene(mainPandaGene);
        ((Panda) entity).setHiddenGene(hiddenPandaGene);
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (mainGene != null && hiddenGene != null) {
            String niceName;
            if ("weak".equals(mainGene) && !"weak".equals(hiddenGene)) {
                niceName = "normal";
            } else if ("brown".equals(mainGene) && !"brown".equals(hiddenGene)) {
                niceName = "normal";
            } else {
                niceName = mainGene;
            }

            switch (niceName) {
                case "lazy":
                    niceName = "懒惰的";
                    break;
                case "worried":
                    niceName = "发愁的";
                    break;
                case "playful":
                    niceName = "顽皮的";
                    break;
                case "aggressive":
                    niceName = "好斗的";
                    break;
                case "weak":
                    niceName = "体弱的";
                    break;
                case "brown":
                    niceName = "棕色的";
                    break;
                case "normal":
                    niceName = "普通的";
                    break;
                default:
            }
            sb.insert(start, MiscUtils.niceName(niceName)).insert(start + niceName.length(), ' ');
        }
    }
}
