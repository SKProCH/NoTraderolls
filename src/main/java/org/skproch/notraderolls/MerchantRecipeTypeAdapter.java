package org.skproch.notraderolls;

import com.google.gson.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class MerchantRecipeTypeAdapter implements JsonSerializer<MerchantRecipe>, JsonDeserializer<MerchantRecipe> {

    private static final String RESULT_KEY = "result";
    private static final String ITEMS_KEY = "items_yaml";
    private static final String USES_KEY = "uses";
    private static final String MAX_USES_KEY = "maxUses";
    private static final String EXPERIENCE_REWARD_KEY = "experienceReward";
    private static final String VILLAGER_EXPERIENCE_KEY = "villagerExperience";
    private static final String PRICE_MULTIPLIER_KEY = "priceMultiplier";

    @Override
    public JsonElement serialize(MerchantRecipe merchantRecipe, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();


        jsonObject.addProperty(USES_KEY, merchantRecipe.getUses());
        jsonObject.addProperty(MAX_USES_KEY, merchantRecipe.getMaxUses());
        jsonObject.addProperty(EXPERIENCE_REWARD_KEY, merchantRecipe.hasExperienceReward());
        jsonObject.addProperty(VILLAGER_EXPERIENCE_KEY, merchantRecipe.getVillagerExperience());
        jsonObject.addProperty(PRICE_MULTIPLIER_KEY, merchantRecipe.getPriceMultiplier());

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set(RESULT_KEY, merchantRecipe.getResult());

        List<ItemStack> ingredients = merchantRecipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            yamlConfiguration.set(Integer.toString(i), ingredients.get(i));
        }

        jsonObject.addProperty(ITEMS_KEY, yamlConfiguration.saveToString());

        return jsonObject;
    }

    @Override
    public MerchantRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.loadFromString(jsonObject.get(ITEMS_KEY).getAsString());

            ItemStack result = yamlConfiguration.getItemStack(RESULT_KEY);
            int uses = jsonObject.get(USES_KEY).getAsInt();
            int maxUses = jsonObject.get(MAX_USES_KEY).getAsInt();
            boolean experienceReward = jsonObject.get(EXPERIENCE_REWARD_KEY).getAsBoolean();
            int villagerExperience = jsonObject.get(VILLAGER_EXPERIENCE_KEY).getAsInt();
            float priceMultiplier = jsonObject.get(PRICE_MULTIPLIER_KEY).getAsFloat();

            assert result != null;
            MerchantRecipe merchantRecipe = new MerchantRecipe(result, uses, maxUses, experienceReward, villagerExperience, priceMultiplier);

            for (String key : yamlConfiguration.getKeys(false)) {
                if (key.equals(RESULT_KEY)) {
                    continue;
                }

                merchantRecipe.addIngredient(Objects.requireNonNull(yamlConfiguration.getItemStack(key)));
            }

            return merchantRecipe;
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

