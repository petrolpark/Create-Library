package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.common.mobeffect.shader.ClientEffectHandler;
import com.petrolpark.common.mobeffect.shader.IShaderEffect;
import com.petrolpark.common.mobeffect.shader.packet.InitEffectShaderPacket;
import com.petrolpark.common.mobeffect.shader.packet.SyncMobEffectTotalDurationPacket;
import com.petrolpark.util.mixininterfaces.IMobEffectInstanceMixin;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@Mixin( MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstanceMixin, Comparable<MobEffectInstance>{
    
    @Shadow
    private int duration;

    @Shadow
    private Holder<MobEffect> effect;

    private int initialDuration;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("RETURN"))
    private void onInitialize(CallbackInfo ignored) {
        initialDuration = duration;
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void inEffectAdded(LivingEntity entity, CallbackInfo ignored) {
        PacketDistributor.sendToPlayer(player, null, null);
        if (entity instanceof ServerPlayer player && effect.value() instanceof IShaderEffect) {
            PacketDistributor.sendToPlayer(player,
                new SyncMobEffectTotalDurationPacket(this.initialDuration, effect),
                new InitEffectShaderPacket(effect));
        };
    };

    @ModifyReturnValue( method = "save", at = @At("RETURN") )
    private Tag saveData(Tag original) {
        CompoundTag nbt = (( CompoundTag ) original);
        nbt.putInt("initialDuration", this.getInitialDuration());
        return nbt;
    }

    @ModifyReturnValue(method = "load", at = @At("RETURN"))
    private static MobEffectInstance loadData(MobEffectInstance original, CompoundTag nbt) {
        if (original != null) {
            (( IMobEffectInstanceMixin ) original).setTotalDuration(nbt.getInt("initialDuration"));
        }
        return original;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateUniforms() {
        float value = duration >= 0 && initialDuration > 0 ?
                (float) duration / initialDuration :
                0.5f;

        ClientEffectHandler.updateUniforms(value);
    }

    @Override
    public void setTotalDuration(int initialDuration) {
        this.initialDuration = initialDuration;
    }

    @Override
    public int getInitialDuration() {
        return initialDuration;
    }
}
