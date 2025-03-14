package com.petrolpark.data.reward.generator;

import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.data.reward.IReward;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface IRewardGenerator extends LootContextUser {

    /**
     * Use {@link IRewardGenerator#CODEC} instead.
     */
    public static final Codec<IRewardGenerator> TYPED_CODEC = PetrolparkRegistries.REWARD_GENERATOR_TYPES
        .byNameCodec()
        .dispatch(IRewardGenerator::getType, RewardGeneratorType::codec);

    public static final Codec<IRewardGenerator> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, DirectRewardGenerator.INLINE_CODEC));
    
    public List<IReward> generate(LootContext context);

    public RewardGeneratorType getType();
};
