package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.IAbstractReward;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface IEntityReward extends IAbstractReward<IEntityReward.Type> {

    /**
     * Use {@link IEntityReward#DIRECT_CODEC} instead.
     */
    static final Codec<IEntityReward> TYPED_CODEC = PetrolparkRegistries.ENTITY_REWARD_TYPES
        .byNameCodec()
        .dispatch("entity_reward_type", IEntityReward::getType, IEntityReward.Type::entityRewardCodec);

    public static final Codec<IEntityReward> DIRECT_CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);
    public static final Codec<Holder<IEntityReward>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.ENTITY_REWARD, DIRECT_CODEC);
    
    public boolean reward(Entity entity, LootContext context, float multiplier, boolean simulate);

    public interface Type {

        public MapCodec<? extends IEntityReward> entityRewardCodec();
    };
};
