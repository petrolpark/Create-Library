package petrolpark.mc.library.core.client.effectShaders.packet;

import petrolpark.mc.library.registry.PetrolparkPackets;
import petrolpark.mc.library.util.mixininterfaces.IMobEffectInstanceMixin;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
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

public record SyncMobEffectTotalDurationPacket(int totalDuration, Holder<MobEffect> mobEffect) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncMobEffectTotalDurationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncMobEffectTotalDurationPacket::totalDuration,
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT), SyncMobEffectTotalDurationPacket::mobEffect,
            SyncMobEffectTotalDurationPacket::new
    );
    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.SYNC_MOB_EFFECT_TOTAL_DURATION;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer localPlayer) {
        MobEffectInstance instance = localPlayer.getEffect(mobEffect());
        if (instance == null) return;
        ((IMobEffectInstanceMixin) instance).petrolpark$setTotalDuration(totalDuration);
    };
};
