package com.petrolpark.data.loot.predicate;

import java.util.Map;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.data.loot.PetrolparkLootConditionTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ParameterSuppliedLootCondition implements LootItemCondition {

    protected static final Map<ResourceLocation, LootContextParam<?>> KNOWN_PARAMS = new HashMap<>();

    public static final void makeKnown(LootContextParam<?> ...params) {
        for (LootContextParam<?> param : params) makeKnown(param);
    };

    public static final void makeKnown(LootContextParam<?> param) {
        KNOWN_PARAMS.put(param.getName(), param);  
    };

    static {
        makeKnown(
            LootContextParams.BLOCK_ENTITY,
            LootContextParams.BLOCK_STATE,
            LootContextParams.DAMAGE_SOURCE,
            LootContextParams.DIRECT_ATTACKING_ENTITY,
            LootContextParams.EXPLOSION_RADIUS,
            LootContextParams.ATTACKING_ENTITY,
            LootContextParams.LAST_DAMAGE_PLAYER,
            LootContextParams.ORIGIN,
            LootContextParams.THIS_ENTITY,
            LootContextParams.TOOL
        );
    };

    public final LootContextParam<?>[] params;

    public ParameterSuppliedLootCondition(LootContextParam<?>[] params) {
        this.params = params;
    };

    @Override
    public boolean test(LootContext context) {
        for (LootContextParam<?> param : params) if (!context.hasParam(param)) return false;
        return true;
    };

    @Override
    public LootItemConditionType getType() {
        return PetrolparkLootConditionTypes.PARAMETERS_SUPPLIED.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ParameterSuppliedLootCondition> {

        @Override
        public void serialize(JsonObject json, ParameterSuppliedLootCondition value, JsonSerializationContext serializationContext) {
            JsonArray jsonArray = new JsonArray(value.params.length);
            for (LootContextParam<?> param : value.params) jsonArray.add(param.getName().toString());
            json.add("parameters", jsonArray);
        };

        @Override
        public ParameterSuppliedLootCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, "parameters");
            LootContextParam<?>[] params = new LootContextParam[jsonArray.size()];
            int i = 0;
            for (JsonElement element : jsonArray) {
                String name = GsonHelper.convertToString(element, "parameter");
                LootContextParam<?> param = KNOWN_PARAMS.get(ResourceLocation.fromNamespaceAndPath(name));
                if (param == null) throw new JsonSyntaxException("Unknown Loot Context Paramater: "+name);
                params[i] = param;
                i++;
            };
            return new ParameterSuppliedLootCondition(params);
        };

    };
    
};
