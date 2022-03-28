package network.darkhelmet.prism.actions.entity;

import network.darkhelmet.prism.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VillagerSerializer extends EntitySerializer {
    protected String profession = null;
    protected String type = null;
    protected int level = -1;
    protected int experience = -1;
    protected List<RecipeData> recipeDataList = null;

    @Override
    protected void serializer(Entity entity) {
        Villager villager = (Villager) entity;
        profession = villager.getProfession().name().toLowerCase();
        type = villager.getVillagerType().name().toLowerCase();
        recipeDataList = new ArrayList<>();
        for (MerchantRecipe recipe : villager.getRecipes()) {
            RecipeData recipeData = new RecipeData();
            recipeData.result = recipe.getResult().serialize();
            for (ItemStack ingredient : recipe.getIngredients()) {
                recipeData.ingredients.add(ingredient.serialize());
            }
            recipeData.uses = recipe.getUses();
            recipeData.maxUses = recipe.getMaxUses();
            recipeData.experienceReward = recipe.hasExperienceReward();
            recipeData.villagerExperience = recipe.getVillagerExperience();
            recipeData.priceMultiplier = recipe.getPriceMultiplier();
            recipeDataList.add(recipeData);
        }
        level = villager.getVillagerLevel();
        experience = villager.getVillagerExperience();
    }

    @Override
    protected void deserializer(Entity entity) {
        Villager villager = (Villager) entity;
        villager.setProfession(MiscUtils.getEnum(profession, Profession.FARMER));
        villager.setVillagerType(MiscUtils.getEnum(type, Villager.Type.PLAINS));
        if (level != -1) {
            villager.setVillagerLevel(level);
        }
        if (experience != -1) {
            villager.setVillagerExperience(experience);
        }
        List<MerchantRecipe> bukkitRecipes = new ArrayList<>();
        if (recipeDataList != null) {
            for (RecipeData recipeData : recipeDataList) {
                MerchantRecipe bukkit = new MerchantRecipe(ItemStack.deserialize(recipeData.result), recipeData.uses,
                        recipeData.maxUses, recipeData.experienceReward, recipeData.villagerExperience, recipeData.priceMultiplier);
                List<ItemStack> deserializedIngredients = new ArrayList<>();
                for (Map<String, Object> ingredient : recipeData.ingredients) {
                    deserializedIngredients.add(ItemStack.deserialize(ingredient));
                }
                bukkit.setIngredients(deserializedIngredients);
                bukkitRecipes.add(bukkit);
            }
        }
        villager.setRecipes(bukkitRecipes);
    }

    @Override
    protected void niceName(StringBuilder sb, int start) {
        if (profession != null) {
            sb.insert(start, MiscUtils.niceName(profession)).insert(start + profession.length(), ' ');
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class RecipeData {
        public Map<String, Object> result;
        public List<Map<String, Object>> ingredients = new ArrayList<>();
        public int uses;
        public int maxUses;
        public boolean experienceReward;
        public int villagerExperience;
        public float priceMultiplier;
    }
}
