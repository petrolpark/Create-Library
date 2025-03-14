package com.petrolpark.data.reward.generator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.data.reward.IReward;
import com.petrolpark.data.reward.PetrolparkRewardGeneratorTypes;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record DirectRewardGenerator(List<IReward> rewards) implements IRewardGenerator {

    public static final MapCodec<DirectRewardGenerator> CODEC = NetworkHelper.singleFieldMapCodec(IReward.LIST_CODEC, "rewards", DirectRewardGenerator::rewards, DirectRewardGenerator::new);
    public static final Codec<DirectRewardGenerator> INLINE_CODEC = IReward.LIST_CODEC.xmap(DirectRewardGenerator::new, DirectRewardGenerator::rewards);

    @Override
    public List<IReward> generate(LootContext context) {
        return rewards;
    };

    @Override
    public RewardGeneratorType getType() {
        return PetrolparkRewardGeneratorTypes.DIRECT.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return rewards.stream().flatMap(p -> p.getReferencedContextParams().stream()).collect(Collectors.toSet());
    };
    
};
