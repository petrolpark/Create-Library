package com.petrolpark.data.reward.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.IEntityTarget;
import com.petrolpark.data.loot.ILootTableAccessor;
import com.petrolpark.data.reward.ContextEntityReward;
import com.petrolpark.data.reward.IReward;
import com.petrolpark.data.reward.PetrolparkRewardGeneratorTypes;
import com.petrolpark.data.reward.entity.GiveItemEntityReward;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

public record LootTableRewardGenerator(IEntityTarget target, List<LootItemFunction> lateFunctions, Either<ResourceKey<LootTable>, LootTable> lootTable) implements IContextEntityRewardGenerator, ILootTableAccessor {

    public static final MapCodec<LootTableRewardGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            IEntityTarget.CODEC.fieldOf("target").forGetter(LootTableRewardGenerator::target),
            ConditionalOps.decodeListWithElementConditions(LootItemFunctions.ROOT_CODEC).optionalFieldOf("lateFunctions", Collections.emptyList()).forGetter(LootTableRewardGenerator::lateFunctions)
        ).and(ILootTableAccessor.lootTableField(instance).t1())
        .apply(instance, LootTableRewardGenerator::new)
    );

    @Override
    public List<IReward> generate(LootContext context) {
        List<IReward> rewards = new ArrayList<>();
        getLootTable(context).getRandomItems(context, stack -> rewards.add(new ContextEntityReward(target, new GiveItemEntityReward(stack, lateFunctions))));
        return rewards;
    };

    @Override
    public RewardGeneratorType getType() {
        return PetrolparkRewardGeneratorTypes.LOOT_TABLE.get();
    };
    
};
