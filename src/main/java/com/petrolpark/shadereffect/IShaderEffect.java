package com.petrolpark.shadereffect;

import com.petrolpark.shadereffect.packet.MEIShaderRemovePacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.network.PacketDistributor;

public interface IShaderEffect {
    ResourceLocation getShader();

    default void cleanupShader(ServerPlayer pLivingEntity, IShaderEffect effect) {
        System.out.println("Politely asking to obliterate shader");
        if ( pLivingEntity != null ) {
            PacketDistributor.sendToPlayer(pLivingEntity,
                    new MEIShaderRemovePacket(BuiltInRegistries.MOB_EFFECT.getKey(( MobEffect ) effect).toString()));
        }
    }
}
