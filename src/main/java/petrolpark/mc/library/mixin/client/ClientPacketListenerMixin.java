package petrolpark.mc.library.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import petrolpark.mc.library.util.mixininterfaces.IClientboundUpdateMobEffectPacketMixin;
import petrolpark.mc.library.util.mixininterfaces.IMobEffectInstanceMixin;

@Mixin( ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(
            method = "handleUpdateMobEffect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;forceAddEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void petrolpark$initializeTotalDuration(ClientboundUpdateMobEffectPacket packet, CallbackInfo ci, Entity entity, Holder holder, MobEffectInstance mobeffectinstance) {
        (( IMobEffectInstanceMixin ) mobeffectinstance)
                .petrolpark$setTotalDuration(
                        (( IClientboundUpdateMobEffectPacketMixin ) packet)
                                .petrolpark$getTotalDuration()
                );
    }
}
