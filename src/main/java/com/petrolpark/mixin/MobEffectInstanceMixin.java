package com.petrolpark.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.shadereffect.ClientEffectHandler;
import com.petrolpark.shadereffect.IShaderEffect;
import com.petrolpark.shadereffect.packet.SyncInitialDurationPacket;
import com.petrolpark.util.mixininterfaces.IMobEffectInstanceMixin;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstanceMixin, Comparable<MobEffectInstance>{
    @Shadow
    private int duration;

    @Shadow
    private Holder<MobEffect> effect;

    public int initialDuration;
    private boolean sentInitialDuration;

    private boolean shaderInitialized;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("RETURN"))
    private void onInitialize(CallbackInfo ci) {
        initialDuration = duration;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void inTick(LivingEntity pEntity, Runnable pOnExpirationRunnable, CallbackInfoReturnable<Boolean> ci) {
        //Server side
        if (!pEntity.level().isClientSide() && !sentInitialDuration && pEntity instanceof ServerPlayer player && effect.value() instanceof IShaderEffect ) {
            PacketDistributor.sendToPlayer(player,
                    new SyncInitialDurationPacket(this.initialDuration, BuiltInRegistries.MOB_EFFECT.getKey(this.effect.value()).toString()));
            sentInitialDuration = true;
        }

        //Client side
        if (pEntity.level().isClientSide() && !shaderInitialized && effect.value() instanceof IShaderEffect shaderEffect) {
            shaderInitialized = true;
            //Isolation required due to mixins things
            ClientEffectHandler.initShaderEffect((MobEffectInstance)(Object)this, shaderEffect);
        }
    }

    @ModifyReturnValue( method = "save", at = @At("RETURN") )
    private Tag saveData(Tag original) {
        CompoundTag nbt = (( CompoundTag ) original);
        nbt.putInt("initialDuration", this.getInitialDuration());
        return nbt;
    }

    @ModifyReturnValue(method = "load", at = @At("RETURN"))
    private static MobEffectInstance loadData(MobEffectInstance original, CompoundTag nbt) {
        if (original != null) {
            (( IMobEffectInstanceMixin ) original).setInitialDuration(nbt.getInt("initialDuration"));
        }
        return original;
    }

    @Override
    public void updateUniforms() {
        float value = duration >= 0 && initialDuration > 0 ?
                (float) duration / initialDuration :
                0.5f;

        ClientEffectHandler.updateUniforms(value);
    }

    @Override
    public void setInitialDuration(int initialDuration) {
        this.initialDuration = initialDuration;
    }

    @Override
    public int getInitialDuration() {
        return initialDuration;
    }
}
