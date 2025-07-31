package com.petrolpark.shadereffect.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RemoveAllShadersPacket(boolean filler) implements ClientboundPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, RemoveAllShadersPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RemoveAllShadersPacket::filler,
            RemoveAllShadersPacket::new
    );
    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.REMOVE_ALL_SHADERS;
    }

    @Override
    public void handle(LocalPlayer player) {
        IGameRendererMixin gameRenderer = (( IGameRendererMixin ) Minecraft.getInstance().gameRenderer);
        gameRenderer.cleanShaderEffects();
    }
}
