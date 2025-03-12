package com.petrolpark.data.reward.entity;

import com.mojang.serialization.Codec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IEntityReward extends LootContextUser {

    public static final Codec<IEntityReward> CODEC = null; //TODO
    
    public void reward(Entity entity, LootContext context, float multiplier);

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics);

    @OnlyIn(Dist.CLIENT)
    public Component getName();

    static class TypedCodec {

    };
};
