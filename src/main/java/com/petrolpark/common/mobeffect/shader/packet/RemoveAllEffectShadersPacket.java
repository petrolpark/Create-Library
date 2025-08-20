package com.petrolpark.common.mobeffect.shader.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record RemoveAllEffectShadersPacket(boolean filler) implements ClientboundPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RemoveAllEffectShadersPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, RemoveAllEffectShadersPacket::filler,
        RemoveAllEffectShadersPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.REMOVE_ALL_EFFECT_SHADERS;
    };

    @Override
    @OnlyIn( Dist.CLIENT)
    public void handle(LocalPlayer player) {
        IGameRendererMixin gameRenderer = (( IGameRendererMixin ) Minecraft.getInstance().gameRenderer);
        gameRenderer.cleanShaderEffects();
    };
};
