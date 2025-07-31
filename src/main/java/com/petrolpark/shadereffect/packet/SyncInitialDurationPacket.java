package com.petrolpark.shadereffect.packet;

import com.petrolpark.PetrolparkPackets;
import com.petrolpark.util.mixininterfaces.IMobEffectInstanceMixin;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

public record SyncInitialDurationPacket(int initialDuration, String mobEffectID) implements ClientboundPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, SyncInitialDurationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncInitialDurationPacket::initialDuration,
            ByteBufCodecs.STRING_UTF8, SyncInitialDurationPacket::mobEffectID,
            SyncInitialDurationPacket::new
    );
    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.SYNC_INITIAL_DURATION;
    }

    @Override
    public void handle(LocalPlayer localPlayer) {
        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(mobEffectID));
        if ( effect.isEmpty() ) return;

        MobEffectInstance instance = localPlayer.getEffect(effect.get());
        if (instance == null) return;

        (( IMobEffectInstanceMixin ) instance).setInitialDuration(initialDuration);
    }
}
