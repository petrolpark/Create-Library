package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;

@ParametersAreNonnullByDefault
public record ContextEntityReward(IEntityTarget target, IEntityReward reward) implements IWrappedReward<IEntityReward> {

    public static final MapCodec<ContextEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityTarget.STRICT_CODEC.optionalFieldOf("target", IEntityTarget.CONTEXT_THIS).forGetter(ContextEntityReward::target),
        IEntityReward.CODEC.fieldOf("reward").forGetter(ContextEntityReward::reward)
    ).apply(instance, ContextEntityReward::new));

    public static final Codec<ContextEntityReward> INLINE_CODEC = IEntityReward.CODEC.xmap(ContextEntityReward::new, ContextEntityReward::reward);

    public ContextEntityReward(IEntityReward reward) {
        this(IEntityTarget.CONTEXT_THIS, reward);
    };

    @Override
    public boolean reward(LootContext context, float multiplier, boolean simulate) {
        final Entity entity = target.get(context);
        return entity == null ? false : reward().reward(entity, context, multiplier, simulate);
    };

    @Override
    public ContextEntityReward.Info wrapInfo(IRewardInfo info) {
        return new ContextEntityReward.Info(info);
    };

    public static class Info extends WrappedRewardInfo {

        public Info(IRewardInfo wrapped) {
            super(wrapped);
        };

        @Override
        public RewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.CONTEXT_ENTITY.get();
        };

    };

    @Override
    public RewardAndInfoType getType() {
        return PetrolparkRewardTypes.CONTEXT_ENTITY.get();
    };
    
};
