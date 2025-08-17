package com.petrolpark.shadereffect.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;
import com.petrolpark.util.mixininterfaces.IMobEffectInstanceMixin;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

public record MEIShaderRemovePacket(String mobEffectID) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, MEIShaderRemovePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, MEIShaderRemovePacket::mobEffectID,
            MEIShaderRemovePacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.MEI_SHADER_REMOVE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer localPlayer) {
        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(mobEffectID));
        if ( effect.isEmpty() ) return;

        MobEffectInstance instance = localPlayer.getEffect(effect.get());
        if ( instance == null ) return;

        IGameRendererMixin gameRenderer = (( IGameRendererMixin ) Minecraft.getInstance().gameRenderer);
        gameRenderer.removeMobEffectInstanceShader((( IMobEffectInstanceMixin ) instance));
    }
}
