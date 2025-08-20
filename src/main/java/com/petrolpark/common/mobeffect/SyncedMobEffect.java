package com.petrolpark.common.mobeffect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class SyncedMobEffect extends SimpleMobEffect {
    
    public SyncedMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    };

    @SubscribeEvent
    public static final void onEffectAdded(MobEffectEvent.Added event) {

        // Sync Effect total duration to effect recipient

        // Sync SyncedMobEffects to all Players tracking the Mob, not just the mob (Player) itself
        if (event.getEffectInstance().getEffect().value() instanceof SyncedMobEffect
            && event.getEntity().level().getChunkSource() instanceof ServerChunkCache chunkCache
        ) chunkCache.broadcast(event.getEntity(), new ClientboundUpdateMobEffectPacket(event.getEntity().getId(), event.getEffectInstance(), false));
    };

    @SubscribeEvent
    public static final void onEffectRemoved(MobEffectEvent.Remove event) {

        // Sync SyncedMobEffects to all Players tracking the Mob, not just the mob (Player) itself
        if (event.getEffect().value() instanceof SyncedMobEffect
            && event.getEntity().level().getChunkSource() instanceof ServerChunkCache chunkCache
        ) chunkCache.broadcast(event.getEntity(), new ClientboundRemoveMobEffectPacket(event.getEntity().getId(), event.getEffect()));
    };

    @SubscribeEvent
    public static final void onStartTrackingEntity(PlayerEvent.StartTracking event) {

        // Sync SyncedMobEffects to all Players tracking the Mob, not just the mob (Player) itself
        if (event.getTarget() instanceof LivingEntity entity && event.getEntity() instanceof ServerPlayer player) {
            final List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
            for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
                if (effectInstance.getEffect().value() instanceof SyncedMobEffect) {
                    packets.add(new ClientboundUpdateMobEffectPacket(entity.getId(), effectInstance, false));
                };
            };
            if (packets.size() == 1) {
                player.connection.send(packets.get(0));
            } else if (packets.size() > 0) {
                player.connection.send(new ClientboundBundlePacket(packets));
            };
        };
    };
};
