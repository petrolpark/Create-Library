package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public record ContextEntityReward(IEntityTarget target, IEntityReward reward) implements IReward {

    public static final MapCodec<ContextEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityTarget.CODEC.optionalFieldOf("target", IEntityTarget.CONTEXT_THIS).forGetter(ContextEntityReward::target),
        IEntityReward.CODEC.fieldOf("reward").forGetter(ContextEntityReward::reward)
    ).apply(instance, ContextEntityReward::new));

    public static final Codec<ContextEntityReward> INLINE_CODEC = IEntityReward.CODEC.xmap(ContextEntityReward::new, ContextEntityReward::reward);

    public ContextEntityReward(IEntityReward reward) {
        this(IEntityTarget.CONTEXT_THIS, reward);
    };

    @Override
    public final void reward(LootContext context, float multiplier) {
        Entity entity = target.get(context);
        if (entity != null) reward.reward(entity, context, multiplier);
    };

    @Override
    public RewardType getType() {
        return PetrolparkRewardTypes.CONTEXT_ENTITY.get();
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics graphics) {
        reward.render(graphics);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder
            .add(translateSimple(target.getName()))
            .indent();
        reward().addToDescription(builder);
        builder.unindent();
    };

    @Override
    public void validate(ValidationContext context) {
        IReward.super.validate(context);
        reward().validate(context.forChild(".entity_reward"));
    };
    
};
