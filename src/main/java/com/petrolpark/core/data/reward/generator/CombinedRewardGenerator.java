package com.petrolpark.core.data.reward.generator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.reward.IReward;
import com.petrolpark.registry.PetrolparkRewardGeneratorTypes;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record CombinedRewardGenerator(List<IRewardGenerator> values) implements IRewardGenerator {

    public static final MapCodec<CombinedRewardGenerator> CODEC = CodecHelper.singleFieldMap(IRewardGenerator.CODEC.listOf(), "values", CombinedRewardGenerator::values, CombinedRewardGenerator::new);

    @Override
    public List<IReward> generate(LootContext context) {
        return values.stream().map(rg -> rg.generate(context)).flatMap(List::stream).toList();
    };

    @Override
    public RewardGeneratorType getType() {
        return PetrolparkRewardGeneratorTypes.COMBINED.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return values.stream().map(IRewardGenerator::getReferencedContextParams).flatMap(Set::stream).collect(Collectors.toSet());
    };
    
};
