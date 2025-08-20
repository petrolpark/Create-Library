package com.petrolpark.common.mobeffect.shader.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record RemoveEffectShaderPacket(Holder<MobEffect> mobEffect) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveEffectShaderPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT), RemoveEffectShaderPacket::mobEffect,
        RemoveEffectShaderPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.REMOVE_SHADER;
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer localPlayer) {
        MobEffectInstance instance = localPlayer.getEffect(mobEffect());
        if (instance == null) return;

        IGameRendererMixin gameRenderer = ((IGameRendererMixin) Minecraft.getInstance().gameRenderer);
        gameRenderer.petrolpark$removeMobEffectInstanceShader(instance);
    };
};
