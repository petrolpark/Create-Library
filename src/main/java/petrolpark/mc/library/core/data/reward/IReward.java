package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface IReward extends IAbstractReward<IReward.Type> {

    /**
     * Use {@link IReward#DIRECT_CODEC} instead.
     */
    @ApiStatus.Internal
    public static final Codec<IReward> TYPED_CODEC = PetrolparkRegistries.REWARD_TYPES
        .byNameCodec()
        .dispatch("reward_type", IReward::getType, IReward.Type::rewardCodec);

    public static final Codec<IReward> DIRECT_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, ContextEntityReward.INLINE_CODEC));

    public static final Codec<Holder<IReward>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.REWARD, DIRECT_CODEC);
    public static final Codec<HolderSet<IReward>> SET_CODEC = HolderSetCodec.create(PetrolparkRegistries.Keys.REWARD, CODEC, false);
    //TODO codec validations

    public boolean reward(LootContext context, float multiplier, boolean simulate);

    public interface Type {

        public MapCodec<? extends IReward> rewardCodec();
    };
};
