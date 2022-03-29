package network.darkhelmet.prism.actions.entity;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MerchantSerializer extends EntitySerializer {
    protected List<RecipeData> recipeDataList = null;

    @Override
    protected void serializer(Entity entity) {
        Merchant merchant = (Merchant) entity;
        recipeDataList = new ArrayList<>();
        for (MerchantRecipe recipe : merchant.getRecipes()) {
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
    }

    @Override
    protected void deserializer(Entity entity) {
        if (recipeDataList != null) {
            Merchant merchant = (Merchant) entity;
            List<MerchantRecipe> bukkitRecipes = new ArrayList<>();
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
            merchant.setRecipes(bukkitRecipes);
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
