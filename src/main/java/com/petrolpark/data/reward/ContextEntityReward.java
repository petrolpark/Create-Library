package com.petrolpark.data.reward;

import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.IEntityTarget;
import com.petrolpark.data.reward.entity.IEntityReward;

import java.util.Collections;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getName() {
        return reward.getName();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(target.getReferencedParam());
    };
    
};
