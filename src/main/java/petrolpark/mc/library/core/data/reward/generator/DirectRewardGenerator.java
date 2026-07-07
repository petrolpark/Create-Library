package petrolpark.mc.library.core.data.reward.generator;

import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.registry.PetrolparkRewardGeneratorTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

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
@ParametersAreNonnullByDefault
public record DirectRewardGenerator(HolderSet<IReward> rewards) implements IRewardGenerator {

    public static final MapCodec<DirectRewardGenerator> CODEC = CodecHelper.singleFieldMap(IReward.SET_CODEC, "rewards", DirectRewardGenerator::rewards, DirectRewardGenerator::new);
    public static final Codec<DirectRewardGenerator> INLINE_CODEC = IReward.SET_CODEC.xmap(DirectRewardGenerator::new, DirectRewardGenerator::rewards);

    @Override
    public Stream<Holder<IReward>> generate(LootContext context) {
        return rewards().stream();
    };

    @Override
    public RewardGeneratorType getType() {
        return PetrolparkRewardGeneratorTypes.DIRECT.get();
    };
    
};
