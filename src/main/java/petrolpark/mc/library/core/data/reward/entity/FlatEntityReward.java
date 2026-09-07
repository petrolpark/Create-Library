package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
public record FlatEntityReward(Holder<IReward> rewardHolder) implements IEntityReward {

    public static final MapCodec<FlatEntityReward> CODEC = CodecHelper.singleFieldMap(IReward.CODEC, "reward", FlatEntityReward::rewardHolder, FlatEntityReward::new);
    public static final Codec<FlatEntityReward> INLINE_CODEC = IReward.CODEC.xmap(FlatEntityReward::new, FlatEntityReward::rewardHolder);

    @Override
    public IRewardInfo info() {
        return rewardHolder().value().info();
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.ENTITY_FLAT.get();
    };

    @Override
    public boolean reward(Entity entity, LootContext context, float multiplier, boolean simulate) {
        return rewardHolder().value().reward(context, multiplier, simulate);
    };
    
};
