package petrolpark.mc.library.core.data.reward.generator;

import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface IRewardGenerator extends LootContextUser {

    /**
     * Use {@link IRewardGenerator#DIRECT_CODEC} instead.
     */
    static final Codec<IRewardGenerator> TYPED_CODEC = PetrolparkRegistries.REWARD_GENERATOR_TYPES
        .byNameCodec()
        .dispatch(IRewardGenerator::getType, RewardGeneratorType::codec);

    public static final Codec<IRewardGenerator> DIRECT_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, DirectRewardGenerator.INLINE_CODEC));

    public static final Codec<Holder<IRewardGenerator>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.REWARD_GENERATOR, DIRECT_CODEC);
    
    public Stream<Holder<IReward>> generate(LootContext context);

    public RewardGeneratorType getType();
};
