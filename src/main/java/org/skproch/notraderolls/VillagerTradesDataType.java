package org.skproch.notraderolls;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Type;
import java.util.List;

public class VillagerTradesDataType implements PersistentDataType<String, List<MerchantRecipe>> {
    public static final VillagerTradesDataType Instance = new VillagerTradesDataType();
    private static final Type PARAMETRIZED_TYPE = TypeToken.getParameterized(List.class, MerchantRecipe.class).getType();
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(MerchantRecipe.class, new MerchantRecipeTypeAdapter())
            .create();

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<List<MerchantRecipe>> getComplexType() {
        //noinspection unchecked,rawtypes
        return (Class) List.class;
    }

    @Override
    public String toPrimitive(List<MerchantRecipe> complex, PersistentDataAdapterContext context) {
        return gson.toJson(complex.toArray());
    }

    @Override
    public List<MerchantRecipe> fromPrimitive(String primitive, PersistentDataAdapterContext context) {
        return gson.fromJson(primitive, PARAMETRIZED_TYPE);
    }
}

