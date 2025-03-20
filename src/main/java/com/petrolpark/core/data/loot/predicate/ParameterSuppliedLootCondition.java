package com.petrolpark.core.data.loot.predicate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkLootConditionTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public record ParameterSuppliedLootCondition(List<LootContextParam<Object>> params) implements LootItemCondition {

    protected static final Map<ResourceLocation, LootContextParam<Object>> KNOWN_PARAMS = new HashMap<>();

    public static final MapCodec<ParameterSuppliedLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceLocation.CODEC.xmap(KNOWN_PARAMS::get, LootContextParam::getName).listOf().fieldOf("parameters").forGetter(ParameterSuppliedLootCondition::params)
    ).apply(instance, ParameterSuppliedLootCondition::new));

    @SuppressWarnings("unchecked")
    public static final void makeKnown(Collection<LootContextParam<? extends Object>> params) {
        for (LootContextParam<? extends Object> param : params) makeKnown((LootContextParam<Object>) param);
    };

    public static final void makeKnown(LootContextParam<Object> param) {
        KNOWN_PARAMS.put(param.getName(), param);  
    };

    static {
        makeKnown(List.of(
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
        ));
    };

    public static final LootContextParam<?> byName(String name) {
        return KNOWN_PARAMS.get(ResourceLocation.parse(name));
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
    
};
