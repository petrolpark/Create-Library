package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public record ContextEntityReward(IEntityTarget target, Holder<IEntityReward> rewardHolder) implements IWrappedReward<IEntityReward> {

    public static final MapCodec<ContextEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityTarget.STRICT_CODEC.optionalFieldOf("target", IEntityTarget.CONTEXT_THIS).forGetter(ContextEntityReward::target),
        IEntityReward.CODEC.fieldOf("reward").forGetter(ContextEntityReward::rewardHolder)
    ).apply(instance, ContextEntityReward::new));

    public static final Codec<ContextEntityReward> INLINE_CODEC = IEntityReward.CODEC.xmap(ContextEntityReward::new, ContextEntityReward::rewardHolder);

    public ContextEntityReward(Holder<IEntityReward> reward) {
        this(IEntityTarget.CONTEXT_THIS, reward);
    };

    @Override
    public boolean reward(LootContext context, float multiplier, boolean simulate) {
        final Entity entity = target.get(context);
        return entity == null ? false : rewardHolder().value().reward(entity, context, multiplier, simulate);
    };

    @Override
    public ContextEntityReward.Info wrapInfo(IRewardInfo info) {
        return new ContextEntityReward.Info(info, target());
    };

    public record Info(IRewardInfo wrapped, IEntityTarget target) implements INamedRewardInfo {

        public static final MapCodec<ContextEntityReward.Info> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IRewardInfo.DIRECT_CODEC.fieldOf("wrapped").forGetter(ContextEntityReward.Info::wrapped),
            IEntityTarget.CODEC.fieldOf("target").forGetter(ContextEntityReward.Info::target)
        ).apply(instance, ContextEntityReward.Info::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ContextEntityReward.Info> STREAM_CODEC = StreamCodec.composite(
            IRewardInfo.STREAM_CODEC, ContextEntityReward.Info::wrapped,
            IEntityTarget.STREAM_CODEC, ContextEntityReward.Info::target,
            ContextEntityReward.Info::new
        );

        @Override
        public RewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.CONTEXT_ENTITY.get();
        };

        @Override
        public void render(GuiGraphics graphics) {
            wrapped().render(graphics);
        };

        @Override
        public void addToDescription(IndentedTooltipBuilder builder) {
            builder.add(translateSimple(target().getName())).indent();
            wrapped().addToDescription(builder);
            builder.unindent();
        };

    };

    @Override
    public RewardAndInfoType getType() {
        return PetrolparkRewardTypes.CONTEXT_ENTITY.get();
    };
    
};
