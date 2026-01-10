package com.petrolpark.core.world.effect.shader.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.core.world.effect.shader.IShaderEffect;
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

public record InitEffectShaderPacket(Holder<MobEffect> mobEffect) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, InitEffectShaderPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT), InitEffectShaderPacket::mobEffect,
        InitEffectShaderPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.INIT_SHADER;
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        MobEffectInstance instance = player.getEffect(mobEffect());
        if (instance == null) return;

        MobEffect mobEffect = instance.getEffect().value();
        if (mobEffect instanceof IShaderEffect shaderEffect) {
            IGameRendererMixin gameRenderer = (IGameRendererMixin) Minecraft.getInstance().gameRenderer;
            gameRenderer.petrolpark$addMobEffectInstanceShader(shaderEffect.getShader(), instance);
        };
    };
};
