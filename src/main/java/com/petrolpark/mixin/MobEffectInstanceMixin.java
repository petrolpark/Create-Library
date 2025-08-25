package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstanceMixin, Comparable<MobEffectInstance> {
    
    private static final String TOTAL_DURATION_TAG_KEY = "TotalDuration";

    @Shadow
    private int duration;

    @Final
    @Shadow
    private Holder<MobEffect> effect;

    @Shadow
    public abstract boolean isInfiniteDuration();

    @Unique
    private int petrolpark$totalDuration;

    @Inject(
        method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V",
        at = @At("RETURN")
    )
    private void onInitialize(CallbackInfo ignored) {
        petrolpark$totalDuration = duration;
    };

    /**
     * Sync the total duration of the Effect to the Player with the Effect, and if it is a {@link IShaderEffect}, send the packet to initialize the shader.
     * @param entity
     * @param ignored
     */
    @Inject(
        method = "onEffectAdded",
        at = @At("TAIL")
    )
    private void inEffectAdded(LivingEntity entity, CallbackInfo ignored) {
        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncMobEffectTotalDurationPacket(petrolpark$totalDuration, effect));
            if (effect.value() instanceof IShaderEffect) PacketDistributor.sendToPlayer(player, new InitEffectShaderPacket(effect));
        };
    };

    @ModifyReturnValue(
        method = "save",
        at = @At("RETURN")
    )
    private Tag modifySaveData(Tag original) {
        CompoundTag nbt = (CompoundTag)original;
        nbt.putInt(TOTAL_DURATION_TAG_KEY, this.petrolpark$getTotalDuration());
        return nbt;
    };

    @ModifyReturnValue(
        method = "load",
        at = @At("RETURN")
    )
    private static MobEffectInstance modifyLoadData(MobEffectInstance original, CompoundTag nbt) {
        if (original != null) {
            ((IMobEffectInstanceMixin)original).petrolpark$setTotalDuration(nbt.getInt(TOTAL_DURATION_TAG_KEY));
        };
        return original;
    };

    @Inject(
        method = "update",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void inUpdate(MobEffectInstance other, CallbackInfoReturnable<Boolean> cir, boolean flag) {
        if (flag) {
            if (isInfiniteDuration()) return;
            if (other.isInfiniteDuration()) petrolpark$setTotalDuration(MobEffectInstance.INFINITE_DURATION);
            else {
                petrolpark$setTotalDuration(petrolpark$getTotalDuration() - duration + ((IMobEffectInstanceMixin)(other)).petrolpark$getTotalDuration());  
            };
        };
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void petrolpark$updateUniforms() {
        float value = duration >= 0 && petrolpark$totalDuration > 0 ? (float) duration / petrolpark$totalDuration : 0.5f;
        ClientEffectHandler.updateUniforms(value);
    };

    @Override
    public void petrolpark$setTotalDuration(int totalDuration) {
        this.petrolpark$totalDuration = totalDuration;
    };

    @Override
    public int petrolpark$getTotalDuration() {
        return petrolpark$totalDuration;
    };
};
