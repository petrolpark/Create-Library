package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.ITypedReward;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface IEntityReward extends ITypedReward<EntityRewardType> {

    /**
     * Use {@link IEntityReward#CODEC} instead.
     */
    static final Codec<IEntityReward> TYPED_CODEC = PetrolparkRegistries.ENTITY_REWARD_TYPES
        .byNameCodec()
        .dispatch(IEntityReward::getType, EntityRewardType::codec);

    public static final Codec<IEntityReward> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC); //TODO inline/default
    
    public void reward(Entity entity, LootContext context, float multiplier);
};
