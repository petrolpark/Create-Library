package petrolpark.mc.library.core.data.reward.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.data.loot.ILootTableAccessor;
import petrolpark.mc.library.core.data.reward.GiveItemReward;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.registry.PetrolparkRewardGeneratorTypes;

/**
 * <p>{@code petrolpark:loot}</p>
 * 
 * Generates a 
 */
@ParametersAreNonnullByDefault
public record LootTableRewardGenerator(IEntityTarget target, List<LootItemFunction> lateFunctions, Either<ResourceKey<LootTable>, LootTable> lootTable) implements IContextEntityRewardGenerator, ILootTableAccessor {

    public static final MapCodec<LootTableRewardGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            IEntityTarget.STRICT_CODEC.fieldOf("target").forGetter(LootTableRewardGenerator::target),
            ConditionalOps.decodeListWithElementConditions(LootItemFunctions.ROOT_CODEC).optionalFieldOf("late_functions", Collections.emptyList()).forGetter(LootTableRewardGenerator::lateFunctions)
        ).and(ILootTableAccessor.lootTableField(instance).t1())
        .apply(instance, LootTableRewardGenerator::new)
    );

    @Override
    public Stream<Holder<IReward>> generate(LootContext context) {
        final List<Holder<IReward>> rewards = new ArrayList<>();
        getLootTable(context).getRandomItems(context, stack -> rewards.add(Holder.direct(new GiveItemReward(stack, lateFunctions()))));
        return rewards.stream();
    };

    @Override
    public RewardGeneratorType getType() {
        return PetrolparkRewardGeneratorTypes.LOOT_TABLE.get();
    };
    
};
