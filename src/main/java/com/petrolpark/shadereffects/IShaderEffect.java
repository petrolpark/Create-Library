package com.petrolpark.shadereffects;

import com.petrolpark.network.PetrolparkMessages;
import com.petrolpark.shadereffects.packet.MEIShaderRemovePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public interface IShaderEffect {
    ResourceLocation getShader();

    default void cleanupShader(LivingEntity pLivingEntity, MobEffect effect) {
        if (pLivingEntity instanceof ServerPlayer ) {
            PetrolparkMessages.sendToClient(new MEIShaderRemovePacket(effect), (( ServerPlayer ) pLivingEntity));
        }
    }
}
