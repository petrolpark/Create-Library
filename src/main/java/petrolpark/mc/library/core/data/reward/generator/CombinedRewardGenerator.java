package petrolpark.mc.library.core.data.reward.generator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.registry.PetrolparkRewardGeneratorTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
public record CombinedRewardGenerator(List<IRewardGenerator> children) implements IRewardGenerator {

    public static final MapCodec<CombinedRewardGenerator> CODEC = CodecHelper.singleFieldMap(IRewardGenerator.DIRECT_CODEC.listOf(), "values", CombinedRewardGenerator::children, CombinedRewardGenerator::new);

    @Override
    public Stream<Holder<IReward>> generate(LootContext context) {
        return children().stream().flatMap(generator -> generator.generate(context));
    };

    @Override
    public RewardGeneratorType getType() {
        return PetrolparkRewardGeneratorTypes.COMBINED.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return children().stream().map(IRewardGenerator::getReferencedContextParams).flatMap(Set::stream).collect(Collectors.toSet());
    };

    @Override
    public void validate(ValidationContext context) {
        IRewardGenerator.super.validate(context);
        for (int i = 0; i < children().size(); i++) children().get(i).validate(context.forChild(".child[" + i + "]"));
    };
    
};
