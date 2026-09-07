package petrolpark.mc.library.core.data.reward.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.registry.PetrolparkRewardGeneratorTypes;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public record RandomRewardGenerator(List<RandomRewardGenerator.Entry> entries, NumberProvider rolls) implements IRewardGenerator {

    public static final MapCodec<RandomRewardGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RandomRewardGenerator.Entry.CODEC.listOf().fieldOf("entries").forGetter(RandomRewardGenerator::entries),
        NumberProviders.CODEC.optionalFieldOf("rolls", ConstantValue.exactly(1f)).forGetter(RandomRewardGenerator::rolls)
    ).apply(instance, RandomRewardGenerator::new));

    @Override
    public Stream<Holder<IReward>> generate(LootContext context) {
        final float rollsFloat = rolls().getFloat(context);
        final int rolls = Mth.floor(rollsFloat) + context.getRandom().nextFloat() < Mth.frac(rollsFloat) ? 1 : 0;
        Stream<Holder<IReward>> rewards = Stream.empty();
        for (int roll = 0; roll < rolls; roll++) {
            float totalWeight = 0f;
            final FloatList weights = new FloatArrayList();
            final List<IRewardGenerator> generators = new ArrayList<>();
            for (RandomRewardGenerator.Entry entry : entries()) {
                final float weight = entry.weight().getFloat(context);
                totalWeight += weight;
                weights.add(weight);
                generators.add(entry.reward().value());
            };
            float rolled = context.getRandom().nextFloat() * totalWeight;
            for (int entry = 0; entry < weights.size(); entry++) {
                if ((rolled -= weights.getFloat(entry)) <= 0f) {
                    rewards = Stream.concat(rewards, generators.get(entry).generate(context));
                    break;
                };
            };
        };
        return rewards;
    };

    @Override
    public RewardGeneratorType getType() {
        return PetrolparkRewardGeneratorTypes.RANDOM.get();
    };

    public record Entry(NumberProvider weight, Holder<IRewardGenerator> reward) implements LootContextUser {

        public static final Codec<RandomRewardGenerator.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProviders.CODEC.optionalFieldOf("weight", ConstantValue.exactly(1f)).forGetter(RandomRewardGenerator.Entry::weight),
            IRewardGenerator.CODEC.fieldOf("reward").forGetter(RandomRewardGenerator.Entry::reward)
        ).apply(instance, RandomRewardGenerator.Entry::new));

        @Override
        public void validate(ValidationContext context) {
            LootContextUser.super.validate(context);
            weight().validate(context.forChild(".weight"));
            DataValidationHelper.validateHolder(reward(), context, "rewardGenerator");
        };
    };
};
