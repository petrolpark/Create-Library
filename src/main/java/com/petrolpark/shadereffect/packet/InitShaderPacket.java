package com.petrolpark.shadereffect.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.shadereffect.IShaderEffect;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;
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

public record InitShaderPacket(String mobEffectId) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, InitShaderPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, InitShaderPacket::mobEffectId,
            InitShaderPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.INIT_SHADER;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(mobEffectId));
        if ( effect.isEmpty() ) return;

        MobEffectInstance instance = player.getEffect(effect.get());
        if ( instance == null ) return;

        MobEffect mobEffect = instance.getEffect().value();
        if (mobEffect instanceof IShaderEffect shaderEffect) {
            IGameRendererMixin gameRenderer = ( IGameRendererMixin ) Minecraft.getInstance().gameRenderer;
            gameRenderer.addMobEffectInstanceShader(shaderEffect.getShader(), instance);
        }
    }
}
