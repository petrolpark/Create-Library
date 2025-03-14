package com.petrolpark.data.reward;

import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.world.level.storage.loot.LootContext;

public interface IReward extends ITypedReward<RewardType> {

    /**
     * Use {@link IReward#CODEC} instead.
     */
    public static final Codec<IReward> TYPED_CODEC = PetrolparkRegistries.REWARD_TYPES
        .byNameCodec()
        .dispatch(IReward::getType, RewardType::codec);

    public static final Codec<IReward> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, ContextEntityReward.INLINE_CODEC)); //TODO add default

    public static final Codec<List<IReward>> LIST_CODEC = NetworkHelper.listOrSingle(CODEC);

    public void reward(LootContext context, float multiplier);
    
    public RewardType getType();
};
