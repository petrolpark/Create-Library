package com.petrolpark.core.data.reward.generator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.reward.IReward;
import com.petrolpark.registry.PetrolparkRewardGeneratorTypes;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

/**
 * <p>{@code petrolpark:direct}</p>
 * 
 * Generates exactly the given {@link IReward}s.
 * 
 * Arguments:
 * <ul>
 * <li> {@code rewards} - A single or list of {@link IReward}s to "generate"
 * </ul>
 * 
 * @author petrolpark
 */
public record DirectRewardGenerator(List<IReward> rewards) implements IRewardGenerator {

    public static final MapCodec<DirectRewardGenerator> CODEC = CodecHelper.singleFieldMap(IReward.LIST_CODEC, "rewards", DirectRewardGenerator::rewards, DirectRewardGenerator::new);
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
