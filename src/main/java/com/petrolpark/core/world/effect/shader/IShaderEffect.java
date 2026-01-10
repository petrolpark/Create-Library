package com.petrolpark.core.world.effect.shader;

import com.petrolpark.core.world.effect.shader.packet.RemoveEffectShaderPacket;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.network.PacketDistributor;

public interface IShaderEffect {

    ResourceLocation getShader();

    default void cleanupShader(ServerPlayer livingEntity, Holder<MobEffect> effect) {
        if (livingEntity != null) {
            PacketDistributor.sendToPlayer(livingEntity, new RemoveEffectShaderPacket(effect));
        };
    };
};
